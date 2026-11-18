package com.example.digisim.StandardComponents

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import com.example.digisim.DrawingLogic.calculateInputPinPositions
import com.example.digisim.ParsingLogic.Component
import com.example.digisim.ParsingLogic.ComponentType
import logicGates.Pin
import logicGates.SimulationComponent

open class ModularBlockComponent(
    id: Int,
    override val componentType: ComponentType,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialWidth: Float = 100f,
    initialHeight: Float = 100f,
    initialInputCount: Int,
    initialOutputCount: Int,
    label: String = "",
    override val abstractedLogic: MutableList<MutableList<SimulationComponent>>? = null,
    override val customComponentName: String? = null
) : Component(id) {
    override var label: String by mutableStateOf(label)
    override var x: Float by mutableStateOf(initialX)
    override var y: Float by mutableStateOf(initialY)
    override var width: Float by mutableStateOf(initialWidth)
    override var height: Float by mutableStateOf(initialHeight)
    override var inputCount: Int by mutableStateOf(initialInputCount)
    override var outputCount: Int by mutableStateOf(initialOutputCount)
    override var delay: Float? = null

    override fun findPortOffset(): Offset = when {
        outputCount == 1 && inputCount == 0 -> Offset(x + width, y + height / 2f)
        inputCount == 1 && outputCount == 0 -> Offset(x, y + height / 2f)
        else -> Offset(x, y + height / 2f)
    }

    override fun inputPortPositions(): List<Offset> {
        if (inputCount <= 0) return emptyList()
        val spacing = height / (inputCount + 1)
        return (1..inputCount).map { i -> Offset(x, y + spacing * i) }
    }

    override fun outputPortPositions(): List<Offset> {
        if (outputCount <= 0) return emptyList()
        val spacing = height / (outputCount + 1)
        return (1..outputCount).map { i -> Offset(x + width, y + spacing * i) }
    }
}

// 1. Gates Expansion
class BufferGate(
    id: Int,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialInputCount: Int = 1,
    initialOutputCount: Int = 1,
    label: String = ""
) : ModularBlockComponent(id, ComponentType.BUFFER, initialX, initialY, 80f, 60f, initialInputCount, initialOutputCount, label)

class ControlledBufferGate(
    id: Int,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialInputCount: Int = 2,
    initialOutputCount: Int = 1,
    label: String = ""
) : ModularBlockComponent(id, ComponentType.CONTROLLED_BUFFER, initialX, initialY, 80f, 60f, initialInputCount, initialOutputCount, label)

class ControlledInverterGate(
    id: Int,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialInputCount: Int = 2,
    initialOutputCount: Int = 1,
    label: String = ""
) : ModularBlockComponent(id, ComponentType.CONTROLLED_INVERTER, initialX, initialY, 80f, 60f, initialInputCount, initialOutputCount, label)

class OddParityGate(
    id: Int,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialInputCount: Int = 2,
    initialOutputCount: Int = 1,
    label: String = ""
) : ModularBlockComponent(id, ComponentType.ODD_PARITY, initialX, initialY, 80f, 60f, initialInputCount, initialOutputCount, label) {
    override fun inputPortPositions(): List<Offset> = calculateInputPinPositions(this)
}

class EvenParityGate(
    id: Int,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialInputCount: Int = 2,
    initialOutputCount: Int = 1,
    label: String = ""
) : ModularBlockComponent(id, ComponentType.EVEN_PARITY, initialX, initialY, 80f, 60f, initialInputCount, initialOutputCount, label) {
    override fun inputPortPositions(): List<Offset> = calculateInputPinPositions(this)
}

// 2. Wiring Expansion
class ConstantHighPin(
    id: Int,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialInputCount: Int = 0,
    initialOutputCount: Int = 1,
    label: String = ""
) : ModularBlockComponent(id, ComponentType.CONSTANT_HIGH, initialX, initialY, 80f, 50f, initialInputCount, initialOutputCount, label) {
    init {
        outputPin = Pin.HIGH
    }
    override fun inputPortPositions(): List<Offset> = emptyList()
    override fun outputPortPositions(): List<Offset> = listOf(Offset(x + width, y + height / 2f))
    override fun findPortOffset(): Offset = Offset(x + width, y + height / 2f)
}

class ConstantLowPin(
    id: Int,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialInputCount: Int = 0,
    initialOutputCount: Int = 1,
    label: String = ""
) : ModularBlockComponent(id, ComponentType.CONSTANT_LOW, initialX, initialY, 80f, 50f, initialInputCount, initialOutputCount, label) {
    init {
        outputPin = Pin.LOW
    }
    override fun inputPortPositions(): List<Offset> = emptyList()
    override fun outputPortPositions(): List<Offset> = listOf(Offset(x + width, y + height / 2f))
    override fun findPortOffset(): Offset = Offset(x + width, y + height / 2f)
}

