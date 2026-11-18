package com.example.digisim.DrawingLogic

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import com.example.digisim.ComponentsDrawing.drawAnd
import com.example.digisim.ComponentsDrawing.drawButton
import com.example.digisim.ComponentsDrawing.drawClock
import com.example.digisim.ComponentsDrawing.drawCustomComponent
import com.example.digisim.ComponentsDrawing.drawInput
import com.example.digisim.ComponentsDrawing.drawNand
import com.example.digisim.ComponentsDrawing.drawNor
import com.example.digisim.ComponentsDrawing.drawNot
import com.example.digisim.ComponentsDrawing.drawOr
import com.example.digisim.ComponentsDrawing.drawOutput
import com.example.digisim.ComponentsDrawing.drawXnor
import com.example.digisim.ComponentsDrawing.drawXor
import com.example.digisim.ComponentsDrawing.drawDFlipFlop
import com.example.digisim.ComponentsDrawing.drawJKFlipFlop
import com.example.digisim.ComponentsDrawing.drawRSFlipFlop
import com.example.digisim.ComponentsDrawing.drawTFlipFlop
import com.example.digisim.ComponentsDrawing.drawDFlipFlopAsync
import com.example.digisim.ComponentsDrawing.drawJKFlipFlopAsync
import com.example.digisim.ComponentsDrawing.drawSRFlipFlopAsync
import com.example.digisim.ComponentsDrawing.drawTFlipFlopAsync
import com.example.digisim.ComponentsDrawing.drawIC7400
import com.example.digisim.ComponentsDrawing.drawIC7402
import com.example.digisim.ComponentsDrawing.drawIC7404
import com.example.digisim.ComponentsDrawing.drawIC7407
import com.example.digisim.ComponentsDrawing.drawIC7408
import com.example.digisim.ComponentsDrawing.drawIC7410
import com.example.digisim.ComponentsDrawing.drawIC7411
import com.example.digisim.ComponentsDrawing.drawIC7420
import com.example.digisim.ComponentsDrawing.drawIC7421
import com.example.digisim.ComponentsDrawing.drawIC7427
import com.example.digisim.ComponentsDrawing.drawIC7430
import com.example.digisim.ComponentsDrawing.drawIC7432
import com.example.digisim.ComponentsDrawing.drawIC7473
import com.example.digisim.ComponentsDrawing.drawIC7474
import com.example.digisim.ComponentsDrawing.drawIC7476
import com.example.digisim.ComponentsDrawing.drawIC7486
import com.example.digisim.ComponentsDrawing.drawIC74138
import com.example.digisim.ComponentsDrawing.drawIC74139
import com.example.digisim.ComponentsDrawing.drawIC74151
import com.example.digisim.ComponentsDrawing.drawIC74157
import com.example.digisim.ComponentsDrawing.drawIC74266
import com.example.digisim.ComponentsDrawing.drawIC74283
import com.example.digisim.ComponentsDrawing.drawBufferGate
import com.example.digisim.ComponentsDrawing.drawConstantPin
import com.example.digisim.ComponentsDrawing.drawDipSwitch4Bit
import com.example.digisim.ComponentsDrawing.drawLedComponent
import com.example.digisim.ComponentsDrawing.drawParityGate
import com.example.digisim.ComponentsDrawing.drawProbeComponent
import com.example.digisim.ComponentsDrawing.drawSevenSegmentComponent
import com.example.digisim.ComponentsDrawing.drawStandardBlock
import com.example.digisim.ComponentsDrawing.drawTunnelComponent
import com.example.digisim.ParsingLogic.Component
import com.example.digisim.ParsingLogic.ComponentType
import com.example.digisim.SettingsViewModel
import logicGates.Pin

/**
 * Calculates the positions for input ports.
 * Modular structure to support future orientation and size changes.
 */
fun calculateInputPinPositions(component: Component): List<Offset> {
    val inputCount = component.inputCount
    if (inputCount <= 0) return emptyList()

    // Base logic for standard layout (<= 2 inputs)
    if (inputCount <= 2) {
        val spacing = component.height / (inputCount + 1)
        return (1..inputCount).map { i -> Offset(component.x, component.y + spacing * i) }
    }

    // Line-based layout for > 2 inputs
    // Constant spacing
    val pinSpacing = 20f

    // For now, assume vertical orientation (standard)
    val totalHeight = (inputCount - 1) * pinSpacing
    val startY = component.y + (component.height / 2f) - (totalHeight / 2f)

    return (0 until inputCount).map { i ->
        Offset(component.x, startY + (i * pinSpacing))
    }
}

