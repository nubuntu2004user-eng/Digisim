package com.example.digisim.ComponentsDrawing

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.sp
import com.example.digisim.DrawingLogic.getGateOutlineColor
import com.example.digisim.DrawingLogic.getPortColor
import com.example.digisim.ParsingLogic.Component
import com.example.digisim.SettingsViewModel

private data class PinInfo(val pinNum: Int, val name: String, val yOffset: Float)

private fun DrawScope.drawDIP14Chip(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel,
    chipNumber: String,
    chipSubtitle: String,
    leftPins: List<PinInfo>,
    rightPins: List<PinInfo>
) {
    val x = component.x
    val y = component.y
    val w = component.width
    val h = component.height

    val cornerRadius = CornerRadius(6f, 6f)

    // DIP IC Body (dark IC package)
    val bodyColor = Color(0xFF212121)
    drawRoundRect(
        color = bodyColor,
        topLeft = Offset(x, y),
        size = Size(w, h),
        cornerRadius = cornerRadius
    )

    // Body Outline
    val outlineColor = getGateOutlineColor(settings)
    drawRoundRect(
        color = outlineColor,
        topLeft = Offset(x, y),
        size = Size(w, h),
        cornerRadius = cornerRadius,
        style = Stroke(width = 2f)
    )

    // Top Notch (U-shaped notch at the top center of the IC)
    val notchRadius = 9f
    val notchCenter = Offset(x + w / 2f, y)
    val notchPath = Path().apply {
        moveTo(notchCenter.x - notchRadius, y)
        arcTo(
            rect = androidx.compose.ui.geometry.Rect(
                notchCenter.x - notchRadius,
                y - notchRadius,
                notchCenter.x + notchRadius,
                y + notchRadius
            ),
            startAngleDegrees = 0f,
            sweepAngleDegrees = 180f,
            forceMoveTo = false
        )
    }
    drawPath(path = notchPath, color = Color(0xFF333333))
    drawPath(path = notchPath, color = outlineColor, style = Stroke(width = 2f))

    // Pin 1 Indicator Dot (top-left corner)
    drawCircle(
        color = Color(0xFF555555),
        radius = 3.5f,
        center = Offset(x + 12f, y + 12f)
    )

    // Chip Center Label (e.g. "7400")
    val centerTextStyle = TextStyle(
        color = Color.White,
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace
    )
    val centerLayout = textMeasurer.measure(chipNumber, style = centerTextStyle)
    drawText(
        textMeasurer = textMeasurer,
        text = chipNumber,
        style = centerTextStyle,
        topLeft = Offset(
            x + w / 2f - centerLayout.size.width / 2f,
            y + h / 2f - centerLayout.size.height / 2f - 8f
        )
    )

    // Subtitle under chip number (e.g. "QUAD NAND")
    val subTextStyle = TextStyle(
        color = Color(0xFFB0BEC5),
        fontSize = 8.5.sp,
        fontWeight = FontWeight.Medium
    )
    val subLayout = textMeasurer.measure(chipSubtitle, style = subTextStyle)
    drawText(
        textMeasurer = textMeasurer,
        text = chipSubtitle,
        style = subTextStyle,
        topLeft = Offset(
            x + w / 2f - subLayout.size.width / 2f,
            y + h / 2f + 8f
        )
    )

    // Pin Labels Style
    val pinNumStyle = TextStyle(
        color = Color(0xFF90CAF9),
        fontSize = 8.5.sp,
        fontWeight = FontWeight.SemiBold,
        fontFamily = FontFamily.Monospace
    )
    val pinNameStyle = TextStyle(
        color = Color(0xFFE0E0E0),
        fontSize = 9.sp,
        fontWeight = FontWeight.Medium
    )

    // Left Pins (Pins 1 to 7)
    leftPins.forEach { pin ->
        val pinY = y + pin.yOffset
        // Pin Number
        val numLayout = textMeasurer.measure(pin.pinNum.toString(), style = pinNumStyle)
        drawText(
            textMeasurer = textMeasurer,
            text = pin.pinNum.toString(),
            style = pinNumStyle,
            topLeft = Offset(x + 5f, pinY - numLayout.size.height / 2f)
        )

        // Pin Function Name
        val nameLayout = textMeasurer.measure(pin.name, style = pinNameStyle)
        drawText(
            textMeasurer = textMeasurer,
            text = pin.name,
            style = pinNameStyle,
            topLeft = Offset(x + 16f, pinY - nameLayout.size.height / 2f)
        )
    }

    // Right Pins (Pins 14 down to 8)
    rightPins.forEach { pin ->
        val pinY = y + pin.yOffset
        // Pin Function Name
        val nameLayout = textMeasurer.measure(pin.name, style = pinNameStyle)
        drawText(
            textMeasurer = textMeasurer,
            text = pin.name,
            style = pinNameStyle,
            topLeft = Offset(x + w - nameLayout.size.width - 18f, pinY - nameLayout.size.height / 2f)
        )

        // Pin Number
        val numLayout = textMeasurer.measure(pin.pinNum.toString(), style = pinNumStyle)
        drawText(
            textMeasurer = textMeasurer,
            text = pin.pinNum.toString(),
            style = pinNumStyle,
            topLeft = Offset(x + w - numLayout.size.width - 5f, pinY - numLayout.size.height / 2f)
        )
    }

    // Port terminals for wire connections
    val portColor = getPortColor(settings)
    component.inputPortPositions().forEach { pos ->
        drawCircle(color = portColor, radius = 5.5f, center = pos)
    }
    component.outputPortPositions().forEach { pos ->
        drawCircle(color = portColor, radius = 5.5f, center = pos)
    }
}

