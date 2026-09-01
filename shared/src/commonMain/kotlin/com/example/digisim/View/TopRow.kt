package com.example.digisim.View

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.digisim.SimulationHandling.SimulationViewModel
import com.example.digisim.UiUtils.TranslationViewModel
import engineLogic.ClockManager
import kotlinx.coroutines.CoroutineScope


@Composable
fun TopRow(simulation : SimulationViewModel , viewModel : CanvasViewModel , scope : CoroutineScope , clockManager : ClockManager, translationViewModel: TranslationViewModel){
    Row {
        controlButtons(viewModel , scope, translationViewModel)
        Spacer(modifier = Modifier.fillMaxWidth(0.1f))
        if(!simulation.isRunning){
            IconButton(onClick = {simulation.startSimulation(viewModel , scope , clockManager = clockManager) ; viewModel.currentMode = CanvasViewModel.editingMode.POKE}){
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
                IconButton(onClick = {simulation.isRunning = false ; simulation.isAuto = false ; simulation.componentsState.clear() ; clockManager.tick = 0.0f}){
                    Icon(
                        imageVector = Icons.Filled.Stop,
                        contentDescription = null
                    )
                }
                if (simulation.isAuto){
                    IconButton(onClick = {simulation.isAuto = false }){
                        Icon(
                            imageVector = Icons.Filled.Pause,
                            contentDescription = null

                        )
                    }
                }
            else{
                    IconButton(onClick = {simulation.isAuto = true ; simulation.startSimulation(viewModel , scope , clockManager = clockManager)}){
                        Icon(
                            imageVector = Icons.Filled.DoubleArrow,
                            contentDescription = null

                        )
                    }
            }

        }
        if (viewModel.currentMode == CanvasViewModel.editingMode.WIRE){
            val colorButtonList = listOf(
                colorButton(Color.Blue , translationViewModel.getString("Blue")),
                colorButton(Color.Black , translationViewModel.getString("Black")),
                colorButton(Color.Red , translationViewModel.getString("Red")),
                colorButton(Color.Green , translationViewModel.getString("Green")),
                colorButton(Color.Magenta , translationViewModel.getString("Magenta")),
                colorButton(Color.Cyan , translationViewModel.getString("Cyan"))
            )
            Text(translationViewModel.getString("Select Color: ") , modifier = Modifier.padding(10.dp))
            for (i in colorButtonList){
                Button(onClick = {viewModel.currentWiringColor = i.color}, colors = ButtonColors(i.color , i.color , i.color , i.color)){}
            }
        }

    }
}
private class colorButton(
    val color: Color,
    val name : String
)