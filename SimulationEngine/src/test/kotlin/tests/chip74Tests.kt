package tests

import chips74.*
import engineLogic.computeSimulation
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import logicGates.SimulationComponent
import logicGates.Pin
import logicGates.inputWire
import logicGates.outputWire
import org.junit.Test
import wiring.Output

class Chip74Tests {

    @Test
    fun testIC7400QuadNandTruthTableAndPower() = runBlocking {
        val chip = IC7400(
            id = 1,
            inputs = mutableListOf(
                Pin.HIGH, Pin.HIGH, // Gate 1: 1A=1, 1B=1 -> 1Y=0
                Pin.HIGH, Pin.LOW,  // Gate 2: 2A=1, 2B=0 -> 2Y=1
                Pin.LOW,            // Pin 7: GND=0
                Pin.LOW, Pin.LOW,   // Gate 3: 3A=0, 3B=0 -> 3Y=1
                Pin.HIGH, Pin.HIGH, // Gate 4: 4A=1, 4B=1 -> 4Y=0
                Pin.HIGH            // Pin 14: VCC=1
            ),
            output = mutableListOf(),
            inputFrom = mutableListOf(),
            outputTo = mutableListOf()
        )

        var eval = chip.evaluate()
        assertEquals(listOf(Pin.LOW, Pin.HIGH, Pin.HIGH, Pin.LOW), eval)

        // Invalid power: VCC is LOW
        chip.inputs[9] = Pin.LOW
        eval = chip.evaluate()
        assertEquals(listOf(Pin.ERROR, Pin.ERROR, Pin.ERROR, Pin.ERROR), eval)

        // Invalid power: GND is HIGH
        chip.inputs[9] = Pin.HIGH
        chip.inputs[4] = Pin.HIGH
        eval = chip.evaluate()
        assertEquals(listOf(Pin.ERROR, Pin.ERROR, Pin.ERROR, Pin.ERROR), eval)
    }

    @Test
    fun testIC7408QuadAndTruthTable() = runBlocking {
        val chip = IC7408(
            id = 2,
            inputs = mutableListOf(
                Pin.HIGH, Pin.HIGH, // Gate 1: 1A=1, 1B=1 -> 1Y=1
                Pin.HIGH, Pin.LOW,  // Gate 2: 2A=1, 2B=0 -> 2Y=0
                Pin.LOW,            // GND
                Pin.LOW, Pin.HIGH,  // Gate 3: 3A=0, 3B=1 -> 3Y=0
                Pin.LOW, Pin.LOW,   // Gate 4: 4A=0, 4B=0 -> 4Y=0
                Pin.HIGH            // VCC
            ),
            output = mutableListOf(),
            inputFrom = mutableListOf(),
            outputTo = mutableListOf()
        )

        val eval = chip.evaluate()
        assertEquals(listOf(Pin.HIGH, Pin.LOW, Pin.LOW, Pin.LOW), eval)
    }

    @Test
    fun testIC7432QuadOrTruthTable() = runBlocking {
        val chip = IC7432(
            id = 3,
            inputs = mutableListOf(
                Pin.HIGH, Pin.LOW,  // Gate 1: 1A=1, 1B=0 -> 1Y=1
                Pin.LOW, Pin.LOW,   // Gate 2: 2A=0, 2B=0 -> 2Y=0
                Pin.LOW,            // GND
                Pin.HIGH, Pin.HIGH, // Gate 3: 3A=1, 3B=1 -> 3Y=1
                Pin.LOW, Pin.HIGH,  // Gate 4: 4A=0, 4B=1 -> 4Y=1
                Pin.HIGH            // VCC
            ),
            output = mutableListOf(),
            inputFrom = mutableListOf(),
            outputTo = mutableListOf()
        )

        val eval = chip.evaluate()
        assertEquals(listOf(Pin.HIGH, Pin.LOW, Pin.HIGH, Pin.HIGH), eval)
    }

