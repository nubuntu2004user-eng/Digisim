package com.example.digisim.View

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.digisim.SettingsViewModel
import com.example.digisim.SimulationHandling.SimulationViewModel
import com.example.digisim.UiUtils.TranslationViewModel
import com.example.digisim.UiUtils.AppState
import engineLogic.ClockManager
import kotlinx.coroutines.CoroutineScope


@Composable
fun TopRow(simulation : SimulationViewModel, viewModel : CanvasViewModel, scope : CoroutineScope, clockManager : ClockManager, translationViewModel: TranslationViewModel, appState : AppState){
    val settingsViewModel = SettingsViewModel.default
    Row {
        controlButtons(viewModel , scope, translationViewModel , clockManager , appState)
        Spacer(modifier = Modifier.fillMaxWidth(0.1f))
        if(!simulation.isRunning){
            TooltipBox(
            positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
                tooltip = {
                    PlainTooltip {
                        Text(translationViewModel.getString("Start" , appState.currentLanguage))
                    }
                },
                state = rememberTooltipState()
            ){
            IconButton(onClick = {simulation.startSimulation(viewModel , scope , clockManager = clockManager) ; viewModel.currentMode = CanvasViewModel.editingMode.POKE}){
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null
                )
            }
            }
            TooltipBox(
                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
                tooltip = {
                    PlainTooltip {
                        Text(translationViewModel.getString("Clear", appState.currentLanguage))
                    }
                },
                state = rememberTooltipState()
            ){
            IconButton(onClick = {viewModel.components.clear() ; viewModel.wires.clear() ; viewModel.nextId = 0}){
                Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = null
                )
            }

        }
        }
        else{
            TooltipBox(
                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
                tooltip = {
                    PlainTooltip {
                        Text(translationViewModel.getString("Stop", appState.currentLanguage))
                    }
                },
                state = rememberTooltipState()
            ){
                IconButton(onClick = {simulation.isRunning = false ; simulation.isAuto = false ; simulation.componentsState.clear() ; clockManager.tick = 0}){
                    Icon(
                        imageVector = Icons.Filled.Stop,
                        contentDescription = null
                    )
                }
            }
                if (simulation.isAuto){
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
                        tooltip = {
                            PlainTooltip {
                                Text(translationViewModel.getString("Pause", appState.currentLanguage))
                            }
                        },
                        state = rememberTooltipState()
                    ){
                    IconButton(onClick = {simulation.isAuto = false }){
                        Icon(
                            imageVector = Icons.Filled.Pause,
                            contentDescription = null

                        )
                    }
                }
                }
            else{
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
                        tooltip = {
                            PlainTooltip {
                                Text(translationViewModel.getString("One step", appState.currentLanguage))
                            }
                        },
                        state = rememberTooltipState()
                    ){
                    IconButton(onClick = {simulation.startSimulation(viewModel, scope, clockManager ,true)}){
                        Icon(
                            imageVector = Icons.Filled.ChevronRight,
                            contentDescription = null
                        )
                    }
                    }
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
                        tooltip = {
                            PlainTooltip {
                                Text(translationViewModel.getString("Auto", appState.currentLanguage))
                            }
                        },
                        state = rememberTooltipState()
                    ){
                    IconButton(onClick = {simulation.isAuto = true ; simulation.startSimulation(viewModel , scope , clockManager = clockManager)}){
                        Icon(
                            imageVector = Icons.Filled.DoubleArrow,
                            contentDescription = null

                        )
                    }
            }
            }

        }
        if (viewModel.currentMode == CanvasViewModel.editingMode.WIRE){
            val colorButtonList = listOf(
                colorButton(Color.Blue , translationViewModel.getString("Blue", appState.currentLanguage)),
                colorButton(Color.Black , translationViewModel.getString("Black", appState.currentLanguage)),
                colorButton(Color.Red , translationViewModel.getString("Red", appState.currentLanguage)),
                colorButton(Color.Green , translationViewModel.getString("Green", appState.currentLanguage)),
                colorButton(Color.Magenta , translationViewModel.getString("Magenta", appState.currentLanguage)),
                colorButton(Color.Cyan , translationViewModel.getString("Cyan", appState.currentLanguage))
            )
            Text(translationViewModel.getString("Select Color: ", appState.currentLanguage) , color = settingsViewModel.textColor, modifier = Modifier.padding(10.dp))
            for (i in colorButtonList){
                Button(onClick = {viewModel.currentWiringColor = i.color}, colors = ButtonColors(i.color , i.color , i.color , i.color)){}
            }
        }
        IconButton(onClick = {appState.agenticMode = !appState.agenticMode}){
            Icon(
                imageVector = Icons.Filled.SmartToy,
                contentDescription = null
            )
        }
    }
}
private class colorButton(
    val color: Color,
    val name : String
)