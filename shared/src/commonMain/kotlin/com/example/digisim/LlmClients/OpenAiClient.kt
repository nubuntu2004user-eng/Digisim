package com.example.digisim.LlmClients

import androidx.compose.runtime.mutableStateOf
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.chat.ToolChoice
import com.aallam.openai.api.core.Parameters
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.response.Response
import com.aallam.openai.api.response.ResponseInput
import com.aallam.openai.api.response.ResponseRequest
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIHost
import com.example.digisim.mcpClient.mcpClient
import com.aallam.openai.api.chat.ToolCall
import com.aallam.openai.api.model.Model
import com.example.digisim.UiUtils.AppState
import io.modelcontextprotocol.kotlin.sdk.types.TextContent
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlin.time.Duration.Companion.seconds

class OpenAiClient(private val mcpClient: mcpClient) {
    private var openAi = OpenAI(
        token = "",
        timeout = Timeout(socket = 300.seconds),
        host = OpenAIHost(baseUrl = "http://127.0.0.1:9931")
    )
    private var model = ModelId("")

    val currentlyAnswering = mutableStateOf(false)
    val messages = mutableListOf(ChatMessage(
        role = ChatRole.System,
        content = "role: act as a senior digital circuit designer and help user to build the circuit he asks for"
    ))

    fun applySettings( appState: AppState) {
        if (appState.isUsingLocalModel){
        openAi = OpenAI(
            token = appState.apiKey.ifBlank { "none" },
            timeout = Timeout(socket = 300.seconds),
            host = OpenAIHost(baseUrl = appState.baseUrl)
        )}else{
            openAi = OpenAI(
                token = appState.apiKey.ifBlank { "none" },
                timeout = Timeout(socket = 300.seconds)
            )
        }
        model = ModelId(appState.currentModel)
    }
    suspend fun prompt(input: String): Response {
        val response = ResponseRequest(
            model = model,
            input = ResponseInput(input)
        )
        return openAi.response(response)
    }
    suspend fun chatCompletion(): ChatCompletion {
        val json = Json { encodeDefaults = true }
        val tools = mcpClient.listTools().map { t ->
            Tool.function(
                name = t.name,
                description = t.description,
                parameters = Parameters.fromJsonString(
                    json.encodeToString(t.inputSchema)
                )
            )
        }
        val request = ChatCompletionRequest(
            model = model,
            messages = messages,
            tools = tools,
            toolChoice = ToolChoice.Auto
        )
        return openAi.chatCompletion(request)
    }

    suspend fun sendMessage(userText: String): String {
        messages.add(ChatMessage(role = ChatRole.User, content = userText))
        var response = chatCompletion()
        var currentMessage = response.choices.firstOrNull()?.message ?: return "No response"
        messages.add(currentMessage)

        val allToolResults = mutableListOf<String>()
        var round = 0
        val maxToolRounds = 10

        while (!currentMessage.toolCalls.isNullOrEmpty() && round < maxToolRounds) {
            round++
            val toolCalls = currentMessage.toolCalls.orEmpty()
            for (toolCall in toolCalls) {
                if (toolCall is ToolCall.Function) {
                    val name = toolCall.function.name
                    val argsStr = toolCall.function.arguments
                    val jsonArgs = try {
                        Json.parseToJsonElement(argsStr).jsonObject
                    } catch (e: Exception) {
                        null
                    }
                    val result = mcpClient.callTool(name, jsonArgs)
                    val resultText = result.content
                        .filterIsInstance<io.modelcontextprotocol.kotlin.sdk.types.TextContent>()
                        .joinToString("\n") { it.text }

                    allToolResults.add(resultText)

                    messages.add(
                        ChatMessage(
                            role = ChatRole.Tool,
                            toolCallId = toolCall.id,
                            name = toolCall.function.name,
                            content = resultText
                        )
                    )
                }
            }

            response = chatCompletion()
            val followUpMessage = response.choices.firstOrNull()?.message ?: break
            messages.add(followUpMessage)
            currentMessage = followUpMessage
        }

        return currentMessage.content ?: allToolResults.joinToString("\n")
    }
}