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
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.digisim.DrawingLogic.drawSignalPort
import com.example.digisim.DrawingLogic.getComponentInnerRectColor
import com.example.digisim.DrawingLogic.getGateOutlineColor
import com.example.digisim.DrawingLogic.getPortColor
import com.example.digisim.ParsingLogic.Component
import com.example.digisim.SettingsViewModel
import logicGates.Pin

internal fun DrawScope.drawStandardBlock(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel,
    title: String,
    subtitle: String? = null,
    inputLabels: List<String> = emptyList(),
    outputLabels: List<String> = emptyList()
) {
    val x = component.x
    val y = component.y
    val w = component.width
    val h = component.height
    val outlineColor = getGateOutlineColor(settings)

    drawRoundRect(
        color = settings.gateBodyFillColor,
        topLeft = Offset(x, y),
        size = Size(w, h),
        cornerRadius = CornerRadius(3f, 3f)
    )
    drawRoundRect(
        color = outlineColor,
        topLeft = Offset(x, y),
        size = Size(w, h),
        cornerRadius = CornerRadius(3f, 3f),
        style = Stroke(width = 2f)
    )

    if (settings.drawText) {
        val titleStyle = TextStyle(
            color = settings.textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        val titleLayout = textMeasurer.measure(title, style = titleStyle)
        val centerY = if (subtitle != null) y + h / 2f - titleLayout.size.height / 2f - 5f else y + h / 2f - titleLayout.size.height / 2f
        drawText(
            textMeasurer = textMeasurer,
            text = title,
            style = titleStyle,
            topLeft = Offset(x + w / 2f - titleLayout.size.width / 2f, centerY)
        )

        if (subtitle != null) {
            val subStyle = TextStyle(
                color = settings.secondaryTextColor,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace
            )
            val subLayout = textMeasurer.measure(subtitle, style = subStyle)
            drawText(
                textMeasurer = textMeasurer,
                text = subtitle,
                style = subStyle,
                topLeft = Offset(x + w / 2f - subLayout.size.width / 2f, y + h / 2f + 4f)
            )
        }

        val pinStyle = TextStyle(
            color = settings.secondaryTextColor,
            fontSize = 8.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace
        )

        val inPositions = component.inputPortPositions()
        inPositions.forEachIndexed { idx, pos ->
            val lbl = inputLabels.getOrNull(idx)
            if (lbl != null) {
                val layout = textMeasurer.measure(lbl, style = pinStyle)
                drawText(
                    textMeasurer = textMeasurer,
                    text = lbl,
                    style = pinStyle,
                    topLeft = Offset(pos.x + 7f, pos.y - layout.size.height / 2f)
                )
            }
        }

        val outPositions = component.outputPortPositions()
        outPositions.forEachIndexed { idx, pos ->
            val lbl = outputLabels.getOrNull(idx)
            if (lbl != null) {
                val layout = textMeasurer.measure(lbl, style = pinStyle)
                drawText(
                    textMeasurer = textMeasurer,
                    text = lbl,
                    style = pinStyle,
                    topLeft = Offset(pos.x - layout.size.width - 7f, pos.y - layout.size.height / 2f)
                )
            }
        }
    }

    component.inputPortPositions().forEachIndexed { idx, pos ->
        val pinState = component.inputPins.getOrElse(idx) { Pin.UNDEFINED }
        drawSignalPort(center = pos, pin = pinState, settings = settings, radius = 5f)
    }
    component.outputPortPositions().forEachIndexed { idx, pos ->
        val pinState = component.outputPins.getOrElse(idx) {
            if (idx == 0) component.outputPin else Pin.UNDEFINED
        }
        drawSignalPort(center = pos, pin = pinState, settings = settings, radius = 5f)
    }
}

fun DrawScope.drawBufferGate(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default,
    inverting: Boolean = false,
    controlled: Boolean = false
) {
    val x = component.x
    val y = component.y
    val w = component.width
    val h = component.height
    val outlineColor = getGateOutlineColor(settings)
    val tipX = if (inverting) x + w - 12f else x + w

    val path = Path().apply {
        moveTo(x, y + 6f)
        lineTo(tipX, y + h / 2f)
        lineTo(x, y + h - 6f)
        close()
    }
    drawPath(path = path, color = settings.gateBodyFillColor)
    drawPath(path = path, color = outlineColor, style = Stroke(width = 2f))

    if (inverting) {
        drawCircle(
            color = settings.gateBodyFillColor,
            radius = 5f,
            center = Offset(x + w - 6f, y + h / 2f)
        )
        drawCircle(
            color = outlineColor,
            radius = 5f,
            center = Offset(x + w - 6f, y + h / 2f),
            style = Stroke(width = 2f)
        )
    }

    if (settings.drawText) {
        val label = when {
            controlled && inverting -> "TRI-INV"
            controlled -> "TRI-BUF"
            else -> "BUF"
        }
        val style = TextStyle(
            color = settings.textColor,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        val layout = textMeasurer.measure(label, style = style)
        drawText(
            textMeasurer = textMeasurer,
            text = label,
            style = style,
            topLeft = Offset(x + 8f, y + h / 2f - layout.size.height / 2f)
        )
    }

    component.inputPortPositions().forEachIndexed { idx, pos ->
        val pinState = component.inputPins.getOrElse(idx) { Pin.UNDEFINED }
        drawSignalPort(center = pos, pin = pinState, settings = settings)
    }
    component.outputPortPositions().forEach { pos ->
        drawSignalPort(center = pos, pin = component.outputPin, settings = settings)
    }
}

fun DrawScope.drawParityGate(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default,
    isOdd: Boolean
) {
    val symbol = if (isOdd) "2k+1" else "2k"
    val sub = if (isOdd) "ODD PAR" else "EVEN PAR"
    drawStandardBlock(component, textMeasurer, settings, symbol, sub)
}

fun DrawScope.drawConstantPin(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default,
    isHigh: Boolean
) {
    val pin = if (isHigh) Pin.HIGH else Pin.LOW
    val outlineColor = getGateOutlineColor(settings)
    val innerColor = if (isHigh) settings.highPinColor else settings.ioIdleFillColor
    val textColor = if (isHigh) settings.textHighColor else settings.textColor

    drawRoundRect(
        color = innerColor,
        topLeft = Offset(component.x, component.y),
        size = Size(component.width, component.height),
        cornerRadius = CornerRadius(3f, 3f)
    )
    drawRoundRect(
        color = outlineColor,
        topLeft = Offset(component.x, component.y),
        size = Size(component.width, component.height),
        cornerRadius = CornerRadius(3f, 3f),
        style = Stroke(width = 2f)
    )

    if (settings.drawText) {
        val text = if (isHigh) "1 (VCC)" else "0 (GND)"
        val style = TextStyle(
            color = textColor,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        val layout = textMeasurer.measure(text, style = style)
        drawText(
            textMeasurer = textMeasurer,
            text = text,
            style = style,
            topLeft = Offset(
                component.x + component.width / 2f - layout.size.width / 2f,
                component.y + component.height / 2f - layout.size.height / 2f
            )
        )
    }

    drawSignalPort(
        center = component.findPortOffset(),
        pin = pin,
        settings = settings
    )
}

fun DrawScope.drawProbeComponent(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    val pinState = component.inputPins.firstOrNull() ?: component.outputPin
    val outlineColor = getGateOutlineColor(settings)
    val fill = when (pinState) {
        Pin.HIGH -> settings.highPinColor
        Pin.ERROR -> settings.errorPinColor
        Pin.LOW, Pin.UNDEFINED -> settings.ioIdleFillColor
    }
    val textColor = when (pinState) {
        Pin.HIGH -> settings.textHighColor
        Pin.ERROR -> settings.primaryControlText
        Pin.LOW, Pin.UNDEFINED -> settings.textColor
    }

    drawRoundRect(
        color = fill,
        topLeft = Offset(component.x, component.y),
        size = Size(component.width, component.height),
        cornerRadius = CornerRadius(3f, 3f)
    )
    drawRoundRect(
        color = outlineColor,
        topLeft = Offset(component.x, component.y),
        size = Size(component.width, component.height),
        cornerRadius = CornerRadius(3f, 3f),
        style = Stroke(width = 2f)
    )

    val text = when (pinState) {
        Pin.HIGH -> "PRB: 1"
        Pin.LOW -> "PRB: 0"
        Pin.ERROR -> "PRB: E"
        Pin.UNDEFINED -> "PRB: ?"
    }
    val style = TextStyle(
        color = textColor,
        fontSize = 10.5.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace
    )
    val layout = textMeasurer.measure(text, style = style)
    drawText(
        textMeasurer = textMeasurer,
        text = text,
        style = style,
        topLeft = Offset(
            component.x + component.width / 2f - layout.size.width / 2f,
            component.y + component.height / 2f - layout.size.height / 2f
        )
    )

    component.inputPortPositions().forEach { pos ->
        drawSignalPort(center = pos, pin = pinState, settings = settings)
    }
}

fun DrawScope.drawTunnelComponent(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    val x = component.x
    val y = component.y
    val w = component.width
    val h = component.height
    val outlineColor = getGateOutlineColor(settings)
    val path = Path().apply {
        moveTo(x, y + h / 2f)
        lineTo(x + 14f, y + 6f)
        lineTo(x + w, y + 6f)
        lineTo(x + w, y + h - 6f)
        lineTo(x + 14f, y + h - 6f)
        close()
    }
    drawPath(path = path, color = settings.ioIdleFillColor)
    drawPath(path = path, color = outlineColor, style = Stroke(width = 2f))

    val netName = component.label.ifBlank { "NET" }
    val style = TextStyle(
        color = settings.textColor,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace
    )
    val layout = textMeasurer.measure(netName, style = style)
    drawText(
        textMeasurer = textMeasurer,
        text = netName,
        style = style,
        topLeft = Offset(x + 16f, y + h / 2f - layout.size.height / 2f)
    )

    component.inputPortPositions().forEachIndexed { idx, pos ->
        val pinState = component.inputPins.getOrElse(idx) { Pin.UNDEFINED }
        drawSignalPort(center = pos, pin = pinState, settings = settings, radius = 5f)
    }
    component.outputPortPositions().forEach { pos ->
        drawSignalPort(center = pos, pin = component.outputPin, settings = settings, radius = 5f)
    }
}

fun DrawScope.drawLedComponent(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    val pinState = component.inputPins.firstOrNull() ?: component.outputPin
    val isLit = pinState == Pin.HIGH
    val center = Offset(component.x + component.width / 2f + 4f, component.y + component.height / 2f)
    val radius = minOf(component.width, component.height) / 2f - 6f
    val outlineColor = getGateOutlineColor(settings)
    val lampColor = if (isLit) settings.highPinColor else settings.ioIdleFillColor

    drawCircle(color = lampColor, radius = radius, center = center)
    drawCircle(color = outlineColor, radius = radius, center = center, style = Stroke(width = 2f))
    drawCircle(
        color = if (isLit) Color.White.copy(alpha = 0.85f) else outlineColor.copy(alpha = 0.25f),
        radius = radius * 0.4f,
        center = center
    )

    if (settings.drawText) {
        val style = TextStyle(
            color = if (isLit) settings.textHighColor else settings.textColor,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        val lbl = if (isLit) "ON" else "LED"
        val layout = textMeasurer.measure(lbl, style = style)
        drawText(
            textMeasurer = textMeasurer,
            text = lbl,
            style = style,
            topLeft = Offset(center.x - layout.size.width / 2f, center.y - layout.size.height / 2f)
        )
    }

    component.inputPortPositions().forEach { pos ->
        drawSignalPort(center = pos, pin = pinState, settings = settings)
    }
}

private val hexSegmentMasks = intArrayOf(
    0b0111111, // 0: a,b,c,d,e,f
    0b0000110, // 1: b,c
    0b1011011, // 2: a,b,d,e,g
    0b1001111, // 3: a,b,c,d,g
    0b1100110, // 4: b,c,f,g
    0b1101101, // 5: a,c,d,f,g
    0b1111101, // 6: a,c,d,e,f,g
    0b0000111, // 7: a,b,c
    0b1111111, // 8: a..g
    0b1101111, // 9: a,b,c,d,f,g
    0b1110111, // A: a,b,c,e,f,g
    0b1111100, // b: c,d,e,f,g
    0b0111001, // C: a,d,e,f
    0b1011110, // d: b,c,d,e,g
    0b1111001, // E: a,d,e,f,g
    0b1110001  // F: a,e,f,g
)

fun DrawScope.drawSevenSegmentComponent(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default,
    isHexDecoded: Boolean = false
) {
    val x = component.x
    val y = component.y
    val w = component.width
    val h = component.height
    val outlineColor = getGateOutlineColor(settings)

    // Dark high-contrast display bezel
    drawRoundRect(
        color = Color(0xFF181816),
        topLeft = Offset(x, y),
        size = Size(w, h),
        cornerRadius = CornerRadius(4f, 4f)
    )
    drawRoundRect(
        color = outlineColor,
        topLeft = Offset(x, y),
        size = Size(w, h),
        cornerRadius = CornerRadius(4f, 4f),
        style = Stroke(width = 2f)
    )

    val pins = if (component.inputPins.isNotEmpty()) component.inputPins else component.outputPins
    val mask: Int
    val dpLit: Boolean
    if (isHexDecoded) {
        val b0 = if (pins.getOrNull(0) == Pin.HIGH) 1 else 0
        val b1 = if (pins.getOrNull(1) == Pin.HIGH) 2 else 0
        val b2 = if (pins.getOrNull(2) == Pin.HIGH) 4 else 0
        val b3 = if (pins.getOrNull(3) == Pin.HIGH) 8 else 0
        val nibble = (b0 or b1 or b2 or b3) and 0xF
        mask = hexSegmentMasks[nibble]
        dpLit = pins.getOrNull(4) == Pin.HIGH
    } else {
        var m = 0
        for (seg in 0 until 7) {
            if (pins.getOrNull(seg) == Pin.HIGH) m = m or (1 shl seg)
        }
        mask = m
        dpLit = pins.getOrNull(7) == Pin.HIGH
    }

    val litColor = Color(0xFFF59E0B)
    val dimColor = Color(0xFF2E2D2A)

    val leftX = x + 32f
    val rightX = x + w - 22f
    val topY = y + 20f
    val midY = y + h / 2f
    val botY = y + h - 22f
    val segThickness = 6f

    // Segment a (top horizontal)
    drawLine(
        color = if ((mask and (1 shl 0)) != 0) litColor else dimColor,
        start = Offset(leftX + 4f, topY),
        end = Offset(rightX - 4f, topY),
        strokeWidth = segThickness
    )
    // Segment b (top-right vertical)
    drawLine(
        color = if ((mask and (1 shl 1)) != 0) litColor else dimColor,
        start = Offset(rightX, topY + 4f),
        end = Offset(rightX, midY - 4f),
        strokeWidth = segThickness
    )
    // Segment c (bottom-right vertical)
    drawLine(
        color = if ((mask and (1 shl 2)) != 0) litColor else dimColor,
        start = Offset(rightX, midY + 4f),
        end = Offset(rightX, botY - 4f),
        strokeWidth = segThickness
    )
    // Segment d (bottom horizontal)
    drawLine(
        color = if ((mask and (1 shl 3)) != 0) litColor else dimColor,
        start = Offset(leftX + 4f, botY),
        end = Offset(rightX - 4f, botY),
        strokeWidth = segThickness
    )
    // Segment e (bottom-left vertical)
    drawLine(
        color = if ((mask and (1 shl 4)) != 0) litColor else dimColor,
        start = Offset(leftX, midY + 4f),
        end = Offset(leftX, botY - 4f),
        strokeWidth = segThickness
    )
    // Segment f (top-left vertical)
    drawLine(
        color = if ((mask and (1 shl 5)) != 0) litColor else dimColor,
        start = Offset(leftX, topY + 4f),
        end = Offset(leftX, midY - 4f),
        strokeWidth = segThickness
    )
    // Segment g (middle horizontal)
    drawLine(
        color = if ((mask and (1 shl 6)) != 0) litColor else dimColor,
        start = Offset(leftX + 4f, midY),
        end = Offset(rightX - 4f, midY),
        strokeWidth = segThickness
    )
    // Decimal point (dp)
    drawCircle(
        color = if (dpLit) litColor else dimColor,
        radius = 4f,
        center = Offset(rightX + 10f, botY)
    )

    if (settings.drawText) {
        val pinLabels = if (isHexDecoded) listOf("D0", "D1", "D2", "D3", "DP") else listOf("a", "b", "c", "d", "e", "f", "g", "dp")
        val pinStyle = TextStyle(
            color = settings.chipPinNumberColor,
            fontSize = 8.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace
        )
        component.inputPortPositions().forEachIndexed { idx, pos ->
            val lbl = pinLabels.getOrNull(idx) ?: ""
            val layout = textMeasurer.measure(lbl, style = pinStyle)
            drawText(
                textMeasurer = textMeasurer,
                text = lbl,
                style = pinStyle,
                topLeft = Offset(pos.x + 6f, pos.y - layout.size.height / 2f)
            )
        }
    }

    component.inputPortPositions().forEachIndexed { idx, pos ->
        val pinState = component.inputPins.getOrElse(idx) { Pin.UNDEFINED }
        drawSignalPort(center = pos, pin = pinState, settings = settings, radius = 5f)
    }
}

fun DrawScope.drawDipSwitch4Bit(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    val x = component.x
    val y = component.y
    val w = component.width
    val h = component.height
    val outlineColor = getGateOutlineColor(settings)

    drawRoundRect(
        color = settings.gateBodyFillColor,
        topLeft = Offset(x, y),
        size = Size(w, h),
        cornerRadius = CornerRadius(3f, 3f)
    )
    drawRoundRect(
        color = outlineColor,
        topLeft = Offset(x, y),
        size = Size(w, h),
        cornerRadius = CornerRadius(3f, 3f),
        style = Stroke(width = 2f)
    )

    val mask = component.memoryData.firstOrNull() ?: 0
    val outPositions = component.outputPortPositions()

    outPositions.forEachIndexed { idx, portPos ->
        val isOn = (mask and (1 shl idx)) != 0
        val rowY = portPos.y - 8f
        val fill = if (isOn) settings.highPinColor else settings.ioIdleFillColor
        val rowTextColor = if (isOn) settings.textHighColor else settings.textColor
        drawRoundRect(
            color = fill,
            topLeft = Offset(x + 8f, rowY),
            size = Size(w - 18f, 16f),
            cornerRadius = CornerRadius(2f, 2f)
        )
        drawRoundRect(
            color = settings.borderColor,
            topLeft = Offset(x + 8f, rowY),
            size = Size(w - 18f, 16f),
            cornerRadius = CornerRadius(2f, 2f),
            style = Stroke(width = 1f)
        )
        if (settings.drawText) {
            val pinStyle = TextStyle(
                color = rowTextColor,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            val txt = "SW$idx:${if (isOn) "1" else "0"}"
            val layout = textMeasurer.measure(txt, style = pinStyle)
            drawText(
                textMeasurer = textMeasurer,
                text = txt,
                style = pinStyle,
                topLeft = Offset(x + 14f, portPos.y - layout.size.height / 2f)
            )
        }
        drawSignalPort(
            center = portPos,
            pin = if (isOn) Pin.HIGH else Pin.LOW,
            settings = settings,
            radius = 5f
        )
    }
}
