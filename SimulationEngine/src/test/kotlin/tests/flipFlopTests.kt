package tests

import engineLogic.computeSimulation
import flipFlops.DFlipFlop
import flipFlops.JKFlipFlop
import flipFlops.RSFlipFlop
import flipFlops.TFlipFlop
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import logicGates.BasicComponent
import logicGates.ComponentType
import logicGates.Pin
import logicGates.inputWire
import logicGates.outputWire
import org.junit.Test
import wiring.Button
import wiring.Input
import wiring.Output

class FlipFlopTests {

    @Test
    fun testRSFlipFlopTruthTable() = runBlocking {
        val rs = RSFlipFlop(
            id = 1,
            inputs = mutableListOf(Pin.LOW, Pin.LOW),
            output = mutableListOf(),
            inputCount = 2,
            inputFrom = mutableListOf(),
            outputTo = mutableListOf(),
            componentType = ComponentType.RS_FLIP_FLOP
        )

        // Initial state: Q = LOW, Q_not = HIGH
        var eval = rs.evaluate()
        assertEquals(listOf(Pin.LOW, Pin.HIGH), eval)

        // Set: S = HIGH, R = LOW -> Q = HIGH, Q_not = LOW
        rs.inputs = mutableListOf(Pin.HIGH, Pin.LOW)
        eval = rs.evaluate()
        assertEquals(listOf(Pin.HIGH, Pin.LOW), eval)

        // Hold: S = LOW, R = LOW -> Q remains HIGH, Q_not = LOW
        rs.inputs = mutableListOf(Pin.LOW, Pin.LOW)
        eval = rs.evaluate()
        assertEquals(listOf(Pin.HIGH, Pin.LOW), eval)

        // Reset: S = LOW, R = HIGH -> Q = LOW, Q_not = HIGH
        rs.inputs = mutableListOf(Pin.LOW, Pin.HIGH)
        eval = rs.evaluate()
        assertEquals(listOf(Pin.LOW, Pin.HIGH), eval)

        // Hold: S = LOW, R = LOW -> Q remains LOW, Q_not = HIGH
        rs.inputs = mutableListOf(Pin.LOW, Pin.LOW)
        eval = rs.evaluate()
        assertEquals(listOf(Pin.LOW, Pin.HIGH), eval)

        // Invalid: S = HIGH, R = HIGH -> ERROR
        rs.inputs = mutableListOf(Pin.HIGH, Pin.HIGH)
        eval = rs.evaluate()
        assertEquals(listOf(Pin.ERROR, Pin.ERROR), eval)
    }

    @Test
    fun testJKFlipFlopTruthTable() = runBlocking {
        val jk = JKFlipFlop(
            id = 2,
            inputs = mutableListOf(Pin.LOW, Pin.HIGH, Pin.LOW), // J=LOW, CLK=HIGH, K=LOW
            output = mutableListOf(),
            inputCount = 3,
            inputFrom = mutableListOf(),
            outputTo = mutableListOf(),
            componentType = ComponentType.JK_FLIP_FLOP
        )

        // Initial Hold: J=0, K=0 -> Q=LOW, Q_not=HIGH
        var eval = jk.evaluate()
        assertEquals(listOf(Pin.LOW, Pin.HIGH), eval)

        // Set: J=HIGH, CLK=HIGH (rising from clock cycle), K=LOW -> Q=HIGH, Q_not=LOW
        jk.inputs = mutableListOf(Pin.HIGH, Pin.LOW, Pin.LOW)
        jk.evaluate() // clock goes low
        jk.inputs = mutableListOf(Pin.HIGH, Pin.HIGH, Pin.LOW)
        eval = jk.evaluate() // clock goes high (rising edge)
        assertEquals(listOf(Pin.HIGH, Pin.LOW), eval)

        // Reset: J=LOW, K=HIGH with rising edge
        jk.inputs = mutableListOf(Pin.LOW, Pin.LOW, Pin.HIGH)
        jk.evaluate() // clock low
        jk.inputs = mutableListOf(Pin.LOW, Pin.HIGH, Pin.HIGH)
        eval = jk.evaluate() // clock high (rising edge)
        assertEquals(listOf(Pin.LOW, Pin.HIGH), eval)

        // Toggle: J=HIGH, K=HIGH
        jk.inputs = mutableListOf(Pin.HIGH, Pin.LOW, Pin.HIGH)
        jk.evaluate() // clock low
        jk.inputs = mutableListOf(Pin.HIGH, Pin.HIGH, Pin.HIGH)
        eval = jk.evaluate() // rising edge -> toggles to HIGH
        assertEquals(listOf(Pin.HIGH, Pin.LOW), eval)

        // Toggle again
        jk.inputs = mutableListOf(Pin.HIGH, Pin.LOW, Pin.HIGH)
        jk.evaluate() // clock low
        jk.inputs = mutableListOf(Pin.HIGH, Pin.HIGH, Pin.HIGH)
        eval = jk.evaluate() // rising edge -> toggles to LOW
        assertEquals(listOf(Pin.LOW, Pin.HIGH), eval)

        // Clock stays HIGH (no edge) -> Should NOT toggle continuously
        eval = jk.evaluate()
        assertEquals(listOf(Pin.LOW, Pin.HIGH), eval)
    }

