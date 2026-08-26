package com.example.digisim.View

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.digisim.SimulationHandling.SimulationViewModel
import com.example.digisim.UiUtils.findComponent

@Composable
fun componentSettings(viewModel: CanvasViewModel , simulationViewModel: SimulationViewModel){
    Column(modifier = Modifier.background(Color.LightGray).fillMaxSize()) {
        if (viewModel.selectedGateId != null ){
            val component = viewModel.components.find{ it.ID == viewModel.selectedGateId}
        Text( " " + component?.ID.toString() + "\n " + component?.componentType)
            Spacer(modifier = Modifier.fillMaxHeight(0.1f))
            Button(onClick = {
                viewModel.components.remove(component)
                viewModel.selectedGateId = null
            }){
                Text("Delete")
            }
        }
    }
}
