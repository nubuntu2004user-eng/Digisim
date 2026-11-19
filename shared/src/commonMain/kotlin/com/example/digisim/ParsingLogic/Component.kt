package com.example.digisim.ParsingLogic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import logicGates.SimulationComponent
import logicGates.Pin

@Serializable
abstract class Component(val ID: Int) {
    abstract var x: Float
    abstract var y: Float
    abstract var width: Float
    abstract var height: Float

    abstract var inputCount: Int
    abstract var outputCount: Int

    abstract val componentType: ComponentType

    open var outputPin: Pin by mutableStateOf(Pin.LOW)
    open var label: String by mutableStateOf("")
    open var rotationSteps: Int by mutableStateOf(0)

    val rotationDegrees: Float
        get() = (rotationSteps.mod(4)) * 90f

    fun rotateLeft() {
        rotationSteps = (rotationSteps - 1).mod(4)
    }

    fun rotateRight() {
        rotationSteps = (rotationSteps + 1).mod(4)
    }

    fun rotatePoint(point: Offset): Offset {
        val step = rotationSteps.mod(4)
        if (step == 0) return point
        val cx = x + width / 2f
        val cy = y + height / 2f
        val dx = point.x - cx
        val dy = point.y - cy
        return when (step) {
            1 -> Offset(cx - dy, cy + dx) // 90 deg CW
            2 -> Offset(cx - dx, cy - dy) // 180 deg
            3 -> Offset(cx + dy, cy - dx) // 270 deg CW (90 CCW)
            else -> point
        }
    }

    fun rotatedBounds(): Rect {
        val step = rotationSteps.mod(4)
        return if (step == 1 || step == 3) {
            val cx = x + width / 2f
            val cy = y + height / 2f
            Rect(
                left = cx - height / 2f,
                top = cy - width / 2f,
                right = cx + height / 2f,
                bottom = cy + width / 2f
            )
        } else {
            Rect(left = x, top = y, right = x + width, bottom = y + height)
        }
    }

    fun containsPoint(worldPos: Offset): Boolean {
        val bounds = rotatedBounds()
        return worldPos.x in bounds.left..bounds.right &&
                worldPos.y in bounds.top..bounds.bottom
    }

    @Transient
    val inputPins = mutableStateListOf<Pin>()

    @Transient
    val outputPins = mutableStateListOf<Pin>()

    @Transient
    var prevClockPin: Pin by mutableStateOf(Pin.LOW)

    @Transient
    val memoryData = mutableStateListOf<Int>()

    abstract var delay: Float?

    abstract val abstractedLogic: MutableList<MutableList<SimulationComponent>>?

    abstract val customComponentName: String?
    abstract fun inputPortPositions(): List<Offset>

    abstract fun outputPortPositions(): List<Offset>

    abstract fun findPortOffset(): Offset // for elements with one pin

    fun rotatedInputPortPositions(): List<Offset> =
        inputPortPositions().map { rotatePoint(it) }

    fun rotatedOutputPortPositions(): List<Offset> =
        outputPortPositions().map { rotatePoint(it) }

    fun rotatedFindPortOffset(): Offset =
        rotatePoint(findPortOffset())
}

enum class ComponentType {
    AND, NAND, NOR, OR, XNOR, XOR, NOT,
    BUFFER, CONTROLLED_BUFFER, CONTROLLED_INVERTER, ODD_PARITY, EVEN_PARITY,
    INPUT, OUTPUT, CLOCK, BUTTON,
    CONSTANT_HIGH, CONSTANT_LOW, PROBE, TUNNEL,
    MULTIPLEXER_2_TO_1, MULTIPLEXER_4_TO_1, DEMULTIPLEXER_1_TO_2, DEMULTIPLEXER_1_TO_4,
    DECODER_2_TO_4, DECODER_3_TO_8, PRIORITY_ENCODER_4_TO_2,
    HALF_ADDER, FULL_ADDER, ADDER_4BIT, SUBTRACTOR_4BIT, COMPARATOR_4BIT, NEGATOR_4BIT,
    RS_FLIP_FLOP, JK_FLIP_FLOP, D_FLIP_FLOP, T_FLIP_FLOP,
    JK_FLIP_FLOP_ASYNC, SR_FLIP_FLOP_ASYNC, D_FLIP_FLOP_ASYNC, T_FLIP_FLOP_ASYNC,
    REGISTER_4BIT, COUNTER_4BIT, SHIFT_REGISTER_4BIT, RAM_16X4, ROM_16X4,
    LED, SEVEN_SEGMENT_DISPLAY, HEX_DIGIT_DISPLAY, DIP_SWITCH_4BIT,
    CUSTOM,
    IC_7400, IC_7402, IC_7404, IC_7407, IC_7408,
    IC_7410, IC_7411, IC_7420, IC_7421, IC_7427, IC_7430,
    IC_7432, IC_7473, IC_7474, IC_7476, IC_7486,
    IC_74138, IC_74139, IC_74151, IC_74157, IC_74266, IC_74283
}
