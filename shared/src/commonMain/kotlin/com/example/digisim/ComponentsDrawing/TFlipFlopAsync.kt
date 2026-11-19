package com.example.digisim.ComponentsDrawing

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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

fun DrawScope.drawTFlipFlopAsync(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    val x = component.x
    val y = component.y
    val w = component.width
    val h = component.height

    val cornerRadius = CornerRadius(3f, 3f)

    // Body Fill: clean architectural drafting surface
    drawRoundRect(
        color = settings.gateBodyFillColor,
        topLeft = Offset(x, y),
        size = Size(w, h),
        cornerRadius = cornerRadius
    )

    // Body Outline
    drawRoundRect(
        color = getGateOutlineColor(settings),
        topLeft = Offset(x, y),
        size = Size(w, h),
        cornerRadius = cornerRadius,
        style = Stroke(width = 2f)
    )

    val inPositions = component.inputPortPositions()
    val clkIndex = 2

    // Clock Chevron on CLK Pin (pin index 2)
    if (clkIndex in inPositions.indices) {
        val clkPos = inPositions[clkIndex]
        val chevronPath = Path().apply {
            moveTo(x, clkPos.y - 6f)
            lineTo(x + 10f, clkPos.y)
            lineTo(x, clkPos.y + 6f)
        }
        drawPath(
            path = chevronPath,
            color = getGateOutlineColor(settings),
            style = Stroke(width = 2f)
        )
    }

    if (settings.drawText) {
        drawGateSignalPill(
            center = Offset(x + w / 2f, y + h / 2f + 12f),
            pin = component.outputPin,
            settings = settings,
            width = 18f,
            height = 5f
        )

        // Center Type Label
        val centerTextStyle = TextStyle(
            color = settings.textColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        val centerLayout = textMeasurer.measure("T", style = centerTextStyle)
        drawText(
            textMeasurer = textMeasurer,
            text = "T",
            style = centerTextStyle,
            topLeft = Offset(
                x + w / 2f - centerLayout.size.width / 2f,
                y + h / 2f - centerLayout.size.height / 2f - 3f
            )
        )

        // Port Labels Style
        val pinTextStyle = TextStyle(
            color = settings.secondaryTextColor,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace
        )

        // Input Labels: 0: SET, 1: T, 2: CLK, 3: RST
        val inputLabels = listOf("SET", "T", "CLK", "RST")
        inputLabels.forEachIndexed { index, label ->
            if (index in inPositions.indices && label.isNotEmpty()) {
                val pos = inPositions[index]
                val layout = textMeasurer.measure(label, style = pinTextStyle)
                val labelX = if (index == clkIndex) x + 14f else x + 8f
                drawText(
                    textMeasurer = textMeasurer,
                    text = label,
                    style = pinTextStyle,
                    topLeft = Offset(labelX, pos.y - layout.size.height / 2f)
                )
            }
        }

        // Output Labels: Q and Q'
        val outPositions = component.outputPortPositions()
        if (outPositions.isNotEmpty()) {
            val qLayout = textMeasurer.measure("Q", style = pinTextStyle)
            drawText(
                textMeasurer = textMeasurer,
                text = "Q",
                style = pinTextStyle,
                topLeft = Offset(x + w - qLayout.size.width - 8f, outPositions[0].y - qLayout.size.height / 2f)
            )
        }
        if (outPositions.size > 1) {
            val qNotLayout = textMeasurer.measure("Q'", style = pinTextStyle)
            drawText(
                textMeasurer = textMeasurer,
                text = "Q'",
                style = pinTextStyle,
                topLeft = Offset(x + w - qNotLayout.size.width - 8f, outPositions[1].y - qNotLayout.size.height / 2f)
            )
        }
    }

    // Input Port Terminals
    inPositions.forEachIndexed { idx, pos ->
        val pinState = component.inputPins.getOrElse(idx) { Pin.UNDEFINED }
        drawSignalPort(center = pos, pin = pinState, settings = settings)
    }

    // Output Port Terminals
    val outPositions = component.outputPortPositions()
    outPositions.forEachIndexed { idx, pos ->
        val outPinState = component.outputPins.getOrElse(idx) {
            if (idx == 0) component.outputPin else Pin.UNDEFINED
        }
        drawSignalPort(center = pos, pin = outPinState, settings = settings)
    }
}