fun DrawScope.drawIC7400(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    val left = listOf(
        PinInfo(1, "1A", 20f),
        PinInfo(2, "1B", 40f),
        PinInfo(3, "1Y", 60f),
        PinInfo(4, "2A", 80f),
        PinInfo(5, "2B", 100f),
        PinInfo(6, "2Y", 120f),
        PinInfo(7, "GND", 140f)
    )
    val right = listOf(
        PinInfo(14, "VCC", 20f),
        PinInfo(13, "4B", 40f),
        PinInfo(12, "4A", 60f),
        PinInfo(11, "4Y", 80f),
        PinInfo(10, "3B", 100f),
        PinInfo(9, "3A", 120f),
        PinInfo(8, "3Y", 140f)
    )
    drawDIP14Chip(component, textMeasurer, settings, "7400", "QUAD NAND", left, right)
}

fun DrawScope.drawIC7408(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    val left = listOf(
        PinInfo(1, "1A", 20f),
        PinInfo(2, "1B", 40f),
        PinInfo(3, "1Y", 60f),
        PinInfo(4, "2A", 80f),
        PinInfo(5, "2B", 100f),
        PinInfo(6, "2Y", 120f),
        PinInfo(7, "GND", 140f)
    )
    val right = listOf(
        PinInfo(14, "VCC", 20f),
        PinInfo(13, "4B", 40f),
        PinInfo(12, "4A", 60f),
        PinInfo(11, "4Y", 80f),
        PinInfo(10, "3B", 100f),
        PinInfo(9, "3A", 120f),
        PinInfo(8, "3Y", 140f)
    )
    drawDIP14Chip(component, textMeasurer, settings, "7408", "QUAD AND", left, right)
}

fun DrawScope.drawIC7432(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    val left = listOf(
        PinInfo(1, "1A", 20f),
        PinInfo(2, "1B", 40f),
        PinInfo(3, "1Y", 60f),
        PinInfo(4, "2A", 80f),
        PinInfo(5, "2B", 100f),
        PinInfo(6, "2Y", 120f),
        PinInfo(7, "GND", 140f)
    )
    val right = listOf(
        PinInfo(14, "VCC", 20f),
        PinInfo(13, "4B", 40f),
        PinInfo(12, "4A", 60f),
        PinInfo(11, "4Y", 80f),
        PinInfo(10, "3B", 100f),
        PinInfo(9, "3A", 120f),
        PinInfo(8, "3Y", 140f)
    )
    drawDIP14Chip(component, textMeasurer, settings, "7432", "QUAD OR", left, right)
}

fun DrawScope.drawIC7486(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    val left = listOf(
        PinInfo(1, "1A", 20f),
        PinInfo(2, "1B", 40f),
        PinInfo(3, "1Y", 60f),
        PinInfo(4, "2A", 80f),
        PinInfo(5, "2B", 100f),
        PinInfo(6, "2Y", 120f),
        PinInfo(7, "GND", 140f)
    )
    val right = listOf(
        PinInfo(14, "VCC", 20f),
        PinInfo(13, "4B", 40f),
        PinInfo(12, "4A", 60f),
        PinInfo(11, "4Y", 80f),
        PinInfo(10, "3B", 100f),
        PinInfo(9, "3A", 120f),
        PinInfo(8, "3Y", 140f)
    )
    drawDIP14Chip(component, textMeasurer, settings, "7486", "QUAD XOR", left, right)
}

fun DrawScope.drawIC7402(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    val left = listOf(
        PinInfo(1, "1Y", 20f),
        PinInfo(2, "1A", 40f),
        PinInfo(3, "1B", 60f),
        PinInfo(4, "2Y", 80f),
        PinInfo(5, "2A", 100f),
        PinInfo(6, "2B", 120f),
        PinInfo(7, "GND", 140f)
    )
    val right = listOf(
        PinInfo(14, "VCC", 20f),
        PinInfo(13, "4Y", 40f),
        PinInfo(12, "4B", 60f),
        PinInfo(11, "4A", 80f),
        PinInfo(10, "3Y", 100f),
        PinInfo(9, "3B", 120f),
        PinInfo(8, "3A", 140f)
    )
    drawDIP14Chip(component, textMeasurer, settings, "7402", "QUAD NOR", left, right)
}

fun DrawScope.drawIC7404(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    val left = listOf(
        PinInfo(1, "1A", 20f),
        PinInfo(2, "1Y", 40f),
        PinInfo(3, "2A", 60f),
        PinInfo(4, "2Y", 80f),
        PinInfo(5, "3A", 100f),
        PinInfo(6, "3Y", 120f),
        PinInfo(7, "GND", 140f)
    )
    val right = listOf(
        PinInfo(14, "VCC", 20f),
        PinInfo(13, "6A", 40f),
        PinInfo(12, "6Y", 60f),
        PinInfo(11, "5A", 80f),
        PinInfo(10, "5Y", 100f),
        PinInfo(9, "4A", 120f),
        PinInfo(8, "4Y", 140f)
    )
    drawDIP14Chip(component, textMeasurer, settings, "7404", "HEX NOT", left, right)
}
