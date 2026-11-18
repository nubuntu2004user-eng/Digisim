package plexers

import logicGates.ComponentType
import logicGates.Pin
import logicGates.SimulationComponent
import logicGates.inputWire
import logicGates.outputWire

class Multiplexer2To1(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 3,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.MULTIPLEXER_2_TO_1,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {
    override suspend fun evaluate(): MutableList<Pin> {
        // Inputs: [0: D0, 1: D1, 2: SEL]
        val d0 = inputs.getOrElse(0) { Pin.LOW }
        val d1 = inputs.getOrElse(1) { Pin.LOW }
        val sel = inputs.getOrElse(2) { Pin.LOW }
        val out = when (sel) {
            Pin.LOW, Pin.UNDEFINED -> if (d0 == Pin.ERROR) Pin.ERROR else if (d0 == Pin.HIGH) Pin.HIGH else Pin.LOW
            Pin.HIGH -> if (d1 == Pin.ERROR) Pin.ERROR else if (d1 == Pin.HIGH) Pin.HIGH else Pin.LOW
            Pin.ERROR -> Pin.ERROR
        }
        output = mutableListOf(out)
        return output
    }
}

class Multiplexer4To1(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 6,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.MULTIPLEXER_4_TO_1,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {
    override suspend fun evaluate(): MutableList<Pin> {
        // Inputs: [0..3: D0..D3, 4: SEL0 (LSB), 5: SEL1 (MSB)]
        val s0 = inputs.getOrElse(4) { Pin.LOW }
        val s1 = inputs.getOrElse(5) { Pin.LOW }
        if (s0 == Pin.ERROR || s1 == Pin.ERROR) {
            output = mutableListOf(Pin.ERROR)
            return output
        }
        val index = (if (s1 == Pin.HIGH) 2 else 0) or (if (s0 == Pin.HIGH) 1 else 0)
        val picked = inputs.getOrElse(index) { Pin.LOW }
        output = mutableListOf(if (picked == Pin.ERROR) Pin.ERROR else if (picked == Pin.HIGH) Pin.HIGH else Pin.LOW)
        return output
    }
}

class Demultiplexer1To2(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 2,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.DEMULTIPLEXER_1_TO_2,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {
    override suspend fun evaluate(): MutableList<Pin> {
        // Inputs: [0: D, 1: SEL], Outputs: [0: Y0, 1: Y1]
        val d = inputs.getOrElse(0) { Pin.LOW }
        val sel = inputs.getOrElse(1) { Pin.LOW }
        if (d == Pin.ERROR || sel == Pin.ERROR) {
            output = mutableListOf(Pin.ERROR, Pin.ERROR)
            return output
        }
        val dBit = if (d == Pin.HIGH) Pin.HIGH else Pin.LOW
        output = if (sel == Pin.HIGH) {
            mutableListOf(Pin.LOW, dBit)
        } else {
            mutableListOf(dBit, Pin.LOW)
        }
        return output
    }
}

class Demultiplexer1To4(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 3,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.DEMULTIPLEXER_1_TO_4,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {
    override suspend fun evaluate(): MutableList<Pin> {
        // Inputs: [0: D, 1: SEL0 (LSB), 2: SEL1 (MSB)], Outputs: [0..3: Y0..Y3]
        val d = inputs.getOrElse(0) { Pin.LOW }
        val s0 = inputs.getOrElse(1) { Pin.LOW }
        val s1 = inputs.getOrElse(2) { Pin.LOW }
        if (d == Pin.ERROR || s0 == Pin.ERROR || s1 == Pin.ERROR) {
            output = MutableList(4) { Pin.ERROR }
            return output
        }
        val sel = (if (s1 == Pin.HIGH) 2 else 0) or (if (s0 == Pin.HIGH) 1 else 0)
        val dBit = if (d == Pin.HIGH) Pin.HIGH else Pin.LOW
        output = MutableList(4) { idx -> if (idx == sel) dBit else Pin.LOW }
        return output
    }
}

class Decoder2To4(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 3,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.DECODER_2_TO_4,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {
    override suspend fun evaluate(): MutableList<Pin> {
        // Inputs: [0: A0 (LSB), 1: A1 (MSB), 2: EN (default HIGH)], Outputs: [0..3: Y0..Y3 (active-HIGH)]
        val a0 = inputs.getOrElse(0) { Pin.LOW }
        val a1 = inputs.getOrElse(1) { Pin.LOW }
        val enRaw = inputs.getOrElse(2) { Pin.HIGH }
        val en = if (enRaw == Pin.UNDEFINED) Pin.HIGH else enRaw
        if (a0 == Pin.ERROR || a1 == Pin.ERROR || en == Pin.ERROR) {
            output = MutableList(4) { Pin.ERROR }
            return output
        }
        if (en == Pin.LOW) {
            output = MutableList(4) { Pin.LOW }
            return output
        }
        val sel = (if (a1 == Pin.HIGH) 2 else 0) or (if (a0 == Pin.HIGH) 1 else 0)
        output = MutableList(4) { idx -> if (idx == sel) Pin.HIGH else Pin.LOW }
        return output
    }
}

class Decoder3To8(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 4,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.DECODER_3_TO_8,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {
    override suspend fun evaluate(): MutableList<Pin> {
        // Inputs: [0: A0 (LSB), 1: A1, 2: A2 (MSB), 3: EN (default HIGH)], Outputs: [0..7: Y0..Y7 (active-HIGH)]
        val a0 = inputs.getOrElse(0) { Pin.LOW }
        val a1 = inputs.getOrElse(1) { Pin.LOW }
        val a2 = inputs.getOrElse(2) { Pin.LOW }
        val enRaw = inputs.getOrElse(3) { Pin.HIGH }
        val en = if (enRaw == Pin.UNDEFINED) Pin.HIGH else enRaw
        if (a0 == Pin.ERROR || a1 == Pin.ERROR || a2 == Pin.ERROR || en == Pin.ERROR) {
            output = MutableList(8) { Pin.ERROR }
            return output
        }
        if (en == Pin.LOW) {
            output = MutableList(8) { Pin.LOW }
            return output
        }
        val sel = (if (a2 == Pin.HIGH) 4 else 0) or (if (a1 == Pin.HIGH) 2 else 0) or (if (a0 == Pin.HIGH) 1 else 0)
        output = MutableList(8) { idx -> if (idx == sel) Pin.HIGH else Pin.LOW }
        return output
    }
}

class PriorityEncoder4To2(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 4,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.PRIORITY_ENCODER_4_TO_2,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {
    override suspend fun evaluate(): MutableList<Pin> {
        // Inputs: [0..3: D0..D3], Outputs: [0: A0 (LSB), 1: A1 (MSB), 2: VALID]
        if (inputs.take(4).any { it == Pin.ERROR }) {
            output = mutableListOf(Pin.ERROR, Pin.ERROR, Pin.ERROR)
            return output
        }
        val d0 = inputs.getOrElse(0) { Pin.LOW } == Pin.HIGH
        val d1 = inputs.getOrElse(1) { Pin.LOW } == Pin.HIGH
        val d2 = inputs.getOrElse(2) { Pin.LOW } == Pin.HIGH
        val d3 = inputs.getOrElse(3) { Pin.LOW } == Pin.HIGH

        output = when {
            d3 -> mutableListOf(Pin.HIGH, Pin.HIGH, Pin.HIGH) // 3
            d2 -> mutableListOf(Pin.LOW, Pin.HIGH, Pin.HIGH)  // 2
            d1 -> mutableListOf(Pin.HIGH, Pin.LOW, Pin.HIGH)  // 1
            d0 -> mutableListOf(Pin.LOW, Pin.LOW, Pin.HIGH)   // 0
            else -> mutableListOf(Pin.LOW, Pin.LOW, Pin.LOW)  // None active
        }
        return output
    }
}
