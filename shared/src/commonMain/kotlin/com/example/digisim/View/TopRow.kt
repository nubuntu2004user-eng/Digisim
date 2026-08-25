package com.example.digisim.View

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.digisim.LogicGates.And
import com.example.digisim.SimulationHandling.SimulationViewModel
import com.example.digisim.SimulationHandling.convertForSimulation


@Composable
fun TopRow(simulation : SimulationViewModel , viewModel : CanvasViewModel){
    val buttonList = mutableListOf(
        TopRowButtonParams("File" , {}),
        TopRowButtonParams("Edit" , {}),
        TopRowButtonParams("Project" , {}),
        TopRowButtonParams("Simulate" , {}),
        TopRowButtonParams("FPGA" , {} ),
        TopRowButtonParams("Window" , {}),
        TopRowButtonParams("Debug" , { print(convertForSimulation(And( 1 , 0f , 0f  , 2 , 1))) }),
        TopRowButtonParams("Run" , onClick = {simulation.convertData(viewModel)})

    )
    Row {
        for (i in buttonList){
            TextButton(onClick = i.onClick){
                Text(i.label , color = Color.Black)
            }
        }
    }
}



private data class TopRowButtonParams (
        var label : String,
        var onClick : () -> Unit
        )