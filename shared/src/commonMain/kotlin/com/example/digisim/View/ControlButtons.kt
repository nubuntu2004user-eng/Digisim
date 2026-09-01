package com.example.digisim.View

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.digisim.Persistance.loadFile
import com.example.digisim.Persistance.loadWrapper
import com.example.digisim.Persistance.saveFileAs
import com.example.digisim.SettingsViewModel
import com.example.digisim.UiUtils.Language
import com.example.digisim.UiUtils.TranslationViewModel
import com.example.digisim.UiUtils.undoOnce
import engineLogic.ClockManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun controlButtons(viewModel: CanvasViewModel , scope : CoroutineScope , translationViewModel: TranslationViewModel ,clockManager: ClockManager){
    val settingsViewModel = SettingsViewModel.default
    Row{
        var showInputDialog by remember { mutableStateOf(false) }
        Column{
            var showFileMenu by remember { mutableStateOf(false)}
      TextButton(onClick = {showFileMenu = !showFileMenu}){
      Text(translationViewModel.getString("File") , color = settingsViewModel.textColor)
      }
      DropdownMenu(
          expanded = showFileMenu,
          onDismissRequest = {showFileMenu = false}
      ){
          DropdownMenuItem(
              text = {Text(translationViewModel.getString("Clear") , color = settingsViewModel.textColor)},
              onClick = {viewModel.components.clear() ; viewModel.wires.clear() ; viewModel.nextId = 0;clockManager.tick = 0}
          )
          DropdownMenuItem(
              text = {Text(translationViewModel.getString("Save as") , color = settingsViewModel.textColor)},
              onClick = { scope.launch {
                  saveFileAs(viewModel)
              }
              }
          )
          DropdownMenuItem(
              text = {Text(translationViewModel.getString("Load") , color = settingsViewModel.textColor)},
              onClick = { scope.launch{
                  loadWrapper(viewModel)
              } }
          )
      }

      }
      Spacer(modifier = Modifier.fillMaxWidth(0.01f))
      var showEditMenu by remember{ mutableStateOf(false)}
      TextButton(onClick = {showEditMenu = !showEditMenu}){
      Text(translationViewModel.getString("Edit"), color = settingsViewModel.textColor)
      DropdownMenu(
          expanded = showEditMenu,
          onDismissRequest = {showEditMenu = false}
      ){
          DropdownMenuItem(
              text = {Text(translationViewModel.getString("Undo") , color = settingsViewModel.textColor)},
              onClick = { undoOnce(viewModel) }
          )
          DropdownMenuItem(
              text = {Text(translationViewModel.getString("Undo multiple") , color = settingsViewModel.textColor)},
              onClick = {showInputDialog = !showInputDialog}
          )

      }
      }
      //TextButton(onClick = {}){ will contain links to docks once they are released
      //        Text("Help")
      //        }

      Spacer(modifier = Modifier.fillMaxWidth(0.01f))
      TextButton(onClick = {translationViewModel.currentLanguage = Language.ENGLISH}){
      Text("EN" , color = settingsViewModel.textColor)
      }
      Spacer(modifier = Modifier.fillMaxWidth(0.01f))
      TextButton(onClick = {translationViewModel.currentLanguage = Language.DEUTSCH}){
      Text("DE", color = settingsViewModel.textColor)
      }
      Spacer(modifier = Modifier.fillMaxWidth(0.01f))
      TextButton(onClick = {translationViewModel.currentLanguage = Language.UKRAINIAN}){
      Text("UA", color = settingsViewModel.textColor)
      }

      Box{
      if(showInputDialog){
      var userInput by remember { mutableStateOf("") }
      AlertDialog(
          onDismissRequest = { showInputDialog = false },
          title = { Text(translationViewModel.getString("How many steps to undo"), color = settingsViewModel.textColor) },
          text = {
              TextField(
                  value = userInput,
                  onValueChange = { userInput = it },
                  label = { Text(translationViewModel.getString("Enter number here"), color = settingsViewModel.textColor) }
              )
          },
          confirmButton = {
              TextButton(onClick = {
                  if (userInput.toIntOrNull() !== null){
                  repeat(userInput.toInt()){
                      undoOnce(viewModel)
                  }
                  showInputDialog = false}
                  else userInput = "Must be number!"
              }) { Text(translationViewModel.getString("Undo"), color = settingsViewModel.textColor) }
          }
      )
      }
      }

    }
}