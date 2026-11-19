package com.example.digisim.ComponentsDrawing

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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

fun DrawScope.drawButton(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    val x = component.x
    val y = component.y
    val w = component.width
    val h = component.height

    val cornerRadius = CornerRadius(3f, 3f)
    val isHigh = component.outputPin == Pin.HIGH

    // Outer Housing Fill
    drawRoundRect(
        color = settings.ioIdleFillColor,
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
    val innerMargin = 7f
    val capFill = if (isHigh) settings.highPinColor else settings.gateBodyFillColor
    val capTextColor = if (isHigh) settings.textHighColor else settings.textColor
    drawRoundRect(
        color = capFill,
        topLeft = Offset(x + innerMargin, y + innerMargin),
        size = Size(w - innerMargin * 2, h - innerMargin * 2),
        cornerRadius = CornerRadius(2f, 2f)
    )
    drawRoundRect(
        color = getGateOutlineColor(settings),
        topLeft = Offset(x + innerMargin, y + innerMargin),
        size = Size(w - innerMargin * 2, h - innerMargin * 2),
        cornerRadius = CornerRadius(2f, 2f),
        style = Stroke(width = 1.5f)
    )

    // Output Port
    drawSignalPort(
        center = component.findPortOffset(),
        pin = component.outputPin,
        settings = settings
    )

    // Label
    val label = if (component.label.isEmpty()) "BTN ${component.ID}" else "${component.label} ${component.ID}"
    val textStyle = TextStyle(
        color = capTextColor,
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
                y + h / 2f - textLayout.size.height / 2f
            )
        )
    }
}
