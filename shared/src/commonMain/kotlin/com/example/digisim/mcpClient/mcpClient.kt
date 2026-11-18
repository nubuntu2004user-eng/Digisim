package com.example.digisim.mcpClient

import io.ktor.client.*
import io.ktor.client.plugins.sse.*
import io.modelcontextprotocol.kotlin.sdk.client.Client
import io.modelcontextprotocol.kotlin.sdk.client.StreamableHttpClientTransport
import io.modelcontextprotocol.kotlin.sdk.types.*
import kotlinx.serialization.json.JsonObject

class mcpClient(val url: String = "http://127.0.0.1:8080/mcp") {
    val httpClient = HttpClient {
        install(SSE)
    }
    val client = Client(
        clientInfo = Implementation(
            name = "DigisimMcpClient",
            version = "1.0.0"
        )
    )
    val transport = StreamableHttpClientTransport(
        client = httpClient,
        url = url
    )
    suspend fun initializeClient(){
        client.connect(transport)
    }
    suspend fun listTools(): List<Tool>{
        return client.listTools().tools
    }

    suspend fun callTool(name: String, arguments: JsonObject? = null): CallToolResult {
        return client.callTool(CallToolRequest(CallToolRequestParams(name = name, arguments = arguments)))
    }
}