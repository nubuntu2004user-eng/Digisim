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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.digisim.DrawingLogic.getComponentInnerRectColor
import com.example.digisim.DrawingLogic.getComponentTextColor
import com.example.digisim.DrawingLogic.getGateOutlineColor
import com.example.digisim.DrawingLogic.getPortColor
import com.example.digisim.ParsingLogic.Component
import com.example.digisim.SettingsViewModel

fun DrawScope.drawDFlipFlopAsync(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    val x = component.x
    val y = component.y
    val w = component.width
    val h = component.height

    val cornerRadius = CornerRadius(6f, 6f)

    // Body Fill
    drawRoundRect(
        color = getComponentInnerRectColor(component.outputPin, settings),
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
        val textColor = getComponentTextColor(component.outputPin, settings)

        // Center Type Label
        val centerTextStyle = TextStyle(
            color = textColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        val centerLayout = textMeasurer.measure("D", style = centerTextStyle)
        drawText(
            textMeasurer = textMeasurer,
            text = "D",
            style = centerTextStyle,
            topLeft = Offset(
                x + w / 2f - centerLayout.size.width / 2f,
                y + h / 2f - centerLayout.size.height / 2f
            )
        )

        // Port Labels Style
        val pinTextStyle = TextStyle(
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )

        // Input Labels: 0: SET, 1: D, 2: CLK, 3: RST
        val inputLabels = listOf("SET", "D", "CLK", "RST")
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
    inPositions.forEach { pos ->
        drawCircle(color = getPortColor(settings), radius = 6f, center = pos)
    }

    // Output Port Terminals
    component.outputPortPositions().forEach { pos ->
        drawCircle(color = getPortColor(settings), radius = 6f, center = pos)
    }
}
