package com.example.digisim.View

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.digisim.DrawingLogic.DigitalLogicSimulator
import com.example.digisim.SimulationHandling.SimulationViewModel
import com.example.digisim.UiUtils.ComponentLibrariesViewModel
import engineLogic.ClockManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.coroutineContext

@Composable
@Preview
fun App() {
    MaterialTheme {
        val scope: CoroutineScope = rememberCoroutineScope()
        val viewModel = remember { CanvasViewModel() }
        val simulation = remember { SimulationViewModel() }
        val librariesViewModel  = remember { ComponentLibrariesViewModel() }
        val clockManager = remember { ClockManager() }
        Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
            TopRow(simulation , viewModel , scope , clockManager)
            Row {
                Column(modifier = Modifier.weight(0.3f)) {
                    leftPanel(viewModel, simulation , librariesViewModel)
                }
                Column(modifier = Modifier.weight(1f)) {
                    ToolSelectorRow(viewModel , simulation)
                    Box(modifier = Modifier.weight(1f)) {
                        DigitalLogicSimulator(viewModel = viewModel, simulation = simulation, scope = scope , clockManager = clockManager)
                    }
                }

            }
        }
    }
}