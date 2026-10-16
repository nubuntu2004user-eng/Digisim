package com.example.digisim

import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.digisim.View.App
import com.example.startServer
import io.ktor.server.engine.stop

fun main() = application {
    val server = startServer(false)
    DisposableEffect(Unit){
    onDispose {
    server.stop(gracePeriodMillis = 300, timeoutMillis = 3000)
    }
    }
    Window(
        onCloseRequest = ::exitApplication,
        title = "DigiSim",
    ) {
        App()
    }
}