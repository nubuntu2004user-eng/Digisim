package com.example.digisim

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.digisim.View.App
import com.example.startServer
import io.ktor.server.engine.stop

fun main() = application {
    val canvasViewModel = remember { CanvasViewModel() }
    val server = remember { startServer(canvasViewModel = canvasViewModel, wait = false) }
    val windowState = rememberWindowState(size = DpSize(1280.dp, 820.dp))

    DisposableEffect(Unit) {
        onDispose {
            server.stop(gracePeriodMillis = 300, timeoutMillis = 3000)
        }
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "DigiSim",
        state = windowState
    ) {
        App(viewModel = canvasViewModel)
    }
}