fun getComponentInnerRectColor(pin: Pin, settings: SettingsViewModel = SettingsViewModel.default): Color {
    return settings.getComponentInnerRectColor(pin)
}
// ... existing code ...

fun getComponentTextColor(pin: Pin, settings: SettingsViewModel = SettingsViewModel.default): Color {
    return settings.getComponentTextColor(pin)
}

fun getGateOutlineColor(settings: SettingsViewModel = SettingsViewModel.default): Color {
    return settings.gateOutlineColor
}

fun getPortColor(settings: SettingsViewModel = SettingsViewModel.default): Color {
    return settings.portColor
}


fun getWireHighlightColor(settings: SettingsViewModel = SettingsViewModel.default): Color {
    return settings.wireHighlightColor
}

internal fun DrawScope.drawWire(
    sourceComponent: Component,
    sourcePortIndex: Int,
    targetComponent: Component,
    targetPortIndex: Int,
    color: Color,
) {

    val sourcePos = if (sourceComponent.componentType == ComponentType.INPUT ||
        sourceComponent.componentType == ComponentType.CLOCK ||
        sourceComponent.componentType == ComponentType.BUTTON ||
        sourceComponent.componentType == ComponentType.CONSTANT_HIGH ||
        sourceComponent.componentType == ComponentType.CONSTANT_LOW
    ) {
        sourceComponent.findPortOffset()
    } else {
        sourceComponent.outputPortPositions().getOrElse(sourcePortIndex) { sourceComponent.findPortOffset() }
    }

    val targetPos = if (targetComponent.componentType == ComponentType.OUTPUT ||
        targetComponent.componentType == ComponentType.PROBE ||
        targetComponent.componentType == ComponentType.LED
    ) {
        targetComponent.findPortOffset()
    } else {
        targetComponent.inputPortPositions().getOrElse(targetPortIndex) { targetComponent.findPortOffset() }
    }

    val midX = (sourcePos.x + targetPos.x) / 2f
    val path = Path().apply {
        moveTo(sourcePos.x, sourcePos.y)
        lineTo(midX, sourcePos.y)
        lineTo(midX, targetPos.y)
        lineTo(targetPos.x, targetPos.y)
    }

    drawPath(
        path = path,
        color = color,
        style = Stroke(width = 3f)
    )
}

