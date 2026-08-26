package com.example.digisim.ParsingLogic

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color


data class Wire(
    val id: Int,
    val sourceGateId: Int,
    val sourcePortIndex: Int,
    val targetGateId: Int,
    val targetPortIndex: Int ,
    var color: Color = Color.Blue
)

data class PortHit(
    val elementId: Int,
    val portIndex: Int,
    val isInput: Boolean,
    val position: Offset
)

data class DragState(
    val componentId: Int,
    val pointerOffset: Offset,
    val originalX: Float,
    val originalY: Float
)
//enum class GateType(
//    val label: String,
//    val inputCount: Int,
//    val outputCount: Int
//) {
//    AND("AND", 2, 1),
//    NAND("NAND", 2, 1),
//    OR("OR", 2, 1),
//    NOR("NOR", 2, 1),
//    XOR("XOR", 2, 1),
//    NOT("NOT", 1, 1)
//}