    @Test
    fun testIC7486QuadXorTruthTable() = runBlocking {
        val chip = IC7486(
            id = 4,
            inputs = mutableListOf(
                Pin.HIGH, Pin.HIGH, // Gate 1: 1A=1, 1B=1 -> 1Y=0
                Pin.HIGH, Pin.LOW,  // Gate 2: 2A=1, 2B=0 -> 2Y=1
                Pin.LOW,            // GND
                Pin.LOW, Pin.HIGH,  // Gate 3: 3A=0, 3B=1 -> 3Y=1
                Pin.LOW, Pin.LOW,   // Gate 4: 4A=0, 4B=0 -> 4Y=0
                Pin.HIGH            // VCC
            ),
            output = mutableListOf(),
            inputFrom = mutableListOf(),
            outputTo = mutableListOf()
        )

        val eval = chip.evaluate()
        assertEquals(listOf(Pin.LOW, Pin.HIGH, Pin.HIGH, Pin.LOW), eval)
    }

    @Test
    fun testIC7402QuadNorTruthTable() = runBlocking {
        val chip = IC7402(
            id = 5,
            inputs = mutableListOf(
                Pin.LOW, Pin.LOW,   // Gate 1: 1A=0, 1B=0 -> 1Y=1
                Pin.HIGH, Pin.LOW,  // Gate 2: 2A=1, 2B=0 -> 2Y=0
                Pin.LOW,            // GND
                Pin.LOW, Pin.HIGH,  // Gate 3: 3A=0, 3B=1 -> 3Y=0
                Pin.HIGH, Pin.HIGH, // Gate 4: 4A=1, 4B=1 -> 4Y=0
                Pin.HIGH            // VCC
            ),
            output = mutableListOf(),
            inputFrom = mutableListOf(),
            outputTo = mutableListOf()
        )

        val eval = chip.evaluate()
        assertEquals(listOf(Pin.HIGH, Pin.LOW, Pin.LOW, Pin.LOW), eval)
    }

    @Test
    fun testIC7404HexInverterTruthTable() = runBlocking {
        val chip = IC7404(
            id = 6,
            inputs = mutableListOf(
                Pin.HIGH, // 1A=1 -> 1Y=0
                Pin.LOW,  // 2A=0 -> 2Y=1
                Pin.HIGH, // 3A=1 -> 3Y=0
                Pin.LOW,  // GND
                Pin.LOW,  // 4A=0 -> 4Y=1
                Pin.HIGH, // 5A=1 -> 5Y=0
                Pin.LOW,  // 6A=0 -> 6Y=1
                Pin.HIGH  // VCC
            ),
            output = mutableListOf(),
            inputFrom = mutableListOf(),
            outputTo = mutableListOf()
        )

        val eval = chip.evaluate()
        assertEquals(listOf(Pin.LOW, Pin.HIGH, Pin.LOW, Pin.HIGH, Pin.LOW, Pin.HIGH), eval)
    }

