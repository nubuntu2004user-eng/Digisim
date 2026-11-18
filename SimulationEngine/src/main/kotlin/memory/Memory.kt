package memory

import logicGates.ComponentType
import logicGates.Pin
import logicGates.SimulationComponent
import logicGates.inputWire
import logicGates.outputWire

class Register4Bit(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 7,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.REGISTER_4BIT,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {
    override suspend fun evaluate(): MutableList<Pin> {
        // Inputs: [0..3: D0..D3, 4: CLK, 5: EN (default HIGH), 6: CLR (default LOW)]
        // Outputs: [0..3: Q0..Q3]
        if (statefulMemory.isEmpty()) statefulMemory.add(0)
        var currentVal = statefulMemory[0] and 0xF

        val clk = inputs.getOrElse(4) { Pin.LOW }
        val enRaw = inputs.getOrElse(5) { Pin.HIGH }
        val en = if (enRaw == Pin.UNDEFINED) Pin.HIGH else enRaw
        val clrRaw = inputs.getOrElse(6) { Pin.LOW }
        val clr = if (clrRaw == Pin.UNDEFINED) Pin.LOW else clrRaw

        val risingEdge = clk == Pin.HIGH && statefulClockPin != Pin.HIGH
        statefulClockPin = clk

        if (clr == Pin.HIGH) {
            currentVal = 0
        } else if (risingEdge && en == Pin.HIGH) {
            var nextVal = 0
            for (bit in 0 until 4) {
                if (inputs.getOrElse(bit) { Pin.LOW } == Pin.HIGH) {
                    nextVal = nextVal or (1 shl bit)
                }
            }
            currentVal = nextVal
        }

        statefulMemory[0] = currentVal
        output = MutableList(4) { bit -> if ((currentVal and (1 shl bit)) != 0) Pin.HIGH else Pin.LOW }
        return output
    }
}

class Counter4Bit(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 9,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.COUNTER_4BIT,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {
    override suspend fun evaluate(): MutableList<Pin> {
        // Inputs: [0: CLK, 1: EN (default HIGH), 2: UP_DOWN (HIGH/UNDEFINED=Up, LOW=Down), 3: CLR, 4: LOAD, 5..8: D0..D3]
        // Outputs: [0..3: Q0..Q3, 4: RCO]
        if (statefulMemory.isEmpty()) statefulMemory.add(0)
        var count = statefulMemory[0] and 0xF

        val clk = inputs.getOrElse(0) { Pin.LOW }
        val enRaw = inputs.getOrElse(1) { Pin.HIGH }
        val en = if (enRaw == Pin.UNDEFINED) Pin.HIGH else enRaw
        val upDownRaw = inputs.getOrElse(2) { Pin.HIGH }
        val countUp = upDownRaw != Pin.LOW
        val clr = inputs.getOrElse(3) { Pin.LOW } == Pin.HIGH
        val load = inputs.getOrElse(4) { Pin.LOW } == Pin.HIGH

        val risingEdge = clk == Pin.HIGH && statefulClockPin != Pin.HIGH
        statefulClockPin = clk

        if (clr) {
            count = 0
        } else if (risingEdge) {
            if (load) {
                var loaded = 0
                for (bit in 0 until 4) {
                    if (inputs.getOrElse(5 + bit) { Pin.LOW } == Pin.HIGH) {
                        loaded = loaded or (1 shl bit)
                    }
                }
                count = loaded
            } else if (en == Pin.HIGH) {
                count = if (countUp) (count + 1) and 0xF else (count - 1) and 0xF
            }
        }

        statefulMemory[0] = count
        val rco = (en == Pin.HIGH) && ((countUp && count == 15) || (!countUp && count == 0))
        output = mutableListOf(
            if ((count and 1) != 0) Pin.HIGH else Pin.LOW,
            if ((count and 2) != 0) Pin.HIGH else Pin.LOW,
            if ((count and 4) != 0) Pin.HIGH else Pin.LOW,
            if ((count and 8) != 0) Pin.HIGH else Pin.LOW,
            if (rco) Pin.HIGH else Pin.LOW
        )
        return output
    }
}

class ShiftRegister4Bit(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 8,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.SHIFT_REGISTER_4BIT,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {
    override suspend fun evaluate(): MutableList<Pin> {
        // Inputs: [0: SER_IN, 1: CLK, 2: LOAD (HIGH=Parallel Load, LOW/UNDEFINED=Shift), 3: CLR, 4..7: D0..D3]
        // Outputs: [0..3: Q0..Q3]
        if (statefulMemory.isEmpty()) statefulMemory.add(0)
        var reg = statefulMemory[0] and 0xF

        val serIn = if (inputs.getOrElse(0) { Pin.LOW } == Pin.HIGH) 1 else 0
        val clk = inputs.getOrElse(1) { Pin.LOW }
        val load = inputs.getOrElse(2) { Pin.LOW } == Pin.HIGH
        val clr = inputs.getOrElse(3) { Pin.LOW } == Pin.HIGH

        val risingEdge = clk == Pin.HIGH && statefulClockPin != Pin.HIGH
        statefulClockPin = clk

        if (clr) {
            reg = 0
        } else if (risingEdge) {
            if (load) {
                var loaded = 0
                for (bit in 0 until 4) {
                    if (inputs.getOrElse(4 + bit) { Pin.LOW } == Pin.HIGH) {
                        loaded = loaded or (1 shl bit)
                    }
                }
                reg = loaded
            } else {
                reg = ((reg shl 1) or serIn) and 0xF
            }
        }

        statefulMemory[0] = reg
        output = MutableList(4) { bit -> if ((reg and (1 shl bit)) != 0) Pin.HIGH else Pin.LOW }
        return output
    }
}

class Ram16x4(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 10,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.RAM_16X4,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {
    override suspend fun evaluate(): MutableList<Pin> {
        // Inputs: [0..3: ADDR0..ADDR3, 4..7: DIN0..DIN3, 8: WE, 9: CLK]
        // Outputs: [0..3: DOUT0..DOUT3]
        while (statefulMemory.size < 16) statefulMemory.add(0)

        var addr = 0
        var din = 0
        for (bit in 0 until 4) {
            if (inputs.getOrElse(bit) { Pin.LOW } == Pin.HIGH) addr = addr or (1 shl bit)
            if (inputs.getOrElse(4 + bit) { Pin.LOW } == Pin.HIGH) din = din or (1 shl bit)
        }
        val we = inputs.getOrElse(8) { Pin.LOW } == Pin.HIGH
        val clk = inputs.getOrElse(9) { Pin.LOW }

        val risingEdge = clk == Pin.HIGH && statefulClockPin != Pin.HIGH
        statefulClockPin = clk

        if (we && (risingEdge || clk == Pin.HIGH)) {
            statefulMemory[addr] = din and 0xF
        }

        val word = statefulMemory[addr] and 0xF
        output = MutableList(4) { bit -> if ((word and (1 shl bit)) != 0) Pin.HIGH else Pin.LOW }
        return output
    }
}

class Rom16x4(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 4,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.ROM_16X4,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {
    override suspend fun evaluate(): MutableList<Pin> {
        // Inputs: [0..3: ADDR0..ADDR3], Outputs: [0..3: DOUT0..DOUT3]
        while (statefulMemory.size < 16) statefulMemory.add(0)

        var addr = 0
        for (bit in 0 until 4) {
            if (inputs.getOrElse(bit) { Pin.LOW } == Pin.HIGH) addr = addr or (1 shl bit)
        }

        val word = statefulMemory[addr] and 0xF
        output = MutableList(4) { bit -> if ((word and (1 shl bit)) != 0) Pin.HIGH else Pin.LOW }
        return output
    }
}
