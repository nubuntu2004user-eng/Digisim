package com.example

import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.mcpServer.mcpServer
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.modelcontextprotocol.kotlin.sdk.server.mcpStreamableHttp

fun Application.configureRouting(canvasViewModel: CanvasViewModel = CanvasViewModel()) {
    val server = mcpServer(canvasViewModel).initializeServer()
    routing {
        get("/") {
            call.respondText("DigiSim MCP Server is running", status = HttpStatusCode.OK)
        }
        mcpStreamableHttp(path = "/mcp") {
            server
        }
    }
}