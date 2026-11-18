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

fun DrawScope.drawIC7407(
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
    drawDIP14Chip(component, textMeasurer, settings, "7407", "HEX BUF", left, right)
}

fun DrawScope.drawIC7410(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    val left = listOf(
        PinInfo(1, "1A", 20f),
        PinInfo(2, "1B", 40f),
        PinInfo(3, "2A", 60f),
        PinInfo(4, "2B", 80f),
        PinInfo(5, "2C", 100f),
        PinInfo(6, "2Y", 120f),
        PinInfo(7, "GND", 140f)
    )
    val right = listOf(
        PinInfo(14, "VCC", 20f),
        PinInfo(13, "1C", 40f),
        PinInfo(12, "1Y", 60f),
        PinInfo(11, "3C", 80f),
        PinInfo(10, "3B", 100f),
        PinInfo(9, "3A", 120f),
        PinInfo(8, "3Y", 140f)
    )
    drawDIP14Chip(component, textMeasurer, settings, "7410", "3x3 NAND", left, right)
}

fun DrawScope.drawIC7411(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    val left = listOf(
        PinInfo(1, "1A", 20f),
        PinInfo(2, "1B", 40f),
        PinInfo(3, "2A", 60f),
        PinInfo(4, "2B", 80f),
        PinInfo(5, "2C", 100f),
        PinInfo(6, "2Y", 120f),
        PinInfo(7, "GND", 140f)
    )
    val right = listOf(
        PinInfo(14, "VCC", 20f),
        PinInfo(13, "1C", 40f),
        PinInfo(12, "1Y", 60f),
        PinInfo(11, "3C", 80f),
        PinInfo(10, "3B", 100f),
        PinInfo(9, "3A", 120f),
        PinInfo(8, "3Y", 140f)
    )
    drawDIP14Chip(component, textMeasurer, settings, "7411", "3x3 AND", left, right)
}

fun DrawScope.drawIC7420(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    val left = listOf(
        PinInfo(1, "1A", 20f),
        PinInfo(2, "1B", 40f),
        PinInfo(3, "NC", 60f),
        PinInfo(4, "1C", 80f),
        PinInfo(5, "1D", 100f),
        PinInfo(6, "1Y", 120f),
        PinInfo(7, "GND", 140f)
    )
    val right = listOf(
        PinInfo(14, "VCC", 20f),
        PinInfo(13, "2D", 40f),
        PinInfo(12, "2C", 60f),
        PinInfo(11, "NC", 80f),
        PinInfo(10, "2B", 100f),
        PinInfo(9, "2A", 120f),
        PinInfo(8, "2Y", 140f)
    )
    drawDIP14Chip(component, textMeasurer, settings, "7420", "2x4 NAND", left, right)
}

fun DrawScope.drawIC7421(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    val left = listOf(
        PinInfo(1, "1A", 20f),
        PinInfo(2, "1B", 40f),
        PinInfo(3, "NC", 60f),
        PinInfo(4, "1C", 80f),
        PinInfo(5, "1D", 100f),
        PinInfo(6, "1Y", 120f),
        PinInfo(7, "GND", 140f)
    )
    val right = listOf(
        PinInfo(14, "VCC", 20f),
        PinInfo(13, "2D", 40f),
        PinInfo(12, "2C", 60f),
        PinInfo(11, "NC", 80f),
        PinInfo(10, "2B", 100f),
        PinInfo(9, "2A", 120f),
        PinInfo(8, "2Y", 140f)
    )
    drawDIP14Chip(component, textMeasurer, settings, "7421", "2x4 AND", left, right)
}

fun DrawScope.drawIC7427(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    val left = listOf(
        PinInfo(1, "1A", 20f),
        PinInfo(2, "1B", 40f),
        PinInfo(3, "2A", 60f),
        PinInfo(4, "2B", 80f),
        PinInfo(5, "2C", 100f),
        PinInfo(6, "2Y", 120f),
        PinInfo(7, "GND", 140f)
    )
    val right = listOf(
        PinInfo(14, "VCC", 20f),
        PinInfo(13, "1C", 40f),
        PinInfo(12, "1Y", 60f),
        PinInfo(11, "3C", 80f),
        PinInfo(10, "3B", 100f),
        PinInfo(9, "3A", 120f),
        PinInfo(8, "3Y", 140f)
    )
    drawDIP14Chip(component, textMeasurer, settings, "7427", "3x3 NOR", left, right)
}

