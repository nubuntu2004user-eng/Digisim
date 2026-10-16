package com.example.digisim.LlmClients

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.response.Response
import com.aallam.openai.api.response.ResponseInput
import com.aallam.openai.api.response.ResponseRequest
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIHost
import kotlin.time.Duration.Companion.seconds

class OpenAiClient {
   private val openAi = OpenAI(
       token = "none",
        timeout = Timeout(socket = 300.seconds),
       host = OpenAIHost(baseUrl = "http://127.0.0.1:9931")
   )
    val messages = mutableListOf(ChatMessage(
        role = ChatRole.System,
        content = "You are a helpful assistant"
    ))

    private val model = ModelId("gemma-4-E2B-it-Q4_K_S.gguf")
    suspend fun prompt(input: String): Response {
        val response = ResponseRequest(
            model = model,
            input = ResponseInput(input)
        )
        return openAi.response(response)
    }
    suspend fun chatCompletion(): ChatCompletion {
        val request = ChatCompletionRequest(
            model = model,
            messages = messages
        )
    return openAi.chatCompletion(request)
    }
}