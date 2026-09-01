package com.example.digisim.View

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.digisim.ParsingLogic.ComponentType
import com.example.digisim.SimulationHandling.SimulationViewModel
import kotlin.math.roundToInt

@Composable
fun componentSettings(viewModel: CanvasViewModel , simulationViewModel: SimulationViewModel){
    Column(modifier = Modifier.fillMaxSize()) {
        if (viewModel.selectedGateId != null ){
            val component by mutableStateOf(viewModel.components.find{ it.ID == viewModel.selectedGateId})
            val wiresIn by mutableStateOf(viewModel.wires.filter{it.targetGateId == component?.ID})
            val wiresOut by mutableStateOf(viewModel.wires.filter { it.sourceGateId == component?.ID })
            Text("Properties" ,  fontSize = 18.sp , modifier = Modifier.padding(10.dp).drawBehind{
                val spaceBeneath = 3f
                val textHeight = this.size.height
                val textWith = this.size.width
                drawLine(
                    color = Color.Black,
                    start = Offset(0f , textHeight + spaceBeneath),
                    end = Offset(textWith , textHeight + spaceBeneath)
                )

        })
        Text( "ID: " + component?.ID.toString())
        Text("Component type:" + component?.componentType)
            val isMultiInputGate = component?.componentType in listOf(
                ComponentType.AND,
                ComponentType.OR,
                ComponentType.NAND,
                ComponentType.NOR,
                ComponentType.XOR,
                ComponentType.XNOR
            )
            if (isMultiInputGate) {
                Text("Input count: " + component?.inputCount)
                var userInputCount by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = userInputCount,
                    onValueChange = { userInputCount = it },
                    placeholder = { Text("Change input count") },
                    trailingIcon = {
                        IconButton(onClick = {
                            userInputCount.toIntOrNull()?.let {
                                if (it < 2) {
                                    component?.inputCount = 2
                                } else if (it > 64) {
                                    component?.inputCount = 64
                                } else {
                                    component?.inputCount = it
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null
                            )
                        }
                    }
                )
            }
            Text("Wires in: ")
            wiresIn.forEach { wireIn ->
             Row{
                 Text("From component: " + wireIn.sourceGateId + "Port: " + wireIn.sourcePortIndex)
                 Button(onClick = {viewModel.wires.remove(wireIn)}){
                     Text("Delete")
                 }
             }
            }
            Text("Wires Out: ")
            wiresOut.forEach { wireOut ->
                Row{
                    Text("To component: " + wireOut.targetGateId + "Port: " + wireOut.targetPortIndex)
                    Button(onClick = {viewModel.wires.remove(wireOut)}){
                        Text("Delete")
                    }
                }
            }

            Spacer(modifier = Modifier.fillMaxHeight(0.1f))
            if (component?.componentType == ComponentType.CLOCK) {
                if (convertHzToDelay(component?.delay) != null) {
                    Text("Frequency: " + convertHzToDelay(component?.delay ?: 1000.0f)?.toString() + "Hz")
                } else {
                    Text("Frequency: 1KHz")
                }
                var userInputDelay by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = userInputDelay,
                    onValueChange = { userInputDelay = it },
                    placeholder = { Text("Change frequency") },
                    trailingIcon = {
                        IconButton(onClick = {
                            userInputDelay.toFloatOrNull()?.let { freq ->
                                val output = convertHzToDelay(freq)
                                if (output != null) {
                                    val rounded = output.roundToInt()
                                    component?.delay = if (rounded % 2 == 0) output else output - 1.0f
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null
                            )
                        }
                    }
                )
            }
            Button(onClick = {
                viewModel.components.remove(component)
                viewModel.selectedGateId = null
            }){
                Text("Delete")
            }
        }
    }
}

private fun convertHzToDelay(input : Float?):Float?{
    if (input == null){
        return null
    }
    if(input > 999.0f){
        return null
    }
    if (input > 1.0f){
        return 1000.0f / input
    }
     return 1000.0f
}
