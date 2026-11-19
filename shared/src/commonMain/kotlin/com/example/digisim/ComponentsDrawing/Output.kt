package com.example.digisim.ComponentsDrawing

import androidx.compose.ui.geometry.Offset
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
import kotlin.math.min

fun DrawScope.drawOutput(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    val x = component.x
    val y = component.y
    val w = component.width
    val h = component.height

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

    val radius = min(w, h) / 2f - 2f
    val center = Offset(x + w / 2f, y + h / 2f)

    // Connecting lead from input port to circular indicator
    drawLine(
        color = getGateOutlineColor(settings),
        start = Offset(x, y + h / 2f),
        end = Offset(center.x - radius, center.y),
        strokeWidth = 2f
    )

    // Fill circular indicator
    drawCircle(
        color = bodyFill,
        radius = radius,
        center = center
    )

    // Outline circular indicator
    drawCircle(
        color = getGateOutlineColor(settings),
        radius = radius,
        center = center,
        style = Stroke(width = 2f)
    )

    if (settings.drawText && !isHigh && !isError) {
        drawGateSignalPill(
            center = Offset(center.x, center.y + radius * 0.48f),
            pin = component.outputPin,
            settings = settings,
            width = 16f,
            height = 4.5f
        )
    }

    // Input port
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
    val baseLabel = if (component.label.isEmpty()) "OUT ${component.ID}" else "${component.label} ${component.ID}"
    val label = "$baseLabel$stateSuffix"

    val textStyle = TextStyle(
        color = labelTextColor,
        fontSize = 10.5.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace
    )
    val textLayout = textMeasurer.measure(label, style = textStyle)
    if (settings.drawText) {
        val textCenterY = if (!isHigh && !isError) center.y - 3f else center.y
        drawText(
            textMeasurer = textMeasurer,
            text = label,
            style = textStyle,
            topLeft = Offset(
                center.x - textLayout.size.width / 2f,
                textCenterY - textLayout.size.height / 2f
            )
        )
    }
}