    @Test
    fun testDFlipFlopTruthTable() = runBlocking {
        val dFf = DFlipFlop(
            id = 3,
            inputs = mutableListOf(Pin.HIGH, Pin.HIGH), // D=HIGH, CLK=HIGH
            output = mutableListOf(),
            inputCount = 2,
            inputFrom = mutableListOf(),
            outputTo = mutableListOf(),
            componentType = ComponentType.D_FLIP_FLOP
        )

        // D=HIGH on rising edge -> Q=HIGH, Q_not=LOW
        var eval = dFf.evaluate()
        assertEquals(listOf(Pin.HIGH, Pin.LOW), eval)

        // D=LOW but CLK=LOW -> Hold Q=HIGH
        dFf.inputs = mutableListOf(Pin.LOW, Pin.LOW)
        eval = dFf.evaluate()
        assertEquals(listOf(Pin.HIGH, Pin.LOW), eval)

        // D=LOW and CLK rising edge -> Q=LOW, Q_not=HIGH
        dFf.inputs = mutableListOf(Pin.LOW, Pin.HIGH)
        eval = dFf.evaluate()
        assertEquals(listOf(Pin.LOW, Pin.HIGH), eval)
    }

    @Test
    fun testTFlipFlopTruthTable() = runBlocking {
        val tFf = TFlipFlop(
            id = 4,
            inputs = mutableListOf(Pin.LOW, Pin.HIGH), // T=LOW, CLK=HIGH
            output = mutableListOf(),
            inputCount = 2,
            inputFrom = mutableListOf(),
            outputTo = mutableListOf(),
            componentType = ComponentType.T_FLIP_FLOP
        )

        // T=LOW on rising edge -> Hold Q=LOW
        var eval = tFf.evaluate()
        assertEquals(listOf(Pin.LOW, Pin.HIGH), eval)

        // T=HIGH on rising edge -> Toggle Q=HIGH, Q_not=LOW
        tFf.inputs = mutableListOf(Pin.HIGH, Pin.LOW)
        tFf.evaluate() // clock low
        tFf.inputs = mutableListOf(Pin.HIGH, Pin.HIGH)
        eval = tFf.evaluate() // clock high (rising edge)
        assertEquals(listOf(Pin.HIGH, Pin.LOW), eval)

        // Clock stays HIGH -> No re-toggle
        eval = tFf.evaluate()
        assertEquals(listOf(Pin.HIGH, Pin.LOW), eval)

        // Next rising edge -> Toggles to LOW
        tFf.inputs = mutableListOf(Pin.HIGH, Pin.LOW)
        tFf.evaluate() // clock low
        tFf.inputs = mutableListOf(Pin.HIGH, Pin.HIGH)
        eval = tFf.evaluate() // clock high (rising edge)
        assertEquals(listOf(Pin.LOW, Pin.HIGH), eval)
    }

    @Test
    fun testButtonOutputsHighForOnlyOneTick() = runBlocking {
        val btn = Button(
            id = 1,
            inputs = mutableListOf(),
            output = mutableListOf(Pin.LOW),
            inputCount = 0,
            inputFrom = mutableListOf(),
            outputTo = mutableListOf()
        )

        // Initially LOW
        assertEquals(listOf(Pin.LOW), btn.evaluate())

        // Press button -> evaluation on tick 1 produces HIGH
        btn.press()
        val tick1 = btn.evaluate()
        assertEquals(listOf(Pin.HIGH), tick1)

        // Next tick automatically resets to LOW without any external intervention
        val tick2 = btn.evaluate()
        assertEquals(listOf(Pin.LOW), tick2)
    }

