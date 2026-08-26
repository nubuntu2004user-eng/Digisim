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

fun DrawScope.drawOr(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    val x = component.x
    val y = component.y
    val w = component.width
    val h = component.height

    // OR gate path: curved back, curved top and bottom meeting at the output tip
    val path = Path().apply {
        moveTo(x, y)
        quadraticTo(x + w * 0.55f, y, x + w, y + h * 0.5f)
        quadraticTo(x + w * 0.55f, y + h, x, y + h)
        quadraticTo(x + w * 0.25f, y + h * 0.5f, x, y)
        close()
    }

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

    // Label – using Compose's drawText (multiplatform)
    val label = "ID ${component.ID}"
    val textStyle = TextStyle(
        color = getComponentTextColor(component.outputPin, settings),
        fontSize = 20.sp,
        fontWeight = FontWeight.Medium
    )
    val textLayout = textMeasurer.measure(label, style = textStyle)
    if (settings.drawText) {
        drawText(
            textMeasurer = textMeasurer,
            text = label,
            style = textStyle,
            topLeft = Offset(
                x + w * 0.45f - textLayout.size.width / 2,
                y + h / 2 - textLayout.size.height / 2
            )
        )
    }
    // Input ports
    component.inputPortPositions().forEach { pos ->
        drawCircle(color = getPortColor(settings), radius = 6f, center = pos)
    }
    // Output ports
    component.outputPortPositions().forEach { pos ->
        drawCircle(color = getPortColor(settings), radius = 6f, center = pos)
    }
}