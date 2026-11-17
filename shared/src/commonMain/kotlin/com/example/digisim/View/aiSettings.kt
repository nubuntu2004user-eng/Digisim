package com.example.digisim.View

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.digisim.SettingsViewModel
import com.example.digisim.UiUtils.AppState
import com.example.digisim.UiUtils.TranslationViewModel

@Composable
fun aiSettings(appState: AppState, translationViewModel: TranslationViewModel) {
    val lang = appState.currentLanguage
    val settings = SettingsViewModel.default

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Provider Mode Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(settings.elevatedSurfaceColor, RoundedCornerShape(2.dp))
                .border(1.dp, settings.borderColor, RoundedCornerShape(2.dp))
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = translationViewModel.getString("Is the model local?", lang).uppercase(),
                color = settings.secondaryTextColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.6.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SwissButton(
                    text = translationViewModel.getString("Local Endpoint", lang),
                    onClick = { appState.isUsingLocalModel = true },
                    isActive = appState.isUsingLocalModel,
                    modifier = Modifier.weight(1f),
                    compact = true,
                    settings = settings
                )
                SwissButton(
                    text = translationViewModel.getString("Cloud API", lang),
                    onClick = { appState.isUsingLocalModel = false },
                    isActive = !appState.isUsingLocalModel,
                    modifier = Modifier.weight(1f),
                    compact = true,
                    settings = settings
                )
            }
        }

        // API Key Field
        SwissTextField(
            value = appState.apiKey,
            onValueChange = { appState.apiKey = it },
            label = translationViewModel.getString("Api key:", lang),
            placeholder = "sk-...",
            settings = settings
        )

        // Base URL (if local model)
        if (appState.isUsingLocalModel) {
            SwissTextField(
                value = appState.baseUrl,
                onValueChange = { appState.baseUrl = it },
                label = translationViewModel.getString("Base URL:", lang),
                placeholder = "http://127.0.0.1:9931",
                settings = settings
            )
        }

        // Current Model Field
        SwissTextField(
            value = appState.currentModel,
            onValueChange = { appState.currentModel = it },
            label = translationViewModel.getString("Current model:", lang),
            placeholder = "e.g. gpt-4o / qwen2.5-coder",
            settings = settings
        )

        // Available Models Readout
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(settings.elevatedSurfaceColor, RoundedCornerShape(2.dp))
                .border(1.dp, settings.borderColor, RoundedCornerShape(2.dp))
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = translationViewModel.getString("Available models:", lang).uppercase(),
                color = settings.secondaryTextColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.6.sp
            )
            Text(
                text = if (appState.modelList.isEmpty()) "None detected" else appState.modelList.joinToString(", "),
                color = settings.textColor,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
