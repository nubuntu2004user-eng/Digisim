package com.example.digisim.ComponentsDrawing

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.digisim.DrawingLogic.getComponentInnerRectColor
import com.example.digisim.DrawingLogic.getComponentTextColor
import com.example.digisim.DrawingLogic.getGateOutlineColor
import com.example.digisim.DrawingLogic.getPortColor
import com.example.digisim.ParsingLogic.Component
import com.example.digisim.SettingsViewModel
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

    val textStyle = TextStyle(
        color = getComponentTextColor(component.outputPin, settings),
        fontSize = 20.sp,
        fontWeight = FontWeight.Medium
    )

    val radius = min(w, h) / 2f - 2f
    val center = Offset(x + w / 2f, y + h / 2f)

    // Connecting lead from input port to circular indicator
    drawLine(
        color = getGateOutlineColor(settings),
        start = Offset(x, y + h / 2f),
        end = Offset(center.x - radius, center.y),
        strokeWidth = 2f
    )

    // Fill circular LED indicator
    drawCircle(
        color = getComponentInnerRectColor(component.outputPin, settings),
        radius = radius,
        center = center
    )

    // Outline circular LED indicator
    drawCircle(
        color = getGateOutlineColor(settings),
        radius = radius,
        center = center,
        style = Stroke(width = 2f)
    )

    // Input port
    drawCircle(color = getPortColor(settings), radius = 6f, center = component.findPortOffset())

    val label = "ID ${component.ID}"
    val textLayout = textMeasurer.measure(label, style = textStyle)
    if (settings.drawText) {
        drawText(
            textMeasurer = textMeasurer,
            text = label,
            style = textStyle,
            topLeft = Offset(
                center.x - textLayout.size.width / 2,
                center.y - textLayout.size.height / 2
            )
        )
    }
}