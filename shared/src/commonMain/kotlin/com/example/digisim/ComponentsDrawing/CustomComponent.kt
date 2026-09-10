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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.digisim.DrawingLogic.getComponentTextColor
import com.example.digisim.DrawingLogic.getPortColor
import com.example.digisim.ParsingLogic.Component
import com.example.digisim.SettingsViewModel

fun DrawScope.drawCustomComponent(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    val x = component.x
    val y = component.y
    val w = component.width
    val h = component.height

    // Square box with rounded corners
    val cornerRadius = CornerRadius(w * 0.25f, h * 0.25f)
    
    // Fill
    drawRoundRect(
        color = settings.customComponentFillColor,
        topLeft = Offset(x, y),
        size = Size(w, h),
        cornerRadius = cornerRadius,
        style = Fill
    )

    // Outline
    drawRoundRect(
        color = settings.customComponentOutlineColor,
        topLeft = Offset(x, y),
        size = Size(w, h),
        cornerRadius = cornerRadius,
        style = Stroke(width = 4f)
    )

    // Label
    val label = if (component.label.isNotEmpty()) component.label else "Custom ${component.ID}"
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
                x + w / 2 - textLayout.size.width / 2,
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