class ProbePin(
    id: Int,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialInputCount: Int = 1,
    initialOutputCount: Int = 0,
    label: String = ""
) : ModularBlockComponent(id, ComponentType.PROBE, initialX, initialY, 80f, 50f, initialInputCount, initialOutputCount, label) {
    override fun inputPortPositions(): List<Offset> = listOf(Offset(x, y + height / 2f))
    override fun outputPortPositions(): List<Offset> = emptyList()
    override fun findPortOffset(): Offset = Offset(x, y + height / 2f)
}

class TunnelPin(
    id: Int,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialInputCount: Int = 1,
    initialOutputCount: Int = 1,
    label: String = "NET"
) : ModularBlockComponent(id, ComponentType.TUNNEL, initialX, initialY, 80f, 44f, initialInputCount, initialOutputCount, label.ifBlank { "NET" })

// 3. Plexers Library
class Multiplexer2To1Comp(
    id: Int,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialInputCount: Int = 3,
    initialOutputCount: Int = 1,
    label: String = ""
) : ModularBlockComponent(id, ComponentType.MULTIPLEXER_2_TO_1, initialX, initialY, 96f, 80f, initialInputCount, initialOutputCount, label)

class Multiplexer4To1Comp(
    id: Int,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialInputCount: Int = 6,
    initialOutputCount: Int = 1,
    label: String = ""
) : ModularBlockComponent(id, ComponentType.MULTIPLEXER_4_TO_1, initialX, initialY, 100f, 120f, initialInputCount, initialOutputCount, label)

class Demultiplexer1To2Comp(
    id: Int,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialInputCount: Int = 2,
    initialOutputCount: Int = 2,
    label: String = ""
) : ModularBlockComponent(id, ComponentType.DEMULTIPLEXER_1_TO_2, initialX, initialY, 96f, 80f, initialInputCount, initialOutputCount, label)

class Demultiplexer1To4Comp(
    id: Int,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialInputCount: Int = 3,
    initialOutputCount: Int = 4,
    label: String = ""
) : ModularBlockComponent(id, ComponentType.DEMULTIPLEXER_1_TO_4, initialX, initialY, 100f, 100f, initialInputCount, initialOutputCount, label)

class Decoder2To4Comp(
    id: Int,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialInputCount: Int = 3,
    initialOutputCount: Int = 4,
    label: String = ""
) : ModularBlockComponent(id, ComponentType.DECODER_2_TO_4, initialX, initialY, 100f, 100f, initialInputCount, initialOutputCount, label)

class Decoder3To8Comp(
    id: Int,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialInputCount: Int = 4,
    initialOutputCount: Int = 8,
    label: String = ""
) : ModularBlockComponent(id, ComponentType.DECODER_3_TO_8, initialX, initialY, 110f, 160f, initialInputCount, initialOutputCount, label)

class PriorityEncoder4To2Comp(
    id: Int,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialInputCount: Int = 4,
    initialOutputCount: Int = 3,
    label: String = ""
) : ModularBlockComponent(id, ComponentType.PRIORITY_ENCODER_4_TO_2, initialX, initialY, 104f, 100f, initialInputCount, initialOutputCount, label)

// 4. Arithmetic Library
class HalfAdderComp(
    id: Int,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialInputCount: Int = 2,
    initialOutputCount: Int = 2,
    label: String = ""
) : ModularBlockComponent(id, ComponentType.HALF_ADDER, initialX, initialY, 96f, 80f, initialInputCount, initialOutputCount, label)

class FullAdderComp(
    id: Int,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialInputCount: Int = 3,
    initialOutputCount: Int = 2,
    label: String = ""
) : ModularBlockComponent(id, ComponentType.FULL_ADDER, initialX, initialY, 96f, 90f, initialInputCount, initialOutputCount, label)

class Adder4BitComp(
    id: Int,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialInputCount: Int = 9,
    initialOutputCount: Int = 5,
    label: String = ""
) : ModularBlockComponent(id, ComponentType.ADDER_4BIT, initialX, initialY, 116f, 160f, initialInputCount, initialOutputCount, label)

class Subtractor4BitComp(
    id: Int,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialInputCount: Int = 9,
    initialOutputCount: Int = 5,
    label: String = ""
) : ModularBlockComponent(id, ComponentType.SUBTRACTOR_4BIT, initialX, initialY, 116f, 160f, initialInputCount, initialOutputCount, label)

class Comparator4BitComp(
    id: Int,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialInputCount: Int = 8,
    initialOutputCount: Int = 3,
    label: String = ""
) : ModularBlockComponent(id, ComponentType.COMPARATOR_4BIT, initialX, initialY, 110f, 140f, initialInputCount, initialOutputCount, label)

class Negator4BitComp(
    id: Int,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialInputCount: Int = 4,
    initialOutputCount: Int = 4,
    label: String = ""
) : ModularBlockComponent(id, ComponentType.NEGATOR_4BIT, initialX, initialY, 100f, 100f, initialInputCount, initialOutputCount, label)

