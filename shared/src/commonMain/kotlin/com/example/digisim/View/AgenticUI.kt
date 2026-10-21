package com.example.digisim.View

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.sp
import com.example.digisim.LlmClients.OpenAiClient
import com.example.digisim.Persistance.persistanceRepository.saveState
import com.example.digisim.UiUtils.AppState
import com.example.digisim.UiUtils.TranslationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun agenticUI(llmClient: OpenAiClient, scope: CoroutineScope, appState: AppState, translationViewModel: TranslationViewModel) {
    var output by remember { mutableStateOf("") }
    var input by remember { mutableStateOf("") }
    val lang = appState.currentLanguage
    val header = if (appState.openAiSettings)
        translationViewModel.getString("AI provider\nsettings", lang)
    else
        translationViewModel.getString("Chat", lang)

    Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.8f).padding(10.dp).verticalScroll(rememberScrollState())) {
        Row {
            Text(header, fontSize = 24.sp, modifier = Modifier.padding(8.dp))
            Spacer(modifier = Modifier.fillMaxWidth(0.7f))
            IconButton(onClick = { appState.openAiSettings = !appState.openAiSettings }) {
                Icon(imageVector = Icons.Filled.Settings, contentDescription = null)
            }
        }
        if (!appState.openAiSettings) {
            Spacer(modifier = Modifier.fillMaxHeight(0.04f))
            Text(output)
            Spacer(modifier = Modifier.fillMaxHeight(0.04f))
        } else {
            aiSettings(appState = appState, translationViewModel = translationViewModel)
        }
    }
    Column(modifier = Modifier.defaultMinSize().padding(10.dp)) {
        if (!appState.openAiSettings) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.padding(14.dp).fillMaxWidth(),
                shape = RoundedCornerShape(50),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            val prompt = input.trim()
                            if (prompt.isEmpty()) return@IconButton
                            input = ""
                            scope.launch {
                                llmClient.currentlyAnswering.value = true
                                output += translationViewModel.getString("You:\n", lang) + "$prompt\n\n"
                                try {
                                    val reply = llmClient.sendMessage(prompt)
                                    output += translationViewModel.getString("Llm:\n", lang) + "$reply\n\n"
                                } catch (e: Exception) {
                                    output += translationViewModel.getString("Error: ", lang) + "${e.message}\n\n"
                                } finally {
                                    llmClient.currentlyAnswering.value = false
                                }
                            }
                        },
                        enabled = !llmClient.currentlyAnswering.value
                    ) {
                        Icon(imageVector = Icons.Filled.Check, contentDescription = null)
                    }
                }
            )
        } else {
            Button(onClick = {
                scope.launch {
                    llmClient.applySettings(appState = appState)
                    saveState(appState)
                    appState.openAiSettings = false
                }
            }) {
                Text(translationViewModel.getString("Save and exit", lang))
            }
        }
    }
}
