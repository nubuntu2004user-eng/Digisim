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
import com.example.digisim.DrawingLogic.drawSignalPort
import com.example.digisim.DrawingLogic.getGateOutlineColor
import com.example.digisim.ParsingLogic.Component
import com.example.digisim.SettingsViewModel
import logicGates.Pin

internal fun DrawScope.drawClock(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    val x = component.x
    val y = component.y
    val w = component.width
    val h = component.height

    val arrowWidth = 16f

    // Clock source path: pointed rightwards towards the output port
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

    // Square-wave pulse glyph at bottom of clock body
    if (settings.drawText) {
        val cx = x + (w - arrowWidth) / 2f
        val cy = y + h * 0.74f
        val waveColor = if (isHigh || isError) labelTextColor else settings.accentTaupeDark
        val wavePath = Path().apply {
            moveTo(cx - 10f, cy + 2f)
            lineTo(cx - 5f, cy + 2f)
            lineTo(cx - 5f, cy - 3f)
            lineTo(cx, cy - 3f)
            lineTo(cx, cy + 2f)
            lineTo(cx + 5f, cy + 2f)
            lineTo(cx + 5f, cy - 3f)
            lineTo(cx + 10f, cy - 3f)
        }
        drawPath(
            path = wavePath,
            color = waveColor,
            style = Stroke(width = 1.6f)
        )
    }

    // Output port
    drawSignalPort(
        center = component.findPortOffset(),
        pin = component.outputPin,
        settings = settings
    )

    val label = if (component.label.isEmpty()) "CLK ${component.ID}" else "${component.label} ${component.ID}"
    val textStyle = TextStyle(
        color = labelTextColor,
        fontSize = 11.5.sp,
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
                x + (w - arrowWidth) / 2f - textLayout.size.width / 2f,
                y + h * 0.42f - textLayout.size.height / 2f
            )
        )
    }
}