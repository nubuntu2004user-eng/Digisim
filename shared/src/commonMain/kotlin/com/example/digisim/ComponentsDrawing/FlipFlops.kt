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

private fun DrawScope.drawFlipFlopBase(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel,
    mainLabel: String,
    inputLabels: List<String>,
    clkInputIndex: Int? = null
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

    // Clock Chevron on Clock Pin
    if (clkInputIndex != null) {
        val inPositions = component.inputPortPositions()
        if (clkInputIndex in inPositions.indices) {
            val clkPos = inPositions[clkInputIndex]
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
    }

    if (settings.drawText) {
        // Output Q state pill indicator below center label
        drawGateSignalPill(
            center = Offset(x + w / 2f, y + h / 2f + 12f),
            pin = component.outputPin,
            settings = settings,
            width = 18f,
            height = 5f
        )

        // Main Center Label
        val centerTextStyle = TextStyle(
            color = settings.textColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        val centerLayout = textMeasurer.measure(mainLabel, style = centerTextStyle)
        drawText(
            textMeasurer = textMeasurer,
            text = mainLabel,
            style = centerTextStyle,
            topLeft = Offset(
                x + w / 2f - centerLayout.size.width / 2f,
                y + h / 2f - centerLayout.size.height / 2f - 3f
            )
        )

        // Pin text style
        val pinTextStyle = TextStyle(
            color = settings.secondaryTextColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace
        )

        // Input Pin Labels
        val inPositions = component.inputPortPositions()
        inputLabels.forEachIndexed { index, label ->
            if (index in inPositions.indices && label.isNotEmpty()) {
                val pos = inPositions[index]
                val layout = textMeasurer.measure(label, style = pinTextStyle)
                drawText(
                    textMeasurer = textMeasurer,
                    text = label,
                    style = pinTextStyle,
                    topLeft = Offset(x + 8f, pos.y - layout.size.height / 2f)
                )
            }
        }

        // Output Pin Labels: Q and Q'
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

    // Input ports
    component.inputPortPositions().forEachIndexed { idx, pos ->
        val pinState = component.inputPins.getOrElse(idx) { Pin.UNDEFINED }
        drawSignalPort(center = pos, pin = pinState, settings = settings)
    }

    // Output ports
    val outPositions = component.outputPortPositions()
    outPositions.forEachIndexed { idx, pos ->
        val outPinState = component.outputPins.getOrElse(idx) {
            if (idx == 0) component.outputPin else Pin.UNDEFINED
        }
        drawSignalPort(center = pos, pin = outPinState, settings = settings)
    }
}

fun DrawScope.drawRSFlipFlop(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    drawFlipFlopBase(
        component = component,
        textMeasurer = textMeasurer,
        settings = settings,
        mainLabel = "RS",
        inputLabels = listOf("S", "R")
    )
}

fun DrawScope.drawJKFlipFlop(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    drawFlipFlopBase(
        component = component,
        textMeasurer = textMeasurer,
        settings = settings,
        mainLabel = "JK",
        inputLabels = listOf("J", "", "K"),
        clkInputIndex = 1
    )
}

fun DrawScope.drawDFlipFlop(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    drawFlipFlopBase(
        component = component,
        textMeasurer = textMeasurer,
        settings = settings,
        mainLabel = "D",
        inputLabels = listOf("D", ""),
        clkInputIndex = 1
    )
}

fun DrawScope.drawTFlipFlop(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    drawFlipFlopBase(
        component = component,
        textMeasurer = textMeasurer,
        settings = settings,
        mainLabel = "T",
        inputLabels = listOf("T", ""),
        clkInputIndex = 1
    )
}
