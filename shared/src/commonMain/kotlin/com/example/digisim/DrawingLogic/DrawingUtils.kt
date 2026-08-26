package com.example.digisim.DrawingLogic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import com.example.digisim.ComponentsDrawing.drawAnd
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

fun getComponentInnerRectColor(pin: Pin, settings: SettingsViewModel = SettingsViewModel.default): Color {
    return settings.getComponentInnerRectColor(pin)
}

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
    }
}