fun DrawScope.drawIC7430(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    val left = listOf(
        PinInfo(1, "A", 20f),
        PinInfo(2, "B", 40f),
        PinInfo(3, "C", 60f),
        PinInfo(4, "D", 80f),
        PinInfo(5, "E", 100f),
        PinInfo(6, "F", 120f),
        PinInfo(7, "GND", 140f)
    )
    val right = listOf(
        PinInfo(14, "VCC", 20f),
        PinInfo(13, "NC", 40f),
        PinInfo(12, "H", 60f),
        PinInfo(11, "G", 80f),
        PinInfo(10, "NC", 100f),
        PinInfo(9, "NC", 120f),
        PinInfo(8, "Y", 140f)
    )
    drawDIP14Chip(component, textMeasurer, settings, "7430", "8-IN NAND", left, right)
}

fun DrawScope.drawIC74266(
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
    drawDIP14Chip(component, textMeasurer, settings, "74266", "QUAD XNOR", left, right)
}

fun DrawScope.drawIC7473(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    val left = listOf(
        PinInfo(1, "1CLR", 20f),
        PinInfo(2, "1J", 40f),
        PinInfo(3, "1CLK", 60f),
        PinInfo(4, "1K", 80f),
        PinInfo(5, "1Q", 100f),
        PinInfo(6, "1~Q", 120f),
        PinInfo(7, "GND", 140f)
    )
    val right = listOf(
        PinInfo(14, "VCC", 20f),
        PinInfo(13, "2CLR", 40f),
        PinInfo(12, "2J", 60f),
        PinInfo(11, "2CLK", 80f),
        PinInfo(10, "2K", 100f),
        PinInfo(9, "2Q", 120f),
        PinInfo(8, "2~Q", 140f)
    )
    drawDIP14Chip(component, textMeasurer, settings, "7473", "DUAL JK-FF", left, right)
}

fun DrawScope.drawIC7474(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    val left = listOf(
        PinInfo(1, "1CLR", 20f),
        PinInfo(2, "1D", 40f),
        PinInfo(3, "1CLK", 60f),
        PinInfo(4, "1PRE", 80f),
        PinInfo(5, "1Q", 100f),
        PinInfo(6, "1~Q", 120f),
        PinInfo(7, "GND", 140f)
    )
    val right = listOf(
        PinInfo(14, "VCC", 20f),
        PinInfo(13, "2CLR", 40f),
        PinInfo(12, "2D", 60f),
        PinInfo(11, "2CLK", 80f),
        PinInfo(10, "2PRE", 100f),
        PinInfo(9, "2Q", 120f),
        PinInfo(8, "2~Q", 140f)
    )
    drawDIP14Chip(component, textMeasurer, settings, "7474", "DUAL D-FF", left, right)
}

fun DrawScope.drawIC7476(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    val left = listOf(
        PinInfo(1, "1PRE", 20f),
        PinInfo(2, "1CLR", 40f),
        PinInfo(3, "1J", 60f),
        PinInfo(4, "1CLK", 80f),
        PinInfo(5, "1K", 100f),
        PinInfo(6, "1Q", 120f),
        PinInfo(7, "1~Q", 140f),
        PinInfo(8, "GND", 160f)
    )
    val right = listOf(
        PinInfo(16, "VCC", 20f),
        PinInfo(15, "2PRE", 40f),
        PinInfo(14, "2CLR", 60f),
        PinInfo(13, "2J", 80f),
        PinInfo(12, "2CLK", 100f),
        PinInfo(11, "2K", 120f),
        PinInfo(10, "2Q", 140f),
        PinInfo(9, "2~Q", 160f)
    )
    drawDIP14Chip(component, textMeasurer, settings, "7476", "DUAL JK-PC", left, right)
}

