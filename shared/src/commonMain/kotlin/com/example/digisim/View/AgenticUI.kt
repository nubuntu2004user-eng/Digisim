package com.example.digisim.View

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.digisim.LlmClients.OpenAiClient
import com.example.digisim.Persistance.persistanceRepository.saveState
import com.example.digisim.SettingsViewModel
import com.example.digisim.UiUtils.AppState
import com.example.digisim.UiUtils.TranslationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun agenticUI(
    llmClient: OpenAiClient,
    scope: CoroutineScope,
    appState: AppState,
    translationViewModel: TranslationViewModel
) {
    var output by remember { mutableStateOf("") }
    var input by remember { mutableStateOf("") }
    val lang = appState.currentLanguage
    val settings = SettingsViewModel.default
    val isAnswering by llmClient.currentlyAnswering

    val header = if (appState.openAiSettings)
        translationViewModel.getString("AI Provider Settings", lang)
    else
        translationViewModel.getString("01 / CIRCUIT ASSISTANT", lang)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(settings.panelBackgroundColor)
    ) {
        SwissSectionHeader(
            indexAndTitle = header,
            settings = settings,
            trailingContent = {
                SwissButton(
                    text = if (appState.openAiSettings)
                        translationViewModel.getString("Chat", lang)
                    else
                        translationViewModel.getString("Settings", lang),
                    onClick = { appState.openAiSettings = !appState.openAiSettings },
                    isActive = appState.openAiSettings,
                    compact = true,
                    settings = settings,
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                )
            }
        )

        if (!appState.openAiSettings) {
            // Message Log Area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (output.isBlank() && !isAnswering) {
                    // Explicit Empty State (R-27)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(settings.elevatedSurfaceColor, RoundedCornerShape(2.dp))
                            .border(1.dp, settings.borderColor, RoundedCornerShape(2.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = translationViewModel.getString("Circuit Assistant", lang),
                            color = settings.textColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = translationViewModel.getString(
                                "No messages yet. Ask the assistant to build, inspect, or debug a digital circuit on the canvas.",
                                lang
                            ),
                            color = settings.secondaryTextColor,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(settings.elevatedSurfaceColor, RoundedCornerShape(2.dp))
                            .border(1.dp, settings.borderColor, RoundedCornerShape(2.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = output.trimEnd(),
                            color = settings.textColor,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                }

                // Explicit Loading State (R-27)
                if (isAnswering) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(settings.elevatedSurfaceColor, RoundedCornerShape(2.dp))
                            .border(1.dp, settings.accentTaupe, RoundedCornerShape(2.dp))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = translationViewModel.getString("Processing circuit request...", lang),
                            color = settings.textColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace
                        )
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth().height(2.dp),
                            color = settings.accentTaupe,
                            trackColor = settings.borderColor
                        )
                    }
                }
            }

            SwissHorizontalDivider(settings)

            // Prompt Input Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(settings.panelBackgroundColor)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SwissTextField(
                    value = input,
                    onValueChange = { input = it },
                    placeholder = translationViewModel.getString(
                        "Describe circuit changes or ask a question...",
                        lang
                    ),
                    singleLine = false,
                    settings = settings,
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
                            enabled = !isAnswering && input.isNotBlank()
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = translationViewModel.getString("Send", lang),
                                tint = if (!isAnswering && input.isNotBlank()) settings.textColor else settings.secondaryTextColor
                            )
                        }
                    }
                )

                SwissButton(
                    text = translationViewModel.getString("Return to Library", lang),
                    onClick = { appState.agenticMode = false },
                    modifier = Modifier.fillMaxWidth(),
                    compact = true,
                    settings = settings
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                aiSettings(appState = appState, translationViewModel = translationViewModel)

                SwissButton(
                    text = translationViewModel.getString("Save and exit", lang),
                    onClick = {
                        scope.launch {
                            llmClient.applySettings(appState = appState)
                            saveState(appState)
                            appState.openAiSettings = false
                        }
                    },
                    isPrimary = true,
                    modifier = Modifier.fillMaxWidth(),
                    settings = settings
                )
            }
        }
    }
}
