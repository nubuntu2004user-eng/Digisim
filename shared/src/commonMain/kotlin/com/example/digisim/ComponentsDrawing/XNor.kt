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

fun DrawScope.drawXnor(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    val x = component.x
    val y = component.y
    val w = component.width
    val h = component.height

    val arcOffset = 8f
    val bubbleRadius = 5f
    val bodyX = x + arcOffset
    val bodyW = w - arcOffset - bubbleRadius

    // XNOR gate body: offset OR body + inversion bubble
    val bodyPath = Path().apply {
        moveTo(bodyX, y)
        quadraticTo(bodyX + bodyW * 0.55f, y, bodyX + bodyW, y + h * 0.5f)
        quadraticTo(bodyX + bodyW * 0.55f, y + h, bodyX, y + h)
        quadraticTo(bodyX + bodyW * 0.25f, y + h * 0.5f, bodyX, y)
        close()
    }

    // Input curved arc at the back
    val inputArcPath = Path().apply {
        moveTo(x, y)
        quadraticTo(x + bodyW * 0.25f, y + h * 0.5f, x, y + h)
    }

    // Fill body
    drawPath(
        path = bodyPath,
        color = settings.gateBodyFillColor
    )

    // Outline body
    drawPath(
        path = bodyPath,
        color = getGateOutlineColor(settings),
        style = Stroke(width = 2f)
    )

    // Outline input arc
    drawPath(
        path = inputArcPath,
        color = getGateOutlineColor(settings),
        style = Stroke(width = 2f)
    )

    // Inversion bubble
    val bubbleCenter = Offset(x + w, y + h / 2f)
    drawCircle(
        color = settings.gateBodyFillColor,
        radius = bubbleRadius,
        center = bubbleCenter
    )
    drawCircle(
        color = getGateOutlineColor(settings),
        radius = bubbleRadius,
        center = bubbleCenter,
        style = Stroke(width = 2f)
    )

    if (settings.drawText) {
        drawGateSignalPill(
            center = Offset(bodyX + bodyW * 0.45f, y + h * 0.72f),
            pin = component.outputPin,
            settings = settings
        )
    }

    val label = if (component.label.isEmpty()) "XNOR ${component.ID}" else "${component.label} ${component.ID}"
    val textStyle = TextStyle(
        color = settings.textColor,
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
                bodyX + bodyW * 0.45f - textLayout.size.width / 2f,
                y + h * 0.44f - textLayout.size.height / 2f
            )
        )
    }

    // Input ports
    val inputPositions = component.inputPortPositions()
    if (component.inputCount > 2 && inputPositions.isNotEmpty()) {
        val minY = inputPositions.minOf { it.y }
        val maxY = inputPositions.maxOf { it.y }
        drawLine(
            color = getGateOutlineColor(settings),
            start = Offset(x, minY),
            end = Offset(x, maxY),
            strokeWidth = 2f
        )
    }

    inputPositions.forEachIndexed { idx, pos ->
        val pinState = component.inputPins.getOrElse(idx) { Pin.UNDEFINED }
        drawSignalPort(center = pos, pin = pinState, settings = settings)
    }

    // Output ports
    component.outputPortPositions().forEach { pos ->
        drawSignalPort(center = pos, pin = component.outputPin, settings = settings)
    }
}