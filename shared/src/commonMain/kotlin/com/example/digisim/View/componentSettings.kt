package com.example.digisim.View

import androidx.compose.foundation.layout.Column
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
            val component = viewModel.components.find{ it.ID == viewModel.selectedGateId}
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
            if (component?.componentType !== ComponentType.OUTPUT && component?.componentType !== ComponentType.INPUT && component?.componentType !== ComponentType.NOT){
        Text("Input count:" + component?.inputCount)
            var userInputCount by remember {mutableStateOf("")}
            OutlinedTextField(value = userInputCount , onValueChange = {userInputCount = it} , placeholder = {Text("Change input count")},
                trailingIcon = {
                    IconButton(onClick = {
                        userInputCount.toIntOrNull()?.let {
                            if (it < 0){
                                component?.inputCount = 2
                            }
                            else if (it > 64){
                                component?.inputCount = 2
                            }
                            else {
                                component?.inputCount = userInputCount.toInt()
                            }

                        }
                    }){
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null
                        )
                    }
                }
                )
            }
            Spacer(modifier = Modifier.fillMaxHeight(0.1f))
            if(component?.componentType == ComponentType.CLOCK){
                if (convertHzToDelay(component.delay?.toFloat()) !== null) {
                    Text("Frequency: " + convertHzToDelay(component.delay?.toFloat() ?: 1000.0f)?.toString() + "Hz")
                }
                else {
                    Text("Frequency: 1KHz" )
                }
                var userInputDelay by remember {mutableStateOf("")}
                OutlinedTextField(value = userInputDelay , onValueChange = {userInputDelay = it} , placeholder = {Text("Change frequency")},
                    trailingIcon = {
                        IconButton(onClick = {
                            userInputDelay.toIntOrNull()?.let {
                                val output = convertHzToDelay(userInputDelay.toFloat())
                                if (output !== null){
                                if (((output?.roundToInt())?.rem(2))!! == 0){
                                    component?.delay = output
                                }
                                    else{
                                    component?.delay = output -1.0f

                                }

                                }
                            }
                        }){
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