// 5. Memory Library
class Register4BitComp(
    id: Int,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialInputCount: Int = 7,
    initialOutputCount: Int = 4,
    label: String = ""
) : ModularBlockComponent(id, ComponentType.REGISTER_4BIT, initialX, initialY, 110f, 140f, initialInputCount, initialOutputCount, label) {
    init {
        if (memoryData.isEmpty()) memoryData.add(0)
    }
}

class Counter4BitComp(
    id: Int,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialInputCount: Int = 9,
    initialOutputCount: Int = 5,
    label: String = ""
) : ModularBlockComponent(id, ComponentType.COUNTER_4BIT, initialX, initialY, 116f, 160f, initialInputCount, initialOutputCount, label) {
    init {
        if (memoryData.isEmpty()) memoryData.add(0)
    }
}

class ShiftRegister4BitComp(
    id: Int,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialInputCount: Int = 8,
    initialOutputCount: Int = 4,
    label: String = ""
) : ModularBlockComponent(id, ComponentType.SHIFT_REGISTER_4BIT, initialX, initialY, 116f, 150f, initialInputCount, initialOutputCount, label) {
    init {
        if (memoryData.isEmpty()) memoryData.add(0)
    }
}

class Ram16x4Comp(
    id: Int,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialInputCount: Int = 10,
    initialOutputCount: Int = 4,
    label: String = ""
) : ModularBlockComponent(id, ComponentType.RAM_16X4, initialX, initialY, 120f, 180f, initialInputCount, initialOutputCount, label) {
    init {
        while (memoryData.size < 16) memoryData.add(0)
    }
}

class Rom16x4Comp(
    id: Int,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialInputCount: Int = 4,
    initialOutputCount: Int = 4,
    label: String = ""
) : ModularBlockComponent(id, ComponentType.ROM_16X4, initialX, initialY, 110f, 110f, initialInputCount, initialOutputCount, label) {
    init {
        if (memoryData.isEmpty()) {
            // Default identity lookup table (0..15) so ROM outputs meaningful values out-of-the-box
            for (i in 0 until 16) memoryData.add(i)
        }
    }
}

// 6. Input/Output Peripherals Library
class LedComp(
    id: Int,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialInputCount: Int = 1,
    initialOutputCount: Int = 0,
    label: String = ""
) : ModularBlockComponent(id, ComponentType.LED, initialX, initialY, 60f, 60f, initialInputCount, initialOutputCount, label) {
    override fun inputPortPositions(): List<Offset> = listOf(Offset(x, y + height / 2f))
    override fun outputPortPositions(): List<Offset> = emptyList()
    override fun findPortOffset(): Offset = Offset(x, y + height / 2f)
}

class SevenSegmentDisplayComp(
    id: Int,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialInputCount: Int = 8,
    initialOutputCount: Int = 0,
    label: String = ""
) : ModularBlockComponent(id, ComponentType.SEVEN_SEGMENT_DISPLAY, initialX, initialY, 100f, 150f, initialInputCount, initialOutputCount, label)

class HexDigitDisplayComp(
    id: Int,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialInputCount: Int = 5,
    initialOutputCount: Int = 0,
    label: String = ""
) : ModularBlockComponent(id, ComponentType.HEX_DIGIT_DISPLAY, initialX, initialY, 100f, 140f, initialInputCount, initialOutputCount, label)

class DipSwitch4BitComp(
    id: Int,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialInputCount: Int = 0,
    initialOutputCount: Int = 4,
    label: String = ""
) : ModularBlockComponent(id, ComponentType.DIP_SWITCH_4BIT, initialX, initialY, 84f, 100f, initialInputCount, initialOutputCount, label) {
    init {
        if (memoryData.isEmpty()) memoryData.add(0)
        syncOutputsFromMask()
    }

    fun toggleSwitch(switchIndex: Int) {
        if (switchIndex !in 0..3) return
        if (memoryData.isEmpty()) memoryData.add(0)
        memoryData[0] = (memoryData[0] xor (1 shl switchIndex)) and 0xF
        syncOutputsFromMask()
    }

    fun setSwitch(switchIndex: Int, high: Boolean) {
        if (switchIndex !in 0..3) return
        if (memoryData.isEmpty()) memoryData.add(0)
        val mask = memoryData[0]
        memoryData[0] = if (high) (mask or (1 shl switchIndex)) else (mask and (1 shl switchIndex).inv())
        syncOutputsFromMask()
    }

    fun syncOutputsFromMask() {
        val mask = memoryData.firstOrNull() ?: 0
        outputPins.clear()
        for (bit in 0 until 4) {
            outputPins.add(if ((mask and (1 shl bit)) != 0) Pin.HIGH else Pin.LOW)
        }
        outputPin = outputPins.firstOrNull() ?: Pin.LOW
    }
}
