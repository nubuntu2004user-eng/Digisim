package com.example.digisim.View

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        TopRow()
            Row {
                Column(modifier = Modifier.weight(0.3f)) {
                    Text("Add things later")
                }
                Column(modifier = Modifier.weight(1f)) {
                    ToolSelectorRow()
                }
            }

        }
    }
}