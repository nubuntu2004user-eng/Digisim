package com.example.digisim.ComponentsDrawing

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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

fun DrawScope.drawButton(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    val x = component.x
    val y = component.y
    val w = component.width
    val h = component.height

    val cornerRadius = CornerRadius(4f, 4f)

    // Outer Simple Box Fill
    drawRoundRect(
        color = getComponentInnerRectColor(component.outputPin, settings),
        topLeft = Offset(x, y),
        size = Size(w, h),
        cornerRadius = cornerRadius
    )

    // Outer Box Outline
    drawRoundRect(
        color = getGateOutlineColor(settings),
        topLeft = Offset(x, y),
        size = Size(w, h),
        cornerRadius = cornerRadius,
        style = Stroke(width = 2f)
    )

    // Inner Push Button Cap Graphic
    val innerMargin = 8f
    drawRoundRect(
        color = getGateOutlineColor(settings),
        topLeft = Offset(x + innerMargin, y + innerMargin),
        size = Size(w - innerMargin * 2, h - innerMargin * 2),
        cornerRadius = CornerRadius(3f, 3f),
        style = Stroke(width = 1.5f)
    )

    // Output Port
    drawCircle(
        color = getPortColor(settings),
        radius = 6f,
        center = component.findPortOffset()
    )

    // Label
    val label = "BTN ${component.ID}"
    val textStyle = TextStyle(
        color = getComponentTextColor(component.outputPin, settings),
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium
    )
    val textLayout = textMeasurer.measure(label, style = textStyle)
    if (settings.drawText) {
        drawText(
            textMeasurer = textMeasurer,
            text = label,
            style = textStyle,
            topLeft = Offset(
                x + w / 2f - textLayout.size.width / 2f,
                y + h / 2f - textLayout.size.height / 2f
            )
        )
    }
}
