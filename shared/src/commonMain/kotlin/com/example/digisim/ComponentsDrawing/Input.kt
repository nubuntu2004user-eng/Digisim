package com.example.digisim.ComponentsDrawing

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
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

    val textStyle = TextStyle(
        color = getComponentTextColor(component.outputPin, settings),
        fontSize = 20.sp,
        fontWeight = FontWeight.Medium
    )

    // Fill
    drawPath(
        path = path,
        color = getComponentInnerRectColor(component.outputPin, settings)
    )

    // Outline
    drawPath(
        path = path,
        color = getGateOutlineColor(settings),
        style = Stroke(width = 2f)
    )

    // Output port
    drawCircle(color = getPortColor(settings), radius = 6f, center = component.findPortOffset())

    val label = "ID ${component.ID}"
    val textLayout = textMeasurer.measure(label, style = textStyle)
    if (settings.drawText) {
        drawText(
            textMeasurer = textMeasurer,
            text = label,
            style = textStyle,
            topLeft = Offset(
                x + (w - arrowWidth) / 2 - textLayout.size.width / 2,
                y + h / 2 - textLayout.size.height / 2
            )
        )
    }
}

