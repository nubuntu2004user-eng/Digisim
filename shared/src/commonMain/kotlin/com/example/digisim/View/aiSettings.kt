package com.example.digisim.View

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.digisim.UiUtils.AppState
import com.example.digisim.UiUtils.TranslationViewModel

@Composable
fun aiSettings(appState: AppState, translationViewModel: TranslationViewModel) {
    val rowPadding = 14.dp
    val lang = appState.currentLanguage
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.padding(rowPadding)) {
            Text(translationViewModel.getString("Api key:", lang), modifier = Modifier.weight(0.2f))
            OutlinedTextField(
                value = appState.apiKey,
                onValueChange = { appState.apiKey = it },
                modifier = Modifier.weight(0.8f),
                shape = RoundedCornerShape(50)
            )
        }
        Row(modifier = Modifier.padding(rowPadding)) {
            Text(
                translationViewModel.getString("Is the model local?", lang) + " ${appState.isUsingLocalModel}",
                modifier = Modifier.weight(0.3f)
            )
            IconButton(onClick = { appState.isUsingLocalModel = true }, modifier = Modifier.weight(0.3f)) {
                Icon(imageVector = Icons.Filled.Check, contentDescription = null)
            }
            IconButton(onClick = { appState.isUsingLocalModel = false }, modifier = Modifier.weight(0.3f)) {
                Icon(imageVector = Icons.Filled.Cancel, contentDescription = null)
            }
        }

        if (appState.isUsingLocalModel) {
            Row(modifier = Modifier.padding(rowPadding)) {
                Text(translationViewModel.getString("Base URL:", lang), modifier = Modifier.weight(0.2f))
                OutlinedTextField(
                    value = appState.baseUrl,
                    onValueChange = { appState.baseUrl = it },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.weight(0.8f)
                )
            }
        }
        Text(
            translationViewModel.getString("Available models:", lang) + " ${appState.modelList}",
            modifier = Modifier.padding(rowPadding)
        )
        Row(modifier = Modifier.padding(rowPadding)) {
            Text(translationViewModel.getString("Current model:", lang), modifier = Modifier.weight(0.2f))
            OutlinedTextField(
                value = appState.currentModel,
                onValueChange = { appState.currentModel = it },
                shape = RoundedCornerShape(50),
                modifier = Modifier.weight(0.8f)
            )
        }
    }
}
