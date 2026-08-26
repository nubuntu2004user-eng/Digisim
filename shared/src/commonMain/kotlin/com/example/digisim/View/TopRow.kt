package com.example.digisim.View

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.digisim.SimulationHandling.SimulationViewModel
import kotlinx.coroutines.CoroutineScope


@Composable
fun TopRow(simulation : SimulationViewModel , viewModel : CanvasViewModel , scope : CoroutineScope){
    val buttonList = listOf(
        TopRowButtonParams("File" , {}),
        TopRowButtonParams("Edit" , {}),
        TopRowButtonParams("Project" , {}),
        TopRowButtonParams("Simulate" , {}),
        TopRowButtonParams("FPGA" , {} ),
        TopRowButtonParams("Window" , {}),

    )
    val simulationButtons = listOf(
        simulationButton(Icons.Filled.Stop , {simulation.isRunning = false ; simulation.componentsState.clear()}),
        simulationButton(Icons.Filled.Pause , {})
    )
    Row {
        for (i in buttonList){
            TextButton(onClick = i.onClick){
                Text(i.label , color = Color.Black)
            }
        }
        Spacer(modifier = Modifier.fillMaxWidth(0.1f))
        if(!simulation.isRunning){
            IconButton(onClick = {simulation.startSimulation(viewModel , scope) ; viewModel.currentMode = CanvasViewModel.editingMode.POKE}){
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null
                )
            }
            IconButton(onClick = {viewModel.components.clear() ; viewModel.wires.clear() ; viewModel.nextId = 0}){
                Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = null
                )
            }

        }
        else{
            for (i in simulationButtons){
                IconButton(onClick = i.onClick){
                    Icon(
                        imageVector = i.icon,
                        contentDescription = null
                    )
                }
            }
        }
        if (viewModel.currentMode == CanvasViewModel.editingMode.WIRE){
            val colorButtonList = listOf(
                colorButton(Color.Blue , "Blue"),
                colorButton(Color.Black , "Black")
            )
            Text("Select Color: " , modifier = Modifier.padding(10.dp))
            for (i in colorButtonList){
                Button(onClick = {viewModel.currentWiringColor = i.color}, colors = ButtonColors(i.color , i.color , i.color , i.color)){}
            }
        }

    }
}



private data class TopRowButtonParams (
        var label : String,
        var onClick : () -> Unit
        )
private class simulationButton(
    val icon : ImageVector ,
    val onClick: () -> Unit

)
private class colorButton(
    val color: Color,
    val name : String
)