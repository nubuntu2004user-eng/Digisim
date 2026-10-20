package com.example.mcpServer

import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.digisim.ParsingLogic.Wire
import io.modelcontextprotocol.kotlin.sdk.types.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.TextContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import androidx.compose.ui.graphics.Color


suspend fun wireComponents(request: CallToolRequest , canvasViewModel: CanvasViewModel) = withContext(Dispatchers.Main){
    val args = request.arguments
    val sourceGateId = args?.get("source_gate_id")?.jsonPrimitive?.contentOrNull?.toIntOrNull()
    val sourcePortIndex = args?.get("source_port_index")?.jsonPrimitive?.contentOrNull?.toIntOrNull()
    val targetGateId = args?.get("target_gate_id")?.jsonPrimitive?.contentOrNull?.toIntOrNull()
    val targetPortIndex = args?.get("target_port_index")?.jsonPrimitive?.contentOrNull?.toIntOrNull()
    var report = ""
    if(sourceGateId !== null && sourcePortIndex !== null && targetGateId !== null && targetPortIndex !== null){
    canvasViewModel.wires.add(
        Wire(
            id = canvasViewModel.nextId++,
            sourceGateId = sourceGateId,
            sourcePortIndex = sourcePortIndex,
            targetGateId = targetGateId,
            targetPortIndex = targetPortIndex,
            color = Color.Blue
        )
    )
        report = "Successfully added wire from gate $sourceGateId port $sourcePortIndex to gate $targetGateId port $targetPortIndex"
    }
    else {
        report = "Failed to add wire one of the properties was parsed as null"
    }
    CallToolResult(
        content = listOf(TextContent(report))
    )
}