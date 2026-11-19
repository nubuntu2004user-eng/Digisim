package com.example.digisim.ComponentsDrawing

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.digisim.DrawingLogic.drawGateSignalPill
import com.example.digisim.DrawingLogic.drawSignalPort
import com.example.digisim.DrawingLogic.getGateOutlineColor
import com.example.digisim.ParsingLogic.Component
import com.example.digisim.SettingsViewModel
import logicGates.Pin

internal fun DrawScope.drawInput(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    val x = component.x
    val y = component.y
    val w = component.width
    val h = component.height

    val arrowWidth = 16f

    // Input switch path: pointed rightwards towards the output port
    val path = Path().apply {
        moveTo(x, y)
        lineTo(x + w - arrowWidth, y)
        lineTo(x + w, y + h / 2f)
        lineTo(x + w - arrowWidth, y + h)
        lineTo(x, y + h)
        close()
    }

    val isHigh = component.outputPin == Pin.HIGH
    val isError = component.outputPin == Pin.ERROR

    val bodyFill = when {
        isHigh -> settings.highPinColor
        isError -> settings.errorPinColor
        else -> settings.ioIdleFillColor
    }
    val labelTextColor = when {
        isHigh -> settings.textHighColor
        isError -> settings.primaryControlText
        else -> settings.textColor
    }

    // Fill
    drawPath(
        path = path,
        color = bodyFill
    )

    // Outline
    drawPath(
        path = path,
        color = getGateOutlineColor(settings),
        style = Stroke(width = 2f)
    )

    if (settings.drawText && !isHigh && !isError) {
        drawGateSignalPill(
            center = Offset(x + (w - arrowWidth) / 2f, y + h * 0.74f),
            pin = component.outputPin,
            settings = settings,
            width = 18f,
            height = 4.5f
        )
    }

    // Output port
    drawSignalPort(
        center = component.findPortOffset(),
        pin = component.outputPin,
        settings = settings
    )

    val stateSuffix = when (component.outputPin) {
        Pin.HIGH -> ":1"
        Pin.LOW -> ":0"
        Pin.ERROR -> ":E"
        Pin.UNDEFINED -> ""
    }
    val baseLabel = if (component.label.isEmpty()) "IN ${component.ID}" else "${component.label} ${component.ID}"
    val label = "$baseLabel$stateSuffix"

    val textStyle = TextStyle(
        color = labelTextColor,
        fontSize = 11.5.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace
    )
    val textLayout = textMeasurer.measure(label, style = textStyle)
    if (settings.drawText) {
        val textCenterY = if (!isHigh && !isError) y + h * 0.44f else y + h / 2f
        drawText(
            textMeasurer = textMeasurer,
            text = label,
            style = textStyle,
            topLeft = Offset(
                x + (w - arrowWidth) / 2f - textLayout.size.width / 2f,
                textCenterY - textLayout.size.height / 2f
            )
        )
    }
}

