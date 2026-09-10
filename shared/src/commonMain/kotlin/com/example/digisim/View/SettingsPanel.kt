package com.example.digisim.View

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.digisim.SettingsViewModel
import com.example.digisim.UiUtils.Language
import com.example.digisim.UiUtils.TranslationViewModel
import com.example.digisim.UiUtils.UiState

@Composable
fun settingsPanel(settingsViewModel: SettingsViewModel , uiState: UiState , translationViewModel: TranslationViewModel) {
    Column {
        Text("Settings" , color = settingsViewModel.textColor , modifier = Modifier.padding(8.dp))
        Spacer(modifier = Modifier.fillMaxHeight(0.01f))
        Row {
            Text("Language:" , color = settingsViewModel.textColor , modifier = Modifier.padding(12.dp))

            TextButton(onClick = {translationViewModel.currentLanguage = Language.ENGLISH}){

                Text("EN", color = settingsViewModel.textColor)
        }
            TextButton(onClick = {translationViewModel.currentLanguage = Language.DEUTSCH}){

                Text("DE", color = settingsViewModel.textColor)
            }
            TextButton(onClick = {translationViewModel.currentLanguage = Language.UKRAINIAN}){

                Text("UA" , color = settingsViewModel.textColor)
            }
        }
        TextButton(onClick = {uiState.showSettings = !uiState.showSettings}){
            Text("Save and exit" , color = settingsViewModel.textColor)
        }

    }
}