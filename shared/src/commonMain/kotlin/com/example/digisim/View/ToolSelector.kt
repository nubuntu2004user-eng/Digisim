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
import com.example.digisim.ParsingLogic.ComponentType


@Composable
fun ToolSelectorRow(viewModel : CanvasViewModel){
     val buttonList = listOf(
        ToolButtonParams(Icons.Filled.TouchApp , {viewModel.currentMode = CanvasViewModel.editingMode.POKE}),
         ToolButtonParams(Icons.Filled.Edit , {viewModel.currentMode = CanvasViewModel.editingMode.EDIT}),
        ToolButtonParams(Icons.Filled.AdsClick , {viewModel.currentMode = CanvasViewModel.editingMode.DRAG}),
        ToolButtonParams(Icons.Filled.Cable , {viewModel.currentMode = CanvasViewModel.editingMode.WIRE}),
        ToolButtonParams(Icons.Filled.TextFields , {}),

        )
    val componentButtonList = listOf(
        ComponentButtonParams("Add Input" , {viewModel.addComponent(ComponentType.INPUT)}),
        ComponentButtonParams("Add Output" , {viewModel.addComponent(ComponentType.OUTPUT)}),
        ComponentButtonParams("Add Not" , {viewModel.addComponent(ComponentType.NOT)}),
        ComponentButtonParams("Add And" , {viewModel.addComponent(ComponentType.AND)}),
        ComponentButtonParams("Add Or" , {viewModel.addComponent(ComponentType.OR)}),
        ComponentButtonParams("Add Xor" , {viewModel.addComponent(ComponentType.XOR)}),
        ComponentButtonParams("Add Nand" , {viewModel.addComponent(ComponentType.NAND)}),
        ComponentButtonParams("Add Nor" , {viewModel.addComponent(ComponentType.NOR)}),
        ComponentButtonParams("Add XNor" , {viewModel.addComponent(ComponentType.XNOR)}),








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
        for (i in componentButtonList){
        TextButton(onClick = i.onClick){
            Text(i.text)
        }
        }



    }
}



private data class ToolButtonParams(
    val icon : ImageVector ,
    val onClick :() -> Unit

)

private data class ComponentButtonParams(
    val text : String,
    val onClick :() -> Unit
)