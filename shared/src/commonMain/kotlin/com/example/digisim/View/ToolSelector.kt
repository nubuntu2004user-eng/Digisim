package com.example.digisim.View

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.painterResource

@Composable
fun ToolSelectorRow(){
    Row{
    for (i in buttonList) {
        IconButton(onClick = i.onClick){
            Icon(
                imageVector = i.icon,
                contentDescription = null
            )
        }





    }
    }
}

private val buttonList = mutableListOf(
    ToolButtonParams(Icons.Filled.TouchApp , {}),
    ToolButtonParams(Icons.Filled.AdsClick , {}),
    ToolButtonParams(Icons.Filled.Cable , {}),
    ToolButtonParams(Icons.Filled.TextFields , {})
)

private data class ToolButtonParams(
    val icon : ImageVector ,
    val onClick :() -> Unit

)