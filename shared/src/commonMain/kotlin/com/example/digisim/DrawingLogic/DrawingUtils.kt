package com.example.digisim.DrawingLogic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import com.example.digisim.ComponentsDrawing.drawAnd
import com.example.digisim.ParsingLogic.Component
import com.example.digisim.ParsingLogic.ComponentType
import com.example.digisim.ComponentsDrawing.drawInput
import com.example.digisim.ComponentsDrawing.drawNand
import com.example.digisim.ComponentsDrawing.drawNor
import com.example.digisim.ComponentsDrawing.drawNot
import com.example.digisim.ComponentsDrawing.drawOr
import com.example.digisim.ComponentsDrawing.drawOutput
import com.example.digisim.ComponentsDrawing.drawXnor
import com.example.digisim.ComponentsDrawing.drawXor

import logicGates.Pin

fun getComponentInnerRectColor(pin: Pin): Color {
    return when (pin) {
        Pin.HIGH -> Color(0xFF81C784) // Brighter green for High
        Pin.LOW -> Color(0xFF2E7D32)  // Darker green for Low
        Pin.ERROR -> Color(0xFFE57373)
        Pin.UNDEFINED -> Color(0xFFC8E6C9)
    }
}

fun getComponentTextColor(pin: Pin): Color {
    return if (pin == Pin.LOW) Color.White else Color.Black
}

internal fun DrawScope.drawWire(
    sourceComponent: Component,
    sourcePortIndex: Int,
    targetComponent: Component,
    targetPortIndex: Int
) {

    val sourcePos = if (sourceComponent.componentType == ComponentType.INPUT) {
        sourceComponent.findPortOffset()
    } else {
        sourceComponent.outputPortPositions()[sourcePortIndex]
    }

    val targetPos = if (targetComponent.componentType == ComponentType.OUTPUT) {
        targetComponent.findPortOffset()
    } else {
        targetComponent.inputPortPositions()[targetPortIndex]
    }

    val midX = (sourcePos.x + targetPos.x) / 2f
    val path = Path().apply {
        moveTo(sourcePos.x, sourcePos.y)
        lineTo(midX, sourcePos.y)
        lineTo(midX, targetPos.y)
        lineTo(targetPos.x, targetPos.y)
    }

    drawPath(
        path = path,
        color = Color.Blue,
        style = Stroke(width = 3f)
    )
}

 fun DrawScope.drawComponent(component: Component, textMeasurer: TextMeasurer){
        when (component.componentType){
            ComponentType.AND -> { drawAnd(component , textMeasurer)}
            ComponentType.OR -> { drawOr(component , textMeasurer)}
            ComponentType.NAND -> { drawNand(component , textMeasurer)}
            ComponentType.NOR -> { drawNor(component , textMeasurer)}
            ComponentType.XOR -> { drawXor(component , textMeasurer)}
            ComponentType.XNOR -> { drawXnor(component , textMeasurer)}
            ComponentType.NOT -> { drawNot(component , textMeasurer)}
            ComponentType.OUTPUT -> { drawOutput(component , textMeasurer)}
            ComponentType.INPUT -> { drawInput(component , textMeasurer)}


        }
}



