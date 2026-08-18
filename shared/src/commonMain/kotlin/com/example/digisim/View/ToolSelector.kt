package com.example.digisim.View

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.digisim.ParsingLogic.GateType

@Composable
fun ToolSelectorRow(viewModel : CanvasViewModel){
     val buttonList = mutableListOf(
        ToolButtonParams(Icons.Filled.TouchApp , {viewModel.currentMode = CanvasViewModel.editingMode.POKE}),
         ToolButtonParams(Icons.Filled.Edit , {viewModel.currentMode = CanvasViewModel.editingMode.EDIT}),
        ToolButtonParams(Icons.Filled.AdsClick , {viewModel.currentMode = CanvasViewModel.editingMode.DRAG}),
        ToolButtonParams(Icons.Filled.Cable , {viewModel.currentMode = CanvasViewModel.editingMode.WIRE}),
        ToolButtonParams(Icons.Filled.TextFields , {}),

        )
    Row{
    for (i in buttonList) {
        IconButton(onClick = i.onClick){
            Icon(
                imageVector = i.icon,
                contentDescription = null
            )
        }
    }
        TextButton(onClick = {viewModel.addInputOrOutput(true)}){
            Text("Add Input")
        }
        TextButton(onClick = {viewModel.addInputOrOutput(false)}){
            Text("Add Output")
        }

        GateType.values().forEach { gateType ->
            TextButton(onClick = {viewModel.addGate(gateType)}){
                Text(gateType.label)
            }
        }
    }
}



private data class ToolButtonParams(
    val icon : ImageVector ,
    val onClick :() -> Unit

)