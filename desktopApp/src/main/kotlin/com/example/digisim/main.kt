package com.example.digisim

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.digisim.View.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "DigiSim",
    ) {
        App()
    }
}