    @Test
    fun testChip74MultiOutputRoutingInCircuit() = runBlocking {
        val outWire1Y = outputWire(targetGateId = 20, portId = 0, sourcePortId = 0)
        val outWire2Y = outputWire(targetGateId = 21, portId = 0, sourcePortId = 1)

        // Drive chip inputs via inputFrom wires (sourceGateId=0 = "external"/constant source),
        // in exact pin order: 1A,1B,2A,2B,GND,3A,3B,4A,4B,VCC
        val chipInputs = mutableListOf(
            inputWire(sourceGateId = 0, portId = 0, value = Pin.HIGH), // 1A
            inputWire(sourceGateId = 0, portId = 1, value = Pin.HIGH), // 1B
            inputWire(sourceGateId = 0, portId = 2, value = Pin.LOW),  // 2A
            inputWire(sourceGateId = 0, portId = 3, value = Pin.HIGH), // 2B
            inputWire(sourceGateId = 0, portId = 4, value = Pin.LOW),  // GND
            inputWire(sourceGateId = 0, portId = 5, value = Pin.UNDEFINED), // 3A
            inputWire(sourceGateId = 0, portId = 6, value = Pin.UNDEFINED), // 3B
            inputWire(sourceGateId = 0, portId = 7, value = Pin.UNDEFINED), // 4A
            inputWire(sourceGateId = 0, portId = 8, value = Pin.UNDEFINED), // 4B
            inputWire(sourceGateId = 0, portId = 9, value = Pin.HIGH), // VCC
        )

        val chip = IC7400(
            id = 1,
            inputs = mutableListOf(), // will be rebuilt from inputFrom by computeSimulation
            output = mutableListOf(),
            inputFrom = chipInputs,
            outputTo = mutableListOf(outWire1Y, outWire2Y)
        )

        val out1 = Output(20, mutableListOf(), mutableListOf(Pin.UNDEFINED), 1, mutableListOf(inputWire(1, 0)), mutableListOf())
        val out2 = Output(21, mutableListOf(), mutableListOf(Pin.UNDEFINED), 1, mutableListOf(inputWire(1, 0)), mutableListOf())

        val stageOut = mutableListOf<SimulationComponent>(out1, out2)
        val stageChip = mutableListOf<SimulationComponent>(chip)

        val result = computeSimulation(mutableListOf(stageOut, stageChip))
        assertEquals(2, result.size)
        assertEquals(Pin.LOW, outWire1Y.value)
        assertEquals(Pin.HIGH, outWire2Y.value)
        assertEquals(listOf(Pin.LOW), out1.evaluate())
        assertEquals(listOf(Pin.HIGH), out2.evaluate())
    }

    @Test
    fun testIC7407HexBufferAndIC74266QuadXnor() = runBlocking {
        val ic7407 = IC7407(
            id = 7,
            inputs = mutableListOf(
                Pin.HIGH, Pin.LOW, Pin.HIGH,
                Pin.LOW, // GND
                Pin.LOW, Pin.HIGH, Pin.LOW,
                Pin.HIGH // VCC
            ),
            output = mutableListOf(),
            inputFrom = mutableListOf(),
            outputTo = mutableListOf()
        )
        assertEquals(listOf(Pin.HIGH, Pin.LOW, Pin.HIGH, Pin.LOW, Pin.HIGH, Pin.LOW), ic7407.evaluate())

        val ic74266 = IC74266(
            id = 8,
            inputs = mutableListOf(
                Pin.HIGH, Pin.HIGH, // 1A=1, 1B=1 -> 1Y=1
                Pin.HIGH, Pin.LOW,  // 2A=1, 2B=0 -> 2Y=0
                Pin.LOW,            // GND
                Pin.LOW, Pin.HIGH,  // 3A=0, 3B=1 -> 3Y=0
                Pin.LOW, Pin.LOW,   // 4A=0, 4B=0 -> 4Y=1
                Pin.HIGH            // VCC
            ),
            output = mutableListOf(),
            inputFrom = mutableListOf(),
            outputTo = mutableListOf()
        )
        assertEquals(listOf(Pin.HIGH, Pin.LOW, Pin.LOW, Pin.HIGH), ic74266.evaluate())
    }

    @Test
    fun testIC7410AndIC7411AndIC7427Triple3InputGates() = runBlocking {
        // Inputs: [0: 1A, 1: 1B, 2: 2A, 3: 2B, 4: 2C, 5: GND, 6: 3A, 7: 3B, 8: 3C, 9: 1C, 10: VCC]
        // Gate 1 = (1, 1, 1), Gate 2 = (1, 0, 1), Gate 3 = (0, 0, 0)
        val inputs = mutableListOf(
            Pin.HIGH, Pin.HIGH,             // 1A, 1B
            Pin.HIGH, Pin.LOW, Pin.HIGH,    // 2A, 2B, 2C
            Pin.LOW,                        // GND
            Pin.LOW, Pin.LOW, Pin.LOW,      // 3A, 3B, 3C
            Pin.HIGH,                       // 1C
            Pin.HIGH                        // VCC
        )

        val ic7410 = IC7410(9, inputs.toMutableList(), mutableListOf(), inputFrom = mutableListOf(), outputTo = mutableListOf())
        assertEquals(listOf(Pin.LOW, Pin.HIGH, Pin.HIGH), ic7410.evaluate())

        val ic7411 = IC7411(10, inputs.toMutableList(), mutableListOf(), inputFrom = mutableListOf(), outputTo = mutableListOf())
        assertEquals(listOf(Pin.HIGH, Pin.LOW, Pin.LOW), ic7411.evaluate())

        val ic7427 = IC7427(11, inputs.toMutableList(), mutableListOf(), inputFrom = mutableListOf(), outputTo = mutableListOf())
        assertEquals(listOf(Pin.LOW, Pin.LOW, Pin.HIGH), ic7427.evaluate())
    }

