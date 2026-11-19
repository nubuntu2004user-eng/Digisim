package com.example.digisim.ComponentsDrawing

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.digisim.DrawingLogic.drawSignalPort
import com.example.digisim.ParsingLogic.Component
import com.example.digisim.SettingsViewModel
import logicGates.Pin

fun DrawScope.drawCustomComponent(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    val x = component.x
    val y = component.y
    val w = component.width
    val h = component.height

    val cornerRadius = CornerRadius(3f, 3f)

    // Warm Parchment / Graphite Subcircuit Fill
    drawRoundRect(
        color = settings.customComponentFillColor,
        topLeft = Offset(x, y),
        size = Size(w, h),
        cornerRadius = cornerRadius,
        style = Fill
    )

    // Swiss Taupe top header bar to distinguish custom hierarchical subcircuits
    drawRoundRect(
        color = settings.accentTaupe,
        topLeft = Offset(x, y),
        size = Size(w, 6f),
        cornerRadius = CornerRadius(3f, 3f),
        style = Fill
    )

    // Technical Pen Outline
    drawRoundRect(
        color = settings.customComponentOutlineColor,
        topLeft = Offset(x, y),
        size = Size(w, h),
        cornerRadius = cornerRadius,
        style = Stroke(width = 2f)
    )

    // Label
    val label = when {
        component.label.isNotEmpty() -> component.label
        !component.customComponentName.isNullOrBlank() -> component.customComponentName!!
        else -> "MOD ${component.ID}"
    }
    val textStyle = TextStyle(
        color = settings.textColor,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace
    )
    val textLayout = textMeasurer.measure(label, style = textStyle)
    if (settings.drawText) {
        drawText(
            textMeasurer = textMeasurer,
            text = label,
            style = textStyle,
            topLeft = Offset(
                x + w / 2f - textLayout.size.width / 2f,
                y + h / 2f - textLayout.size.height / 2f + 2f
            )
        )
    }

    // Input ports
    component.inputPortPositions().forEachIndexed { idx, pos ->
        val pinState = component.inputPins.getOrElse(idx) { Pin.UNDEFINED }
        drawSignalPort(center = pos, pin = pinState, settings = settings)
    }

    // Output ports
    component.outputPortPositions().forEachIndexed { idx, pos ->
        val pinState = component.outputPins.getOrElse(idx) {
            if (idx == 0) component.outputPin else Pin.UNDEFINED
        }
        drawSignalPort(center = pos, pin = pinState, settings = settings)
    }
}
