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
import com.example.digisim.Persistance.persistanceRepository.defaultDataDir
import com.example.digisim.Persistance.persistanceRepository.saveState
import com.example.digisim.SettingsViewModel
import com.example.digisim.UiUtils.Language
import com.example.digisim.UiUtils.TranslationViewModel
import com.example.digisim.UiUtils.AppState
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.dialogs.openDirectoryPicker
import io.github.vinceglb.filekit.path
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun settingsPanel(settingsViewModel: SettingsViewModel, appState: AppState, translationViewModel: TranslationViewModel , scope : CoroutineScope) {
    Column {
        Text("Settings" , color = settingsViewModel.textColor , modifier = Modifier.padding(8.dp))
        Spacer(modifier = Modifier.fillMaxHeight(0.01f))
        Row {
            Text("Language:" , color = settingsViewModel.textColor , modifier = Modifier.padding(12.dp))

            TextButton(onClick = {appState.currentLanguage = Language.ENGLISH}){

                Text("EN", color = settingsViewModel.textColor)
        }
            TextButton(onClick = {appState.currentLanguage = Language.DEUTSCH}){

                Text("DE", color = settingsViewModel.textColor)
            }
            TextButton(onClick = {appState.currentLanguage = Language.UKRAINIAN}){

                Text("UA" , color = settingsViewModel.textColor)
            }
        }
        Spacer(modifier = Modifier.fillMaxHeight(0.01f))

        TextButton(onClick = {appState.showSettings = !appState.showSettings ;
            scope.launch {
            saveState(appState )
            }
            }){
            Text("Save and exit" , color = settingsViewModel.textColor)
        }

    }
}