fun DrawScope.drawComponent(
    component: Component,
    textMeasurer: TextMeasurer,
    settings: SettingsViewModel = SettingsViewModel.default
) {
    when (component.componentType) {
        ComponentType.AND -> { drawAnd(component, textMeasurer, settings) }
        ComponentType.OR -> { drawOr(component, textMeasurer, settings) }
        ComponentType.NAND -> { drawNand(component, textMeasurer, settings) }
        ComponentType.NOR -> { drawNor(component, textMeasurer, settings) }
        ComponentType.XOR -> { drawXor(component, textMeasurer, settings) }
        ComponentType.XNOR -> { drawXnor(component, textMeasurer, settings) }
        ComponentType.NOT -> { drawNot(component, textMeasurer, settings) }
        ComponentType.OUTPUT -> { drawOutput(component, textMeasurer, settings) }
        ComponentType.INPUT -> { drawInput(component, textMeasurer, settings) }
        ComponentType.BUTTON -> { drawButton(component, textMeasurer, settings) }
        ComponentType.CLOCK -> { drawClock(component, textMeasurer, settings) }
        ComponentType.RS_FLIP_FLOP -> { drawRSFlipFlop(component, textMeasurer, settings) }
        ComponentType.JK_FLIP_FLOP -> { drawJKFlipFlop(component, textMeasurer, settings) }
        ComponentType.D_FLIP_FLOP -> { drawDFlipFlop(component, textMeasurer, settings) }
        ComponentType.T_FLIP_FLOP -> { drawTFlipFlop(component, textMeasurer, settings) }
        ComponentType.JK_FLIP_FLOP_ASYNC -> { drawJKFlipFlopAsync(component, textMeasurer, settings) }
        ComponentType.SR_FLIP_FLOP_ASYNC -> { drawSRFlipFlopAsync(component, textMeasurer, settings) }
        ComponentType.D_FLIP_FLOP_ASYNC -> { drawDFlipFlopAsync(component, textMeasurer, settings) }
        ComponentType.T_FLIP_FLOP_ASYNC -> { drawTFlipFlopAsync(component, textMeasurer, settings) }
        ComponentType.IC_7400 -> { drawIC7400(component, textMeasurer, settings) }
        ComponentType.IC_7402 -> { drawIC7402(component, textMeasurer, settings) }
        ComponentType.IC_7404 -> { drawIC7404(component, textMeasurer, settings) }
        ComponentType.IC_7407 -> { drawIC7407(component, textMeasurer, settings) }
        ComponentType.IC_7408 -> { drawIC7408(component, textMeasurer, settings) }
        ComponentType.IC_7410 -> { drawIC7410(component, textMeasurer, settings) }
        ComponentType.IC_7411 -> { drawIC7411(component, textMeasurer, settings) }
        ComponentType.IC_7420 -> { drawIC7420(component, textMeasurer, settings) }
        ComponentType.IC_7421 -> { drawIC7421(component, textMeasurer, settings) }
        ComponentType.IC_7427 -> { drawIC7427(component, textMeasurer, settings) }
        ComponentType.IC_7430 -> { drawIC7430(component, textMeasurer, settings) }
        ComponentType.IC_7432 -> { drawIC7432(component, textMeasurer, settings) }
        ComponentType.IC_7473 -> { drawIC7473(component, textMeasurer, settings) }
        ComponentType.IC_7474 -> { drawIC7474(component, textMeasurer, settings) }
        ComponentType.IC_7476 -> { drawIC7476(component, textMeasurer, settings) }
        ComponentType.IC_7486 -> { drawIC7486(component, textMeasurer, settings) }
        ComponentType.IC_74138 -> { drawIC74138(component, textMeasurer, settings) }
        ComponentType.IC_74139 -> { drawIC74139(component, textMeasurer, settings) }
        ComponentType.IC_74151 -> { drawIC74151(component, textMeasurer, settings) }
        ComponentType.IC_74157 -> { drawIC74157(component, textMeasurer, settings) }
        ComponentType.IC_74266 -> { drawIC74266(component, textMeasurer, settings) }
        ComponentType.IC_74283 -> { drawIC74283(component, textMeasurer, settings) }
        ComponentType.BUFFER -> { drawBufferGate(component, textMeasurer, settings, inverting = false, controlled = false) }
        ComponentType.CONTROLLED_BUFFER -> { drawBufferGate(component, textMeasurer, settings, inverting = false, controlled = true) }
        ComponentType.CONTROLLED_INVERTER -> { drawBufferGate(component, textMeasurer, settings, inverting = true, controlled = true) }
        ComponentType.ODD_PARITY -> { drawParityGate(component, textMeasurer, settings, isOdd = true) }
        ComponentType.EVEN_PARITY -> { drawParityGate(component, textMeasurer, settings, isOdd = false) }
        ComponentType.CONSTANT_HIGH -> { drawConstantPin(component, textMeasurer, settings, isHigh = true) }
        ComponentType.CONSTANT_LOW -> { drawConstantPin(component, textMeasurer, settings, isHigh = false) }
        ComponentType.PROBE -> { drawProbeComponent(component, textMeasurer, settings) }
        ComponentType.TUNNEL -> { drawTunnelComponent(component, textMeasurer, settings) }
        ComponentType.MULTIPLEXER_2_TO_1 -> {
            drawStandardBlock(component, textMeasurer, settings, "MUX", "2:1", listOf("D0", "D1", "S"), listOf("Y"))
        }
        ComponentType.MULTIPLEXER_4_TO_1 -> {
            drawStandardBlock(component, textMeasurer, settings, "MUX", "4:1", listOf("D0", "D1", "D2", "D3", "S0", "S1"), listOf("Y"))
        }
        ComponentType.DEMULTIPLEXER_1_TO_2 -> {
            drawStandardBlock(component, textMeasurer, settings, "DEMUX", "1:2", listOf("D", "S"), listOf("Y0", "Y1"))
        }
        ComponentType.DEMULTIPLEXER_1_TO_4 -> {
            drawStandardBlock(component, textMeasurer, settings, "DEMUX", "1:4", listOf("D", "S0", "S1"), listOf("Y0", "Y1", "Y2", "Y3"))
        }
        ComponentType.DECODER_2_TO_4 -> {
            drawStandardBlock(component, textMeasurer, settings, "DECOD", "2:4", listOf("A0", "A1", "EN"), listOf("Y0", "Y1", "Y2", "Y3"))
        }
        ComponentType.DECODER_3_TO_8 -> {
            drawStandardBlock(component, textMeasurer, settings, "DECOD", "3:8", listOf("A0", "A1", "A2", "EN"), listOf("Y0", "Y1", "Y2", "Y3", "Y4", "Y5", "Y6", "Y7"))
        }
        ComponentType.PRIORITY_ENCODER_4_TO_2 -> {
            drawStandardBlock(component, textMeasurer, settings, "P-ENC", "4:2", listOf("D0", "D1", "D2", "D3"), listOf("A0", "A1", "V"))
        }
        ComponentType.HALF_ADDER -> {
            drawStandardBlock(component, textMeasurer, settings, "H-ADD", "1-BIT", listOf("A", "B"), listOf("S", "C"))
        }
        ComponentType.FULL_ADDER -> {
            drawStandardBlock(component, textMeasurer, settings, "F-ADD", "1-BIT", listOf("A", "B", "CI"), listOf("S", "CO"))
        }
        ComponentType.ADDER_4BIT -> {
            drawStandardBlock(component, textMeasurer, settings, "ADDER", "4-BIT", listOf("A0", "A1", "A2", "A3", "B0", "B1", "B2", "B3", "CI"), listOf("S0", "S1", "S2", "S3", "CO"))
        }
        ComponentType.SUBTRACTOR_4BIT -> {
            drawStandardBlock(component, textMeasurer, settings, "SUB", "4-BIT", listOf("A0", "A1", "A2", "A3", "B0", "B1", "B2", "B3", "BI"), listOf("D0", "D1", "D2", "D3", "BO"))
        }
        ComponentType.COMPARATOR_4BIT -> {
            drawStandardBlock(component, textMeasurer, settings, "COMP", "4-BIT", listOf("A0", "A1", "A2", "A3", "B0", "B1", "B2", "B3"), listOf(">", "=", "<"))
        }
        ComponentType.NEGATOR_4BIT -> {
            drawStandardBlock(component, textMeasurer, settings, "-A", "4-BIT", listOf("A0", "A1", "A2", "A3"), listOf("Y0", "Y1", "Y2", "Y3"))
        }
        ComponentType.REGISTER_4BIT -> {
            drawStandardBlock(component, textMeasurer, settings, "REG", "4-BIT", listOf("D0", "D1", "D2", "D3", "CK", "EN", "CL"), listOf("Q0", "Q1", "Q2", "Q3"))
        }
        ComponentType.COUNTER_4BIT -> {
            drawStandardBlock(component, textMeasurer, settings, "CNT", "4-BIT", listOf("CK", "EN", "U/D", "CL", "LD", "D0", "D1", "D2", "D3"), listOf("Q0", "Q1", "Q2", "Q3", "RC"))
        }
        ComponentType.SHIFT_REGISTER_4BIT -> {
            drawStandardBlock(component, textMeasurer, settings, "SH-REG", "4-BIT", listOf("SI", "CK", "LD", "CL", "D0", "D1", "D2", "D3"), listOf("Q0", "Q1", "Q2", "Q3"))
        }
        ComponentType.RAM_16X4 -> {
            drawStandardBlock(component, textMeasurer, settings, "RAM", "16x4", listOf("A0", "A1", "A2", "A3", "D0", "D1", "D2", "D3", "WE", "CK"), listOf("Q0", "Q1", "Q2", "Q3"))
        }
        ComponentType.ROM_16X4 -> {
            drawStandardBlock(component, textMeasurer, settings, "ROM", "16x4", listOf("A0", "A1", "A2", "A3"), listOf("Q0", "Q1", "Q2", "Q3"))
        }
        ComponentType.LED -> { drawLedComponent(component, textMeasurer, settings) }
        ComponentType.SEVEN_SEGMENT_DISPLAY -> { drawSevenSegmentComponent(component, textMeasurer, settings, isHexDecoded = false) }
        ComponentType.HEX_DIGIT_DISPLAY -> { drawSevenSegmentComponent(component, textMeasurer, settings, isHexDecoded = true) }
        ComponentType.DIP_SWITCH_4BIT -> { drawDipSwitch4Bit(component, textMeasurer, settings) }
        ComponentType.CUSTOM -> { drawCustomComponent(component, textMeasurer, settings) }
    }
}