    @Test
    fun testIC7420AndIC7421Dual4InputAndIC7430Single8InputNand() = runBlocking {
        // Inputs for 7420/7421: [0..3: 1A..1D, 4: GND, 5..8: 2A..2D, 9: VCC]
        val inputs4 = mutableListOf(
            Pin.HIGH, Pin.HIGH, Pin.HIGH, Pin.HIGH, // Gate 1 all HIGH
            Pin.LOW,                                // GND
            Pin.HIGH, Pin.HIGH, Pin.LOW, Pin.HIGH,  // Gate 2 one LOW
            Pin.HIGH                                // VCC
        )

        val ic7420 = IC7420(12, inputs4.toMutableList(), mutableListOf(), inputFrom = mutableListOf(), outputTo = mutableListOf())
        assertEquals(listOf(Pin.LOW, Pin.HIGH), ic7420.evaluate())

        val ic7421 = IC7421(13, inputs4.toMutableList(), mutableListOf(), inputFrom = mutableListOf(), outputTo = mutableListOf())
        assertEquals(listOf(Pin.HIGH, Pin.LOW), ic7421.evaluate())

        // Inputs for 7430: [0..5: A..F, 6: GND, 7: G, 8: H, 9: VCC]
        val inputs8AllHigh = mutableListOf(
            Pin.HIGH, Pin.HIGH, Pin.HIGH, Pin.HIGH, Pin.HIGH, Pin.HIGH,
            Pin.LOW, // GND
            Pin.HIGH, Pin.HIGH,
            Pin.HIGH // VCC
        )
        val ic7430 = IC7430(14, inputs8AllHigh, mutableListOf(), inputFrom = mutableListOf(), outputTo = mutableListOf())
        assertEquals(listOf(Pin.LOW), ic7430.evaluate())

        ic7430.inputs[7] = Pin.LOW
        assertEquals(listOf(Pin.HIGH), ic7430.evaluate())
    }