    @Test
    fun testButtonTriggersTFlipFlopSinglePulse() = runBlocking {
        val btnOutWire = outputWire(targetGateId = 2, portId = 1) // connected to CLK port of T flip-flop
        val btn = Button(
            id = 1,
            inputs = mutableListOf(),
            output = mutableListOf(Pin.LOW),
            inputCount = 0,
            inputFrom = mutableListOf(),
            outputTo = mutableListOf(btnOutWire)
        )

        val tInputWire = inputWire(sourceGateId = 100, portId = 0, value = Pin.HIGH) // T=HIGH
        val clkWire = inputWire(sourceGateId = 1, portId = 1, value = Pin.LOW)
        val qOutWire = outputWire(targetGateId = 3, portId = 0)

        val tFf = TFlipFlop(
            id = 2,
            inputs = mutableListOf(Pin.HIGH, Pin.LOW),
            output = mutableListOf(),
            inputCount = 2,
            inputFrom = mutableListOf(tInputWire, clkWire),
            outputTo = mutableListOf(qOutWire)
        )

        val outComp = Output(
            id = 3,
            inputs = mutableListOf(),
            output = mutableListOf(Pin.UNDEFINED),
            inputCount = 1,
            inputFrom = mutableListOf(inputWire(2, 0)),
            outputTo = mutableListOf()
        )

        val stages = mutableListOf(
            mutableListOf<BasicComponent>(outComp),
            mutableListOf<BasicComponent>(tFf),
            mutableListOf<BasicComponent>(btn)
        )

        // Initial tick: Button is LOW -> Q is LOW
        computeSimulation(stages)
        assertEquals(listOf(Pin.LOW), outComp.evaluate())

        // Press Button: Button gives HIGH for 1 tick -> Rising edge -> T toggles Q to HIGH
        btn.press()
        computeSimulation(stages)
        assertEquals(listOf(Pin.HIGH), outComp.evaluate())

        // Next tick: Button automatically returns to LOW -> Q stays HIGH (held)
        computeSimulation(stages)
        assertEquals(listOf(Pin.HIGH), outComp.evaluate())

        // Press Button again -> Toggles Q to LOW
        btn.press()
        computeSimulation(stages)
        assertEquals(listOf(Pin.LOW), outComp.evaluate())
    }

    @Test
    fun testFlipFlopMultiOutputRoutingInCircuit() = runBlocking {
        val outWireQ = outputWire(targetGateId = 10, portId = 0, sourcePortId = 0)
        val outWireQNot = outputWire(targetGateId = 11, portId = 0, sourcePortId = 1)

        val dFf = DFlipFlop(
            id = 1,
            inputs = mutableListOf(Pin.HIGH, Pin.HIGH),
            output = mutableListOf(),
            inputCount = 2,
            inputFrom = mutableListOf(inputWire(100, 0, Pin.HIGH), inputWire(101, 1, Pin.HIGH)),
            outputTo = mutableListOf(outWireQ, outWireQNot)
        )

        val qOutput = Output(
            id = 10,
            inputs = mutableListOf(),
            output = mutableListOf(Pin.UNDEFINED),
            inputCount = 1,
            inputFrom = mutableListOf(inputWire(1, 0)),
            outputTo = mutableListOf()
        )

        val qNotOutput = Output(
            id = 11,
            inputs = mutableListOf(),
            output = mutableListOf(Pin.UNDEFINED),
            inputCount = 1,
            inputFrom = mutableListOf(inputWire(1, 0)),
            outputTo = mutableListOf()
        )

        val stageOut = mutableListOf<BasicComponent>(qOutput, qNotOutput)
        val stageFf = mutableListOf<BasicComponent>(dFf)

        val result = computeSimulation(mutableListOf(stageOut, stageFf))

        assertEquals(2, result.size)
        assertEquals(Pin.HIGH, outWireQ.value)
        assertEquals(Pin.LOW, outWireQNot.value)
        assertEquals(listOf(Pin.HIGH), qOutput.evaluate())
        assertEquals(listOf(Pin.LOW), qNotOutput.evaluate())
    }
}
