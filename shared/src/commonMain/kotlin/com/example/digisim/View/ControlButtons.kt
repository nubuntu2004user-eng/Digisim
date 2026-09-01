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
import com.example.digisim.UiUtils.undoOnce
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun controlButtons(viewModel: CanvasViewModel , scope : CoroutineScope){
    Row{
        var showInputDialog by remember { mutableStateOf(false) }
        Column{
            var showFileMenu by remember { mutableStateOf(false)}
      TextButton(onClick = {showFileMenu = !showFileMenu}){
          Text("File")
      }
            DropdownMenu(
                expanded = showFileMenu,
                onDismissRequest = {showFileMenu = false}
            ){
                DropdownMenuItem(
                    text = {Text("Clear")},
                    onClick = {viewModel.components.clear() ; viewModel.wires.clear() ; viewModel.nextId = 0}
                )
                DropdownMenuItem(
                    text = {Text("Save as")},
                    onClick = { scope.launch {
                        saveFileAs(viewModel)
                    }
                    }
                )
                DropdownMenuItem(
                    text = {Text("Load")},
                    onClick = { scope.launch{
                        loadWrapper(viewModel)
                    } }
                )
            }

        }
        Spacer(modifier = Modifier.fillMaxWidth(0.01f))
        var showEditMenu by remember{ mutableStateOf(false)}
        TextButton(onClick = {showEditMenu = !showEditMenu}){
            Text("Edit")
            DropdownMenu(
                expanded = showEditMenu,
                onDismissRequest = {showEditMenu = false}
            ){
                DropdownMenuItem(
                    text = {Text("Undo")},
                    onClick = { undoOnce(viewModel) }
                )
                DropdownMenuItem(
                    text = {Text("Undo History")},
                    onClick = {showInputDialog = !showInputDialog}
                )
            }
        }
        Spacer(modifier = Modifier.fillMaxWidth(0.01f))
        TextButton(onClick = {}){
            Text("Settings")
        }
        Spacer(modifier = Modifier.fillMaxWidth(0.01f))
//        TextButton(onClick = {}){ will contain links to docks once they are released
//            Text("Help")
//        }
        Box{
        if(showInputDialog){
            var userInput by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showInputDialog = false },
                title = { Text("How many steps to undo") },
                text = {
                    TextField(
                        value = userInput,
                        onValueChange = { userInput = it },
                        label = { Text("Enter number here") }
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
                    }) { Text("Undo") }
                }
            )
        }
        }
    }
}