    @Test
    fun testIC7473AndIC7474AndIC7476DualFlipFlops() = runBlocking {
        // IC7474: [0: 1CLR, 1: 1D, 2: 1CLK, 3: 1PRE, 4: GND, 5: 2CLR, 6: 2D, 7: 2CLK, 8: 2PRE, 9: VCC]
        val ic7474 = IC7474(
            id = 15,
            inputs = mutableListOf(
                Pin.LOW, Pin.HIGH, Pin.LOW, Pin.LOW, // FF1: CLR=0, D=1, CLK=0, PRE=0
                Pin.LOW,                             // GND
                Pin.LOW, Pin.LOW, Pin.LOW, Pin.HIGH, // FF2: CLR=0, D=0, CLK=0, PRE=1 (Preset active)
                Pin.HIGH                             // VCC
            ),
            output = mutableListOf(),
            inputFrom = mutableListOf(),
            outputTo = mutableListOf()
        )
        // Initial evaluation: FF1 holds LOW, FF2 preset to HIGH
        assertEquals(listOf(Pin.LOW, Pin.HIGH, Pin.HIGH, Pin.LOW), ic7474.evaluate())

        // Rising edge on FF1 CLK (index 2 -> HIGH), release FF2 PRE (index 8 -> LOW) and pulse FF2 CLR (index 5 -> HIGH)
        ic7474.inputs[2] = Pin.HIGH
        ic7474.inputs[8] = Pin.LOW
        ic7474.inputs[5] = Pin.HIGH
        assertEquals(listOf(Pin.HIGH, Pin.LOW, Pin.LOW, Pin.HIGH), ic7474.evaluate())

        // IC7473: [0: 1CLR, 1: 1J, 2: 1CLK, 3: 1K, 4: GND, 5: 2CLR, 6: 2J, 7: 2CLK, 8: 2K, 9: VCC]
        val ic7473 = IC7473(
            id = 16,
            inputs = mutableListOf(
                Pin.LOW, Pin.HIGH, Pin.HIGH, Pin.LOW, // FF1: rising edge with J=1, K=0 -> Q1=HIGH
                Pin.LOW,                              // GND
                Pin.HIGH, Pin.HIGH, Pin.HIGH, Pin.LOW,// FF2: CLR=1 overrides -> Q2=LOW
                Pin.HIGH                              // VCC
            ),
            output = mutableListOf(),
            inputFrom = mutableListOf(),
            outputTo = mutableListOf()
        )
        assertEquals(listOf(Pin.HIGH, Pin.LOW, Pin.LOW, Pin.HIGH), ic7473.evaluate())

        // IC7476: [0: 1PRE, 1: 1CLR, 2: 1J, 3: 1CLK, 4: 1K, 5: GND, 6: 2PRE, 7: 2CLR, 8: 2J, 9: 2CLK, 10: 2K, 11: VCC]
        val ic7476 = IC7476(
            id = 17,
            inputs = mutableListOf(
                Pin.HIGH, Pin.LOW, Pin.LOW, Pin.LOW, Pin.LOW, // FF1: PRE=1 -> Q1=HIGH
                Pin.LOW,                                      // GND
                Pin.LOW, Pin.LOW, Pin.HIGH, Pin.HIGH, Pin.HIGH, // FF2: rising edge with J=1, K=1 -> toggles LOW to HIGH
                Pin.HIGH                                      // VCC
            ),
            output = mutableListOf(),
            inputFrom = mutableListOf(),
            outputTo = mutableListOf()
        )
        assertEquals(listOf(Pin.HIGH, Pin.LOW, Pin.HIGH, Pin.LOW), ic7476.evaluate())
    }

    @Test
    fun testIC74138AndIC74139Decoders() = runBlocking {
        // IC74138: [0: A, 1: B, 2: C, 3: ~G2A, 4: ~G2B, 5: G1, 6: GND, 7: VCC]
        // Select line 5 (C=1, B=0, A=1)
        val ic74138 = IC74138(
            id = 18,
            inputs = mutableListOf(
                Pin.HIGH, Pin.LOW, Pin.HIGH, // A=1, B=0, C=1 -> index 5
                Pin.LOW, Pin.LOW, Pin.HIGH,  // ~G2A=0, ~G2B=0, G1=1 (Enabled)
                Pin.LOW, Pin.HIGH            // GND, VCC
            ),
            output = mutableListOf(),
            inputFrom = mutableListOf(),
            outputTo = mutableListOf()
        )
        val expected138 = MutableList(8) { idx -> if (idx == 5) Pin.LOW else Pin.HIGH }
        assertEquals(expected138, ic74138.evaluate())

        // Disable G1 -> all outputs HIGH
        ic74138.inputs[5] = Pin.LOW
        assertEquals(List(8) { Pin.HIGH }, ic74138.evaluate())

        // IC74139: [0: ~1G, 1: 1A, 2: 1B, 3: GND, 4: ~2G, 5: 2A, 6: 2B, 7: VCC]
        val ic74139 = IC74139(
            id = 19,
            inputs = mutableListOf(
                Pin.LOW, Pin.LOW, Pin.HIGH,  // Decoder 1 enabled, 1A=0, 1B=1 -> index 2 LOW
                Pin.LOW,                     // GND
                Pin.HIGH, Pin.HIGH, Pin.HIGH,// Decoder 2 disabled (~2G=1) -> all HIGH
                Pin.HIGH                     // VCC
            ),
            output = mutableListOf(),
            inputFrom = mutableListOf(),
            outputTo = mutableListOf()
        )
        assertEquals(
            listOf(Pin.HIGH, Pin.HIGH, Pin.LOW, Pin.HIGH, Pin.HIGH, Pin.HIGH, Pin.HIGH, Pin.HIGH),
            ic74139.evaluate()
        )
    }

