package arithmetic

import logicGates.ComponentType
import logicGates.Pin
import logicGates.SimulationComponent
import logicGates.inputWire
import logicGates.outputWire

class HalfAdder(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 2,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.HALF_ADDER,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {
    override suspend fun evaluate(): MutableList<Pin> {
        // Inputs: [0: A, 1: B], Outputs: [0: SUM, 1: CARRY]
        val a = inputs.getOrElse(0) { Pin.LOW }
        val b = inputs.getOrElse(1) { Pin.LOW }
        if (a == Pin.ERROR || b == Pin.ERROR) {
            output = mutableListOf(Pin.ERROR, Pin.ERROR)
            return output
        }
        val aBit = if (a == Pin.HIGH) 1 else 0
        val bBit = if (b == Pin.HIGH) 1 else 0
        val sum = aBit xor bBit
        val carry = aBit and bBit
        output = mutableListOf(
            if (sum == 1) Pin.HIGH else Pin.LOW,
            if (carry == 1) Pin.HIGH else Pin.LOW
        )
        return output
    }
}

class FullAdder(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 3,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.FULL_ADDER,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {
    override suspend fun evaluate(): MutableList<Pin> {
        // Inputs: [0: A, 1: B, 2: CIN], Outputs: [0: SUM, 1: COUT]
        val a = inputs.getOrElse(0) { Pin.LOW }
        val b = inputs.getOrElse(1) { Pin.LOW }
        val cin = inputs.getOrElse(2) { Pin.LOW }
        if (a == Pin.ERROR || b == Pin.ERROR || cin == Pin.ERROR) {
            output = mutableListOf(Pin.ERROR, Pin.ERROR)
            return output
        }
        val total = (if (a == Pin.HIGH) 1 else 0) + (if (b == Pin.HIGH) 1 else 0) + (if (cin == Pin.HIGH) 1 else 0)
        output = mutableListOf(
            if ((total and 1) != 0) Pin.HIGH else Pin.LOW,
            if ((total and 2) != 0) Pin.HIGH else Pin.LOW
        )
        return output
    }
}

class Adder4Bit(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 9,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.ADDER_4BIT,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {
    override suspend fun evaluate(): MutableList<Pin> {
        // Inputs: [0..3: A0..A3, 4..7: B0..B3, 8: CIN], Outputs: [0..3: S0..S3, 4: COUT]
        if (inputs.take(9).any { it == Pin.ERROR }) {
            output = MutableList(5) { Pin.ERROR }
            return output
        }
        var aVal = 0
        var bVal = 0
        for (bit in 0 until 4) {
            if (inputs.getOrElse(bit) { Pin.LOW } == Pin.HIGH) aVal = aVal or (1 shl bit)
            if (inputs.getOrElse(4 + bit) { Pin.LOW } == Pin.HIGH) bVal = bVal or (1 shl bit)
        }
        val cin = if (inputs.getOrElse(8) { Pin.LOW } == Pin.HIGH) 1 else 0
        val sum = aVal + bVal + cin
        output = MutableList(5) { bit ->
            if ((sum and (1 shl bit)) != 0) Pin.HIGH else Pin.LOW
        }
        return output
    }
}

class Subtractor4Bit(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 9,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.SUBTRACTOR_4BIT,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {
    override suspend fun evaluate(): MutableList<Pin> {
        // Inputs: [0..3: A0..A3, 4..7: B0..B3, 8: BIN], Outputs: [0..3: D0..D3, 4: BOUT]
        if (inputs.take(9).any { it == Pin.ERROR }) {
            output = MutableList(5) { Pin.ERROR }
            return output
        }
        var aVal = 0
        var bVal = 0
        for (bit in 0 until 4) {
            if (inputs.getOrElse(bit) { Pin.LOW } == Pin.HIGH) aVal = aVal or (1 shl bit)
            if (inputs.getOrElse(4 + bit) { Pin.LOW } == Pin.HIGH) bVal = bVal or (1 shl bit)
        }
        val bin = if (inputs.getOrElse(8) { Pin.LOW } == Pin.HIGH) 1 else 0
        val rawDiff = aVal - bVal - bin
        val borrowOut = rawDiff < 0
        val diff4 = rawDiff and 0xF
        output = mutableListOf(
            if ((diff4 and 1) != 0) Pin.HIGH else Pin.LOW,
            if ((diff4 and 2) != 0) Pin.HIGH else Pin.LOW,
            if ((diff4 and 4) != 0) Pin.HIGH else Pin.LOW,
            if ((diff4 and 8) != 0) Pin.HIGH else Pin.LOW,
            if (borrowOut) Pin.HIGH else Pin.LOW
        )
        return output
    }
}

class Comparator4Bit(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 8,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.COMPARATOR_4BIT,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {
    override suspend fun evaluate(): MutableList<Pin> {
        // Inputs: [0..3: A0..A3, 4..7: B0..B3], Outputs: [0: A_GT_B, 1: A_EQ_B, 2: A_LT_B]
        if (inputs.take(8).any { it == Pin.ERROR }) {
            output = mutableListOf(Pin.ERROR, Pin.ERROR, Pin.ERROR)
            return output
        }
        var aVal = 0
        var bVal = 0
        for (bit in 0 until 4) {
            if (inputs.getOrElse(bit) { Pin.LOW } == Pin.HIGH) aVal = aVal or (1 shl bit)
            if (inputs.getOrElse(4 + bit) { Pin.LOW } == Pin.HIGH) bVal = bVal or (1 shl bit)
        }
        output = mutableListOf(
            if (aVal > bVal) Pin.HIGH else Pin.LOW,
            if (aVal == bVal) Pin.HIGH else Pin.LOW,
            if (aVal < bVal) Pin.HIGH else Pin.LOW
        )
        return output
    }
}

class Negator4Bit(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 4,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.NEGATOR_4BIT,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {
    override suspend fun evaluate(): MutableList<Pin> {
        // Inputs: [0..3: A0..A3], Outputs: [0..3: Y0..Y3] (-A mod 16)
        if (inputs.take(4).any { it == Pin.ERROR }) {
            output = MutableList(4) { Pin.ERROR }
            return output
        }
        var aVal = 0
        for (bit in 0 until 4) {
            if (inputs.getOrElse(bit) { Pin.LOW } == Pin.HIGH) aVal = aVal or (1 shl bit)
        }
        val neg = (-aVal) and 0xF
        output = MutableList(4) { bit ->
            if ((neg and (1 shl bit)) != 0) Pin.HIGH else Pin.LOW
        }
        return output
    }
}
