package com.example.digisim.View

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


private val buttonList = mutableListOf(
    TopRowButtonParams("File" , {}),
    TopRowButtonParams("Edit" , {}),
    TopRowButtonParams("Project" , {}),
    TopRowButtonParams("Simulate" , {}),
    TopRowButtonParams("FPGA" , {} ),
    TopRowButtonParams("Window" , {}),
    TopRowButtonParams("Help" , {})

)
@Composable
fun TopRow(){
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