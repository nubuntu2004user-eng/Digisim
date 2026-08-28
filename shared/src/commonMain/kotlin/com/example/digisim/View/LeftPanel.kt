package com.example.digisim.View

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.digisim.SimulationHandling.SimulationViewModel
import com.example.digisim.UiUtils.ComponentLibrariesViewModel

@Composable
fun leftPanel(viewModel: CanvasViewModel, simulation: SimulationViewModel , librariesViewModel: ComponentLibrariesViewModel){
    Column(modifier = Modifier.fillMaxSize()) {
        componentLibraries(viewModel , librariesViewModel)
        componentSettings(viewModel, simulation)
    }
}