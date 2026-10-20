package com.example.digisim.View

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.example.digisim.LlmClients.OpenAiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun agenticUI(llmClient: OpenAiClient , scope: CoroutineScope){
    var output by remember{ mutableStateOf("")}
    var input by remember{ mutableStateOf("")}
    Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.8f).padding(10.dp).verticalScroll(rememberScrollState())) {
        Text("Chat")
        Spacer(modifier = Modifier.fillMaxHeight(0.04f))
        Text(output)
        Spacer(modifier = Modifier.fillMaxHeight(0.04f))
    }
    Column(modifier = Modifier.defaultMinSize().padding(10.dp)) {
        OutlinedTextField(value = input , onValueChange = {input = it} ,
            trailingIcon = {
                IconButton(onClick = {
                    val prompt = input.trim()
                    if (prompt.isEmpty()) return@IconButton
                    input = ""
                    scope.launch {
                        llmClient.currentlyAnswering.value = true
                        output += "You:\n$prompt\n\n"
                        try {
                            val reply = llmClient.sendMessage(prompt)
                            output += "Llm:\n$reply\n\n"
                        } catch (e: Exception) {
                            output += "Error: ${e.message}\n\n"
                        } finally {
                            llmClient.currentlyAnswering.value = false
                        }
                    }
                },
                    enabled = !llmClient.currentlyAnswering.value
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null
                    )
                }
            }
            )
    }
}