package com.example.digisim.View

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.digisim.Persistance.persistanceRepository.saveState
import com.example.digisim.SettingsViewModel
import com.example.digisim.UiUtils.AppState
import com.example.digisim.UiUtils.Language
import com.example.digisim.UiUtils.TranslationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun settingsPanel(
    settingsViewModel: SettingsViewModel,
    appState: AppState,
    translationViewModel: TranslationViewModel,
    scope: CoroutineScope
) {
    val lang = appState.currentLanguage

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(settingsViewModel.panelBackgroundColor)
    ) {
        SwissSectionHeader(
            indexAndTitle = translationViewModel.getString("01 / SYSTEM SETTINGS", lang),
            settings = settingsViewModel
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Language Selection Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(settingsViewModel.elevatedSurfaceColor, RoundedCornerShape(2.dp))
                    .border(1.dp, settingsViewModel.borderColor, RoundedCornerShape(2.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = translationViewModel.getString("Language:", lang).uppercase(),
                    color = settingsViewModel.secondaryTextColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.6.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SwissButton(
                        text = translationViewModel.getString("EN", lang),
                        onClick = { appState.currentLanguage = Language.ENGLISH },
                        isActive = appState.currentLanguage == Language.ENGLISH,
                        modifier = Modifier.weight(1f),
                        settings = settingsViewModel
                    )
                    SwissButton(
                        text = translationViewModel.getString("DE", lang),
                        onClick = { appState.currentLanguage = Language.DEUTSCH },
                        isActive = appState.currentLanguage == Language.DEUTSCH,
                        modifier = Modifier.weight(1f),
                        settings = settingsViewModel
                    )
                    SwissButton(
                        text = translationViewModel.getString("UA", lang),
                        onClick = { appState.currentLanguage = Language.UKRAINIAN },
                        isActive = appState.currentLanguage == Language.UKRAINIAN,
                        modifier = Modifier.weight(1f),
                        settings = settingsViewModel
                    )
                }
            }

            // Theme Selection Card (R-21 & R-34)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(settingsViewModel.elevatedSurfaceColor, RoundedCornerShape(2.dp))
                    .border(1.dp, settingsViewModel.borderColor, RoundedCornerShape(2.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = translationViewModel.getString("Theme:", lang).uppercase(),
                    color = settingsViewModel.secondaryTextColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.6.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SwissButton(
                        text = translationViewModel.getString("Zurich Light", lang),
                        onClick = {
                            appState.isDarkMode = false
                            settingsViewModel.applyTheme(false)
                        },
                        isActive = !settingsViewModel.isDarkMode,
                        modifier = Modifier.weight(1f),
                        settings = settingsViewModel
                    )
                    SwissButton(
                        text = translationViewModel.getString("Basel Dark", lang),
                        onClick = {
                            appState.isDarkMode = true
                            settingsViewModel.applyTheme(true)
                        },
                        isActive = settingsViewModel.isDarkMode,
                        modifier = Modifier.weight(1f),
                        settings = settingsViewModel
                    )
                }
            }

            // Schematic Canvas Labels Visibility Toggle
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(settingsViewModel.elevatedSurfaceColor, RoundedCornerShape(2.dp))
                    .border(1.dp, settingsViewModel.borderColor, RoundedCornerShape(2.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = translationViewModel.getString("Component Labels:", lang).uppercase(),
                    color = settingsViewModel.secondaryTextColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.6.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SwissButton(
                        text = translationViewModel.getString("Visible", lang),
                        onClick = { settingsViewModel.drawText = true },
                        isActive = settingsViewModel.drawText,
                        modifier = Modifier.weight(1f),
                        settings = settingsViewModel
                    )
                    SwissButton(
                        text = translationViewModel.getString("Hidden", lang),
                        onClick = { settingsViewModel.drawText = false },
                        isActive = !settingsViewModel.drawText,
                        modifier = Modifier.weight(1f),
                        settings = settingsViewModel
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            SwissButton(
                text = translationViewModel.getString("Save and exit", lang),
                onClick = {
                    appState.showSettings = false
                    scope.launch {
                        saveState(appState)
                    }
                },
                isPrimary = true,
                modifier = Modifier.fillMaxWidth(),
                settings = settingsViewModel
            )
        }
    }
}