fun DrawScope.drawIC74138(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    val left = listOf(
        PinInfo(1, "A", 20f),
        PinInfo(2, "B", 40f),
        PinInfo(3, "C", 60f),
        PinInfo(4, "~G2A", 80f),
        PinInfo(5, "~G2B", 100f),
        PinInfo(6, "G1", 120f),
        PinInfo(7, "~Y7", 140f),
        PinInfo(8, "GND", 160f)
    )
    val right = listOf(
        PinInfo(16, "VCC", 20f),
        PinInfo(15, "~Y0", 40f),
        PinInfo(14, "~Y1", 60f),
        PinInfo(13, "~Y2", 80f),
        PinInfo(12, "~Y3", 100f),
        PinInfo(11, "~Y4", 120f),
        PinInfo(10, "~Y5", 140f),
        PinInfo(9, "~Y6", 160f)
    )
    drawDIP14Chip(component, textMeasurer, settings, "74138", "3:8 DECOD", left, right)
}

fun DrawScope.drawIC74139(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    val left = listOf(
        PinInfo(1, "~1G", 20f),
        PinInfo(2, "1A", 40f),
        PinInfo(3, "1B", 60f),
        PinInfo(4, "1Y0", 80f),
        PinInfo(5, "1Y1", 100f),
        PinInfo(6, "1Y2", 120f),
        PinInfo(7, "1Y3", 140f),
        PinInfo(8, "GND", 160f)
    )
    val right = listOf(
        PinInfo(16, "VCC", 20f),
        PinInfo(15, "~2G", 40f),
        PinInfo(14, "2A", 60f),
        PinInfo(13, "2B", 80f),
        PinInfo(12, "2Y0", 100f),
        PinInfo(11, "2Y1", 120f),
        PinInfo(10, "2Y2", 140f),
        PinInfo(9, "2Y3", 160f)
    )
    drawDIP14Chip(component, textMeasurer, settings, "74139", "2x2:4 DEC", left, right)
}

fun DrawScope.drawIC74151(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    val left = listOf(
        PinInfo(1, "D3", 20f),
        PinInfo(2, "D2", 40f),
        PinInfo(3, "D1", 60f),
        PinInfo(4, "D0", 80f),
        PinInfo(5, "Y", 100f),
        PinInfo(6, "W", 120f),
        PinInfo(7, "~G", 140f),
        PinInfo(8, "GND", 160f)
    )
    val right = listOf(
        PinInfo(16, "VCC", 20f),
        PinInfo(15, "D4", 40f),
        PinInfo(14, "D5", 60f),
        PinInfo(13, "D6", 80f),
        PinInfo(12, "D7", 100f),
        PinInfo(11, "A", 120f),
        PinInfo(10, "B", 140f),
        PinInfo(9, "C", 160f)
    )
    drawDIP14Chip(component, textMeasurer, settings, "74151", "8:1 MUX", left, right)
}

fun DrawScope.drawIC74157(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    val left = listOf(
        PinInfo(1, "SEL", 20f),
        PinInfo(2, "1A", 40f),
        PinInfo(3, "1B", 60f),
        PinInfo(4, "1Y", 80f),
        PinInfo(5, "2A", 100f),
        PinInfo(6, "2B", 120f),
        PinInfo(7, "2Y", 140f),
        PinInfo(8, "GND", 160f)
    )
    val right = listOf(
        PinInfo(16, "VCC", 20f),
        PinInfo(15, "~G", 40f),
        PinInfo(14, "4A", 60f),
        PinInfo(13, "4B", 80f),
        PinInfo(12, "4Y", 100f),
        PinInfo(11, "3A", 120f),
        PinInfo(10, "3B", 140f),
        PinInfo(9, "3Y", 160f)
    )
    drawDIP14Chip(component, textMeasurer, settings, "74157", "4x2:1 MUX", left, right)
}

fun DrawScope.drawIC74283(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    val left = listOf(
        PinInfo(1, "A1", 20f),
        PinInfo(2, "B1", 40f),
        PinInfo(3, "A2", 60f),
        PinInfo(4, "B2", 80f),
        PinInfo(5, "A3", 100f),
        PinInfo(6, "B3", 120f),
        PinInfo(7, "C0", 140f),
        PinInfo(8, "GND", 160f)
    )
    val right = listOf(
        PinInfo(16, "VCC", 20f),
        PinInfo(15, "A4", 40f),
        PinInfo(14, "B4", 60f),
        PinInfo(13, "S1", 80f),
        PinInfo(12, "S2", 100f),
        PinInfo(11, "S3", 120f),
        PinInfo(10, "S4", 140f),
        PinInfo(9, "C4", 160f)
    )
    drawDIP14Chip(component, textMeasurer, settings, "74283", "4B ADDER", left, right)
}
