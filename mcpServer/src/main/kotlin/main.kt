package com.example

import io.ktor.server.application.Application
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine

fun main() {
    startServer(wait = true)
}
fun startServer(wait : Boolean): EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration >{
    return embeddedServer(
        factory = io.ktor.server.netty.Netty,
        port = 8080,
        host = "127.0.0.1",
        module = Application::rootModule
    ).start(wait = wait)

}
