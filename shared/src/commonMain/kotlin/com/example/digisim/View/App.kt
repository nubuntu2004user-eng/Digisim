package com.example.digisim.View

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.digisim.DrawingLogic.DigitalLogicSimulator

@Composable
@Preview
fun App() {
    MaterialTheme {
        val viewModel = CanvasViewModel()
        Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
            TopRow()
            Row {
                Column(modifier = Modifier.weight(0.3f)) {
                    componentSettings(viewModel)
                }
                Column(modifier = Modifier.weight(1f)) {
                    ToolSelectorRow(viewModel)
                    Box(modifier = Modifier.weight(1f)) {
                        DigitalLogicSimulator(viewModel = viewModel)
                    }
                }

            }
        }
    }
}