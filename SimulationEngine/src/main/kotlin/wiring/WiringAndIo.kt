package wiring

import logicGates.ComponentType
import logicGates.Pin
import logicGates.SimulationComponent
import logicGates.inputWire
import logicGates.outputWire

class ConstantHigh(
    id: Int,
    override var inputs: MutableList<Pin> = mutableListOf(),
    override var output: MutableList<Pin> = mutableListOf(Pin.HIGH),
    override var inputCount: Int = 0,
    override val inputFrom: MutableList<inputWire> = mutableListOf(),
    override val outputTo: MutableList<outputWire> = mutableListOf(),
    override val componentType: ComponentType = ComponentType.CONSTANT_HIGH,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {
    override suspend fun evaluate(): MutableList<Pin> {
        output = mutableListOf(Pin.HIGH)
        return output
    }
}

class ConstantLow(
    id: Int,
    override var inputs: MutableList<Pin> = mutableListOf(),
    override var output: MutableList<Pin> = mutableListOf(Pin.LOW),
    override var inputCount: Int = 0,
    override val inputFrom: MutableList<inputWire> = mutableListOf(),
    override val outputTo: MutableList<outputWire> = mutableListOf(),
    override val componentType: ComponentType = ComponentType.CONSTANT_LOW,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {
    override suspend fun evaluate(): MutableList<Pin> {
        output = mutableListOf(Pin.LOW)
        return output
    }
}

class Probe(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 1,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.PROBE,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {
    override suspend fun evaluate(): MutableList<Pin> {
        val v = inputs.firstOrNull() ?: Pin.UNDEFINED
        output = mutableListOf(v)
        return output
    }
}

class Tunnel(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 1,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.TUNNEL,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {
    override suspend fun evaluate(): MutableList<Pin> {
        val v = inputs.firstOrNull() ?: output.firstOrNull() ?: Pin.LOW
        output = mutableListOf(if (v == Pin.UNDEFINED) (output.firstOrNull() ?: Pin.LOW) else v)
        return output
    }
}

class Led(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 1,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.LED,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {
    override suspend fun evaluate(): MutableList<Pin> {
        val v = inputs.firstOrNull() ?: Pin.LOW
        output = mutableListOf(if (v == Pin.HIGH) Pin.HIGH else Pin.LOW)
        return output
    }
}

class SevenSegmentDisplay(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 8,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.SEVEN_SEGMENT_DISPLAY,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {
    override suspend fun evaluate(): MutableList<Pin> {
        // Inputs: [0..6: segments a..g, 7: dp]
        output = MutableList(8) { idx ->
            if (inputs.getOrElse(idx) { Pin.LOW } == Pin.HIGH) Pin.HIGH else Pin.LOW
        }
        return output
    }
}

class HexDigitDisplay(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 5,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.HEX_DIGIT_DISPLAY,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {
    override suspend fun evaluate(): MutableList<Pin> {
        // Inputs: [0..3: D0..D3 (LSB..MSB), 4: dp]
        output = MutableList(5) { idx ->
            if (inputs.getOrElse(idx) { Pin.LOW } == Pin.HIGH) Pin.HIGH else Pin.LOW
        }
        return output
    }
}

class DipSwitch4Bit(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 0,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.DIP_SWITCH_4BIT,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {
    override suspend fun evaluate(): MutableList<Pin> {
        if (output.size < 4) {
            val mask = statefulMemory.firstOrNull() ?: 0
            output = MutableList(4) { bit -> if ((mask and (1 shl bit)) != 0) Pin.HIGH else Pin.LOW }
        }
        return output
    }
}
