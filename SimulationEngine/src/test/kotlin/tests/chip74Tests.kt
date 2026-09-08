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

        val chip = IC7400(
            id = 1,
            inputs = mutableListOf(
                Pin.HIGH, Pin.HIGH, // Gate 1: NAND(1,1)=0
                Pin.LOW, Pin.HIGH,  // Gate 2: NAND(0,1)=1
                Pin.LOW, Pin.UNDEFINED, Pin.UNDEFINED, Pin.UNDEFINED, Pin.UNDEFINED, Pin.HIGH
            ),
            output = mutableListOf(),
            inputFrom = mutableListOf(),
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
}
