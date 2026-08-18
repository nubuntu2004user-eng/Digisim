package com.example.digisim.DrawingLogic

import androidx.compose.ui.geometry.Offset

data class Gate(
    val id: Int,
    val type: GateType,
    var x: Float,       // top‑left x
    var y: Float        // top‑left y
) {
    val width = 80f
    val height = 60f

    fun inputPortOffsets(): List<Offset> {
        val count = type.inputCount
        val spacing = height / (count + 1)
        return (1..count).map { i -> Offset(0f, spacing * i) }
    }

    fun outputPortOffsets(): List<Offset> {
        val count = type.outputCount
        val spacing = height / (count + 1)
        return (1..count).map { i -> Offset(width, spacing * i) }
    }

    fun inputPortPositions(): List<Offset> =
        inputPortOffsets().map { it + Offset(x, y) }

    fun outputPortPositions(): List<Offset> =
        outputPortOffsets().map { it + Offset(x, y) }
}

data class Wire(
    val id: Int,
    val sourceGateId: Int,
    val sourcePortIndex: Int,
    val targetGateId: Int,
    val targetPortIndex: Int
)

data class PortHit(
    val gateId: Int,
    val portIndex: Int,
    val isInput: Boolean,
    val position: Offset
)

data class DragState(
    val gateId: Int,
    val pointerOffset: Offset,
    val originalX: Float,
    val originalY: Float
)
enum class GateType(
    val label: String,
    val inputCount: Int,
    val outputCount: Int
) {
    AND("AND", 2, 1),
    NAND("NAND", 2, 1),
    OR("OR", 2, 1),
    NOR("NOR", 2, 1),
    XOR("XOR", 2, 1),
    NOT("NOT", 1, 1)
}