    @Test
    fun testIC74151AndIC74157MultiplexersAndIC74283Adder() = runBlocking {
        // IC74151: [0..7: D0..D7, 8: A, 9: B, 10: C, 11: ~G, 12: GND, 13: VCC]
        // Set D6 = HIGH, all others LOW, select 6 (C=1, B=1, A=0)
        val ic74151 = IC74151(
            id = 20,
            inputs = mutableListOf(
                Pin.LOW, Pin.LOW, Pin.LOW, Pin.LOW, Pin.LOW, Pin.LOW, Pin.HIGH, Pin.LOW, // D0..D7 (D6=HIGH)
                Pin.LOW, Pin.HIGH, Pin.HIGH, // A=0, B=1, C=1 -> 6
                Pin.LOW,                     // ~G=0 (enabled)
                Pin.LOW, Pin.HIGH            // GND, VCC
            ),
            output = mutableListOf(),
            inputFrom = mutableListOf(),
            outputTo = mutableListOf()
        )
        assertEquals(listOf(Pin.HIGH, Pin.LOW), ic74151.evaluate()) // Y=HIGH, W=LOW

        // IC74157: [0: SEL, 1: 1A, 2: 1B, 3: 2A, 4: 2B, 5: GND, 6: 3A, 7: 3B, 8: 4A, 9: 4B, 10: ~G, 11: VCC]
        val ic74157 = IC74157(
            id = 21,
            inputs = mutableListOf(
                Pin.LOW,             // SEL=0 (selects A inputs: 1, 0, 1, 0)
                Pin.HIGH, Pin.LOW,   // 1A=1, 1B=0
                Pin.LOW, Pin.HIGH,   // 2A=0, 2B=1
                Pin.LOW,             // GND
                Pin.HIGH, Pin.LOW,   // 3A=1, 3B=0
                Pin.LOW, Pin.HIGH,   // 4A=0, 4B=1
                Pin.LOW,             // ~G=0
                Pin.HIGH             // VCC
            ),
            output = mutableListOf(),
            inputFrom = mutableListOf(),
            outputTo = mutableListOf()
        )
        assertEquals(listOf(Pin.HIGH, Pin.LOW, Pin.HIGH, Pin.LOW), ic74157.evaluate())
        ic74157.inputs[0] = Pin.HIGH // SEL=1 (selects B inputs: 0, 1, 0, 1)
        assertEquals(listOf(Pin.LOW, Pin.HIGH, Pin.LOW, Pin.HIGH), ic74157.evaluate())

        // IC74283: 4-bit Full Adder
        // Inputs: [0: A1, 1: B1, 2: A2, 3: B2, 4: A3, 5: B3, 6: A4, 7: B4, 8: C0, 9: GND, 10: VCC]
        // Compute A = 9 (1001_2: A1=1, A2=0, A3=0, A4=1) + B = 7 (0111_2: B1=1, B2=1, B3=1, B4=0) + C0 = 1 -> 17 (1_0001_2: S1=1, S2=0, S3=0, S4=0, C4=1)
        val ic74283 = IC74283(
            id = 22,
            inputs = mutableListOf(
                Pin.HIGH, Pin.HIGH, // A1=1, B1=1
                Pin.LOW, Pin.HIGH,  // A2=0, B2=1
                Pin.LOW, Pin.HIGH,  // A3=0, B3=1
                Pin.HIGH, Pin.LOW,  // A4=1, B4=0
                Pin.HIGH,           // C0=1
                Pin.LOW, Pin.HIGH   // GND, VCC
            ),
            output = mutableListOf(),
            inputFrom = mutableListOf(),
            outputTo = mutableListOf()
        )
        assertEquals(listOf(Pin.HIGH, Pin.LOW, Pin.LOW, Pin.LOW, Pin.HIGH), ic74283.evaluate())
    }
}
