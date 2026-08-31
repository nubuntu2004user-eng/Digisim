package com.example.digisim.DrawingLogic

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import com.example.digisim.ComponentsDrawing.drawAnd
import com.example.digisim.ComponentsDrawing.drawClock
import com.example.digisim.ComponentsDrawing.drawInput
import com.example.digisim.ComponentsDrawing.drawNand
import com.example.digisim.ComponentsDrawing.drawNor
import com.example.digisim.ComponentsDrawing.drawNot
import com.example.digisim.ComponentsDrawing.drawOr
import com.example.digisim.ComponentsDrawing.drawOutput
import com.example.digisim.ComponentsDrawing.drawXnor
import com.example.digisim.ComponentsDrawing.drawXor
import com.example.digisim.ParsingLogic.Component
import com.example.digisim.ParsingLogic.ComponentType
import com.example.digisim.SettingsViewModel
import logicGates.Pin

/**
 * Calculates the positions for input ports.
 * Modular structure to support future orientation and size changes.
 */
fun calculateInputPinPositions(component: Component): List<Offset> {
    val inputCount = component.inputCount
    if (inputCount <= 0) return emptyList()

    // Base logic for standard layout (<= 2 inputs)
    if (inputCount <= 2) {
        val spacing = component.height / (inputCount + 1)
        return (1..inputCount).map { i -> Offset(component.x, component.y + spacing * i) }
    }

    // Line-based layout for > 2 inputs
    // Constant spacing
    val pinSpacing = 20f

    // For now, assume vertical orientation (standard)
    val totalHeight = (inputCount - 1) * pinSpacing
    val startY = component.y + (component.height / 2f) - (totalHeight / 2f)

    return (0 until inputCount).map { i ->
        Offset(component.x, startY + (i * pinSpacing))
    }
}

fun getComponentInnerRectColor(pin: Pin, settings: SettingsViewModel = SettingsViewModel.default): Color {
    return settings.getComponentInnerRectColor(pin)
}
// ... existing code ...

fun getComponentTextColor(pin: Pin, settings: SettingsViewModel = SettingsViewModel.default): Color {
    return settings.getComponentTextColor(pin)
}

fun getGateOutlineColor(settings: SettingsViewModel = SettingsViewModel.default): Color {
    return settings.gateOutlineColor
}

fun getPortColor(settings: SettingsViewModel = SettingsViewModel.default): Color {
    return settings.portColor
}


fun getWireHighlightColor(settings: SettingsViewModel = SettingsViewModel.default): Color {
    return settings.wireHighlightColor
}

internal fun DrawScope.drawWire(
    sourceComponent: Component,
    sourcePortIndex: Int,
    targetComponent: Component,
    targetPortIndex: Int,
    color: Color,
) {

    val sourcePos = if (sourceComponent.componentType == ComponentType.INPUT || sourceComponent.componentType == ComponentType.CLOCK) {
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
        color = color,
        style = Stroke(width = 3f)
    )
}

fun DrawScope.drawComponent(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    when (component.componentType) {
        ComponentType.AND -> { drawAnd(component, textMeasurer, settings) }
        ComponentType.OR -> { drawOr(component, textMeasurer, settings) }
        ComponentType.NAND -> { drawNand(component, textMeasurer, settings) }
        ComponentType.NOR -> { drawNor(component, textMeasurer, settings) }
        ComponentType.XOR -> { drawXor(component, textMeasurer, settings) }
        ComponentType.XNOR -> { drawXnor(component, textMeasurer, settings) }
        ComponentType.NOT -> { drawNot(component, textMeasurer, settings) }
        ComponentType.OUTPUT -> { drawOutput(component, textMeasurer, settings) }
        ComponentType.INPUT -> { drawInput(component, textMeasurer, settings) }
        ComponentType.CLOCK -> {drawClock(component ,textMeasurer , settings)}
    }
}



