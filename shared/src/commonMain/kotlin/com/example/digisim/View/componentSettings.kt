package com.example.digisim.View

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import com.example.digisim.UiUtils.TranslationViewModel

@Composable
fun componentSettings(viewModel: CanvasViewModel , simulationViewModel: SimulationViewModel, translationViewModel: TranslationViewModel){
    Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
        if (viewModel.selectedGateId != null ){
            val component by mutableStateOf(viewModel.components.find{ it.ID == viewModel.selectedGateId})
            val wiresIn by mutableStateOf(viewModel.wires.filter{it.targetGateId == component?.ID})
            val wiresOut by mutableStateOf(viewModel.wires.filter { it.sourceGateId == component?.ID })

            Text(translationViewModel.getString("Properties") ,  fontSize = 18.sp , modifier = Modifier.padding(10.dp).drawBehind{
                val spaceBeneath = 3f
                val textHeight = this.size.height
                val textWith = this.size.width
                drawLine(
                    color = Color.Black,
                    start = Offset(0f , textHeight + spaceBeneath),
                    end = Offset(textWith , textHeight + spaceBeneath)
                )

        })
            Spacer(modifier = Modifier.fillMaxHeight(0.01f))

            Text( translationViewModel.getString("ID: ") + component?.ID.toString())

            Spacer(modifier = Modifier.fillMaxHeight(0.01f))

            var userLabel by remember(component?.label) { mutableStateOf(component?.label ?: "") }
            OutlinedTextField(
                value = userLabel,
                onValueChange = { userLabel = it },
                label = { Text(translationViewModel.getString("Component Label")) },
                shape = RoundedCornerShape(30),
                trailingIcon = {
                    IconButton(onClick = {
                        component?.label = userLabel
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.fillMaxHeight(0.01f))

            Text(translationViewModel.getString("Component type: ") + component?.componentType)
            val isMultiInputGate = component?.componentType in listOf(
                ComponentType.AND,
                ComponentType.OR,
                ComponentType.NAND,
                ComponentType.NOR,
                ComponentType.XOR,
                ComponentType.XNOR
            )
            if (isMultiInputGate) {

                Spacer(modifier = Modifier.fillMaxHeight(0.01f))

                Text(translationViewModel.getString("Input count: ") + component?.inputCount)

                Spacer(modifier = Modifier.fillMaxHeight(0.01f))

                var userInputCount by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = userInputCount,
                    onValueChange = { userInputCount = it },
                    placeholder = { Text(translationViewModel.getString("Change input count")) },
                    shape = RoundedCornerShape(30),
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
            Spacer(modifier = Modifier.fillMaxHeight(0.01f))

            if (wiresIn.isNotEmpty()){

            Text(translationViewModel.getString("Wires in: "))

            wiresIn.forEach { wireIn ->
             Row{
                 Text(translationViewModel.getString("From component: ") + wireIn.sourceGateId + translationViewModel.getString("Port: ") + wireIn.sourcePortIndex ,
                     modifier = Modifier.padding(10.dp))
                 Button(onClick = {viewModel.wires.remove(wireIn)} ,
                     colors = ButtonColors(Color.Red , Color.White , Color.Red , Color.White)
                     ){
                     Text(translationViewModel.getString("Delete"))
                 }
             }
            }
            }
            Spacer(modifier = Modifier.fillMaxHeight(0.01f))

            if (wiresOut.isNotEmpty()){
            Text(translationViewModel.getString("Wires Out: "))

            wiresOut.forEach { wireOut ->
                Row{
                    Text(translationViewModel.getString("To component: ") + wireOut.targetGateId + translationViewModel.getString("Port: ") + wireOut.targetPortIndex,
                        modifier = Modifier.padding(10.dp))
                    Button(onClick = {viewModel.wires.remove(wireOut)},
                        colors = ButtonColors(Color.Red , Color.White , Color.Red , Color.White)
                    ){
                        Text(translationViewModel.getString("Delete"))
                    }
                }
            }
            }

            Spacer(modifier = Modifier.fillMaxHeight(0.1f))

            if (component?.componentType == ComponentType.CLOCK) {
                val frequencyHz = toggleDelayTicksToFrequencyHz(component?.delay)

                if (frequencyHz != null) {
                    Text(
                        translationViewModel.getString("Frequency") +
                                ": " +
                                frequencyHz.toString() +
                                "Hz"
                    )
                } else {
                    Text(
                        translationViewModel.getString("Frequency") +
                                ": " +
                                translationViewModel.getString("Not set")
                    )
                }

                var dropdownExpanded by remember { mutableStateOf(false) }
                Spacer(modifier = Modifier.fillMaxHeight(0.01f))

                Column {
                    Button(onClick = { dropdownExpanded = true }) {
                        Text(translationViewModel.getString("Change frequency"))
                    }
                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        val frequencies = listOf(500f, 250f, 125f, 100f, 50f, 25f, 20f, 10f, 5f, 4f, 1f)
                        frequencies.forEach { freq ->
                            DropdownMenuItem(
                                text = { Text("${freq.toInt()} Hz") },
                                onClick = {
                                    val delayTicks = frequencyHzToToggleDelayTicks(freq)
                                    if (delayTicks != null) {
                                        component?.delay = delayTicks
                                    }
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.fillMaxHeight(0.01f))

            Button(onClick = {
                viewModel.components.remove(component)
                viewModel.selectedGateId = null
            },
                colors = ButtonColors(Color.Red , Color.White , Color.Red , Color.White)

            ){
                Text(translationViewModel.getString("Delete Component"))
            }
        }
    }
}

private const val TICKS_PER_SECOND = 1000.0f

private fun frequencyHzToToggleDelayTicks(frequencyHz: Float?): Float? {
    if (frequencyHz == null || frequencyHz <= 0.0f) {
        return null
    }

    return TICKS_PER_SECOND / (frequencyHz * 2.0f)
}

private fun toggleDelayTicksToFrequencyHz(delayTicks: Float?): Float? {
    if (delayTicks == null || delayTicks <= 0.0f) {
        return null
    }
    return TICKS_PER_SECOND / (delayTicks * 2.0f)
}
