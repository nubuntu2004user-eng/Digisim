package com.example

import com.example.digisim.DrawingLogic.CanvasViewModel
import io.ktor.server.application.Application

fun Application.rootModule(canvasViewModel: CanvasViewModel = CanvasViewModel()) {
    configureRouting(canvasViewModel)
}
