package com.example.digisim.View

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.digisim.DrawingLogic.CanvasViewModel

@Composable
fun componentSettings(viewModel: CanvasViewModel){
    Column(modifier = Modifier.background(Color.LightGray).fillMaxSize()) {
        if (viewModel.selectedGateId != null ){
            val gate = viewModel.gates.find{ it.id == viewModel.selectedGateId}
        Text( " " + gate?.id.toString() + "\n " + gate?.type)
            Spacer(modifier = Modifier.fillMaxHeight(0.1f))
            Button(onClick = {
                viewModel.gates.remove(gate)
                viewModel.selectedGateId = null
            }){
                Text("Delete")
            }
        }
    }
}
