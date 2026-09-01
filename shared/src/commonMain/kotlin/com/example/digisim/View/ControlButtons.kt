package com.example.digisim.View

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun controlButtons(){
    Row{
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
                    text = {Text("New")},
                    onClick = {}
                )
                DropdownMenuItem(
                    text = {Text("Clear")},
                    onClick = {}
                )
                DropdownMenuItem(
                    text = {Text("Save")},
                    onClick = {}
                )
                DropdownMenuItem(
                    text = {Text("Save as")},
                    onClick = {}
                )
                DropdownMenuItem(
                    text = {Text("Load")},
                    onClick = {}
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
                    onClick = {}
                )
                DropdownMenuItem(
                    text = {Text("Redo")},
                    onClick = {}
                )
                DropdownMenuItem(
                    text = {Text("Undo History")},
                    onClick = {}
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
    }
}