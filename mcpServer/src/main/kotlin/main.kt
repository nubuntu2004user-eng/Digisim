package com.example

import com.example.digisim.DrawingLogic.CanvasViewModel
import io.ktor.server.application.Application
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine

fun main() {
    startServer(wait = true)
}

fun startServer(
    canvasViewModel: CanvasViewModel = CanvasViewModel(),
    wait: Boolean = false,
    port: Int = 8080
): EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration> {
    return embeddedServer(
        factory = Netty,
        port = port,
        host = "127.0.0.1",
        module = { rootModule(canvasViewModel) }
    ).start(wait = wait)
}
