package tests

import engineLogic.computeSimulation
import flipFlops.DFlipFlop
import flipFlops.JKFlipFlop
import flipFlops.RSFlipFlop
import flipFlops.TFlipFlop
import flipFlops.DFlipFlopAsync
import flipFlops.JKFlipFlopAsync
import flipFlops.SRFlipFlopAsync
import flipFlops.TFlipFlopAsync
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import logicGates.SimulationComponent
import logicGates.ComponentType
import logicGates.Pin
import logicGates.inputWire
import logicGates.outputWire
import org.junit.Test
import wiring.Button
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
            mutableListOf<SimulationComponent>(outComp),
            mutableListOf<SimulationComponent>(tFf),
            mutableListOf<SimulationComponent>(btn)
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

        val stageOut = mutableListOf<SimulationComponent>(qOutput, qNotOutput)
        val stageFf = mutableListOf<SimulationComponent>(dFf)

        val result = computeSimulation(mutableListOf(stageOut, stageFf))

        assertEquals(2, result.size)
        assertEquals(Pin.HIGH, outWireQ.value)
        assertEquals(Pin.LOW, outWireQNot.value)
        assertEquals(listOf(Pin.HIGH), qOutput.evaluate())
        assertEquals(listOf(Pin.LOW), qNotOutput.evaluate())
    }

    @Test
    fun testJKFlipFlopAsyncTruthTableAndAsyncOverrides() = runBlocking {
        val jkAsync = JKFlipFlopAsync(
            id = 10,
            inputs = mutableListOf(Pin.LOW, Pin.LOW, Pin.LOW, Pin.LOW, Pin.LOW), // SET=0, J=0, CLK=0, K=0, RST=0
            output = mutableListOf(),
            inputCount = 5,
            inputFrom = mutableListOf(),
            outputTo = mutableListOf()
        )

        // Initial hold: Q=LOW, Q_not=HIGH
        var eval = jkAsync.evaluate()
        assertEquals(listOf(Pin.LOW, Pin.HIGH), eval)

        // Asynchronous SET overrides clock and J/K: SET=1, RST=0 -> Q=HIGH immediately
        jkAsync.inputs = mutableListOf(Pin.HIGH, Pin.LOW, Pin.LOW, Pin.HIGH, Pin.LOW)
        eval = jkAsync.evaluate()
        assertEquals(listOf(Pin.HIGH, Pin.LOW), eval)

        // De-assert SET: hold state
        jkAsync.inputs = mutableListOf(Pin.LOW, Pin.LOW, Pin.LOW, Pin.LOW, Pin.LOW)
        eval = jkAsync.evaluate()
        assertEquals(listOf(Pin.HIGH, Pin.LOW), eval)

        // Asynchronous RESET overrides clock and J/K: SET=0, RST=1 -> Q=LOW immediately
        jkAsync.inputs = mutableListOf(Pin.LOW, Pin.HIGH, Pin.HIGH, Pin.LOW, Pin.HIGH)
        eval = jkAsync.evaluate()
        assertEquals(listOf(Pin.LOW, Pin.HIGH), eval)

        // Invalid condition: SET=1 and RST=1 -> ERROR
        jkAsync.inputs = mutableListOf(Pin.HIGH, Pin.LOW, Pin.LOW, Pin.LOW, Pin.HIGH)
        eval = jkAsync.evaluate()
        assertEquals(listOf(Pin.ERROR, Pin.ERROR), eval)

        // Clear error with Reset
        jkAsync.inputs = mutableListOf(Pin.LOW, Pin.LOW, Pin.LOW, Pin.LOW, Pin.HIGH)
        eval = jkAsync.evaluate()
        assertEquals(listOf(Pin.LOW, Pin.HIGH), eval)

        // Synchronous operation: SET=0, RST=0
        // J=1, K=0 on rising edge -> Set (Q=HIGH)
        jkAsync.inputs = mutableListOf(Pin.LOW, Pin.HIGH, Pin.LOW, Pin.LOW, Pin.LOW)
        jkAsync.evaluate() // clk low
        jkAsync.inputs = mutableListOf(Pin.LOW, Pin.HIGH, Pin.HIGH, Pin.LOW, Pin.LOW)
        eval = jkAsync.evaluate() // clk rising edge
        assertEquals(listOf(Pin.HIGH, Pin.LOW), eval)

        // J=0, K=1 on rising edge -> Reset (Q=LOW)
        jkAsync.inputs = mutableListOf(Pin.LOW, Pin.LOW, Pin.LOW, Pin.HIGH, Pin.LOW)
        jkAsync.evaluate() // clk low
        jkAsync.inputs = mutableListOf(Pin.LOW, Pin.LOW, Pin.HIGH, Pin.HIGH, Pin.LOW)
        eval = jkAsync.evaluate() // clk rising edge
        assertEquals(listOf(Pin.LOW, Pin.HIGH), eval)

        // J=1, K=1 on rising edge -> Toggle to HIGH
        jkAsync.inputs = mutableListOf(Pin.LOW, Pin.HIGH, Pin.LOW, Pin.HIGH, Pin.LOW)
        jkAsync.evaluate() // clk low
        jkAsync.inputs = mutableListOf(Pin.LOW, Pin.HIGH, Pin.HIGH, Pin.HIGH, Pin.LOW)
        eval = jkAsync.evaluate() // clk rising edge
        assertEquals(listOf(Pin.HIGH, Pin.LOW), eval)

        // J=1, K=1 on next rising edge -> Toggle to LOW
        jkAsync.inputs = mutableListOf(Pin.LOW, Pin.HIGH, Pin.LOW, Pin.HIGH, Pin.LOW)
        jkAsync.evaluate() // clk low
        jkAsync.inputs = mutableListOf(Pin.LOW, Pin.HIGH, Pin.HIGH, Pin.HIGH, Pin.LOW)
        eval = jkAsync.evaluate() // clk rising edge
        assertEquals(listOf(Pin.LOW, Pin.HIGH), eval)
    }

    @Test
    fun testSRFlipFlopAsyncTruthTableAndAsyncOverrides() = runBlocking {
        val srAsync = SRFlipFlopAsync(
            id = 11,
            inputs = mutableListOf(Pin.LOW, Pin.LOW, Pin.LOW, Pin.LOW, Pin.LOW), // SET=0, S=0, CLK=0, R=0, RST=0
            output = mutableListOf(),
            inputCount = 5,
            inputFrom = mutableListOf(),
            outputTo = mutableListOf()
        )

        // Initial state
        var eval = srAsync.evaluate()
        assertEquals(listOf(Pin.LOW, Pin.HIGH), eval)

        // Asynchronous SET=HIGH -> Q=HIGH
        srAsync.inputs = mutableListOf(Pin.HIGH, Pin.LOW, Pin.LOW, Pin.LOW, Pin.LOW)
        eval = srAsync.evaluate()
        assertEquals(listOf(Pin.HIGH, Pin.LOW), eval)

        // Asynchronous RST=HIGH -> Q=LOW
        srAsync.inputs = mutableListOf(Pin.LOW, Pin.LOW, Pin.LOW, Pin.LOW, Pin.HIGH)
        eval = srAsync.evaluate()
        assertEquals(listOf(Pin.LOW, Pin.HIGH), eval)

        // Asynchronous SET=HIGH and RST=HIGH -> ERROR
        srAsync.inputs = mutableListOf(Pin.HIGH, Pin.LOW, Pin.LOW, Pin.LOW, Pin.HIGH)
        eval = srAsync.evaluate()
        assertEquals(listOf(Pin.ERROR, Pin.ERROR), eval)

        // Reset to normal LOW
        srAsync.inputs = mutableListOf(Pin.LOW, Pin.LOW, Pin.LOW, Pin.LOW, Pin.HIGH)
        srAsync.evaluate()

        // Synchronous Set: S=1, R=0 on rising edge -> Q=HIGH
        srAsync.inputs = mutableListOf(Pin.LOW, Pin.HIGH, Pin.LOW, Pin.LOW, Pin.LOW)
        srAsync.evaluate()
        srAsync.inputs = mutableListOf(Pin.LOW, Pin.HIGH, Pin.HIGH, Pin.LOW, Pin.LOW)
        eval = srAsync.evaluate()
        assertEquals(listOf(Pin.HIGH, Pin.LOW), eval)

        // Hold: S=0, R=0 on rising edge -> Q remains HIGH
        srAsync.inputs = mutableListOf(Pin.LOW, Pin.LOW, Pin.LOW, Pin.LOW, Pin.LOW)
        srAsync.evaluate()
        srAsync.inputs = mutableListOf(Pin.LOW, Pin.LOW, Pin.HIGH, Pin.LOW, Pin.LOW)
        eval = srAsync.evaluate()
        assertEquals(listOf(Pin.HIGH, Pin.LOW), eval)

        // Synchronous Reset: S=0, R=1 on rising edge -> Q=LOW
        srAsync.inputs = mutableListOf(Pin.LOW, Pin.LOW, Pin.LOW, Pin.HIGH, Pin.LOW)
        srAsync.evaluate()
        srAsync.inputs = mutableListOf(Pin.LOW, Pin.LOW, Pin.HIGH, Pin.HIGH, Pin.LOW)
        eval = srAsync.evaluate()
        assertEquals(listOf(Pin.LOW, Pin.HIGH), eval)

        // Synchronous Invalid: S=1, R=1 on rising edge -> ERROR
        srAsync.inputs = mutableListOf(Pin.LOW, Pin.HIGH, Pin.LOW, Pin.HIGH, Pin.LOW)
        srAsync.evaluate()
        srAsync.inputs = mutableListOf(Pin.LOW, Pin.HIGH, Pin.HIGH, Pin.HIGH, Pin.LOW)
        eval = srAsync.evaluate()
        assertEquals(listOf(Pin.ERROR, Pin.ERROR), eval)
    }

    @Test
    fun testDFlipFlopAsyncTruthTableAndAsyncOverrides() = runBlocking {
        val dAsync = DFlipFlopAsync(
            id = 12,
            inputs = mutableListOf(Pin.LOW, Pin.LOW, Pin.LOW, Pin.LOW), // SET=0, D=0, CLK=0, RST=0
            output = mutableListOf(),
            inputCount = 4,
            inputFrom = mutableListOf(),
            outputTo = mutableListOf()
        )

        // Initial state
        var eval = dAsync.evaluate()
        assertEquals(listOf(Pin.LOW, Pin.HIGH), eval)

        // Asynchronous SET -> Q=HIGH
        dAsync.inputs = mutableListOf(Pin.HIGH, Pin.LOW, Pin.LOW, Pin.LOW)
        eval = dAsync.evaluate()
        assertEquals(listOf(Pin.HIGH, Pin.LOW), eval)

        // Asynchronous RST -> Q=LOW
        dAsync.inputs = mutableListOf(Pin.LOW, Pin.HIGH, Pin.HIGH, Pin.HIGH)
        eval = dAsync.evaluate()
        assertEquals(listOf(Pin.LOW, Pin.HIGH), eval)

        // Both SET and RST -> ERROR
        dAsync.inputs = mutableListOf(Pin.HIGH, Pin.LOW, Pin.LOW, Pin.HIGH)
        eval = dAsync.evaluate()
        assertEquals(listOf(Pin.ERROR, Pin.ERROR), eval)

        // Normal D=HIGH on rising edge -> Q=HIGH
        dAsync.inputs = mutableListOf(Pin.LOW, Pin.HIGH, Pin.LOW, Pin.LOW)
        dAsync.evaluate()
        dAsync.inputs = mutableListOf(Pin.LOW, Pin.HIGH, Pin.HIGH, Pin.LOW)
        eval = dAsync.evaluate()
        assertEquals(listOf(Pin.HIGH, Pin.LOW), eval)

        // Normal D=LOW on rising edge -> Q=LOW
        dAsync.inputs = mutableListOf(Pin.LOW, Pin.LOW, Pin.LOW, Pin.LOW)
        dAsync.evaluate()
        dAsync.inputs = mutableListOf(Pin.LOW, Pin.LOW, Pin.HIGH, Pin.LOW)
        eval = dAsync.evaluate()
        assertEquals(listOf(Pin.LOW, Pin.HIGH), eval)
    }

    @Test
    fun testTFlipFlopAsyncTruthTableAndAsyncOverrides() = runBlocking {
        val tAsync = TFlipFlopAsync(
            id = 13,
            inputs = mutableListOf(Pin.LOW, Pin.LOW, Pin.LOW, Pin.LOW), // SET=0, T=0, CLK=0, RST=0
            output = mutableListOf(),
            inputCount = 4,
            inputFrom = mutableListOf(),
            outputTo = mutableListOf()
        )

        // Initial state
        var eval = tAsync.evaluate()
        assertEquals(listOf(Pin.LOW, Pin.HIGH), eval)

        // Asynchronous SET -> Q=HIGH
        tAsync.inputs = mutableListOf(Pin.HIGH, Pin.LOW, Pin.LOW, Pin.LOW)
        eval = tAsync.evaluate()
        assertEquals(listOf(Pin.HIGH, Pin.LOW), eval)

        // Asynchronous RST -> Q=LOW
        tAsync.inputs = mutableListOf(Pin.LOW, Pin.HIGH, Pin.HIGH, Pin.HIGH)
        eval = tAsync.evaluate()
        assertEquals(listOf(Pin.LOW, Pin.HIGH), eval)

        // Both SET and RST -> ERROR
        tAsync.inputs = mutableListOf(Pin.HIGH, Pin.LOW, Pin.LOW, Pin.HIGH)
        eval = tAsync.evaluate()
        assertEquals(listOf(Pin.ERROR, Pin.ERROR), eval)

        // Reset to clean state
        tAsync.inputs = mutableListOf(Pin.LOW, Pin.LOW, Pin.LOW, Pin.HIGH)
        tAsync.evaluate()

        // Normal T=HIGH on rising edge -> Toggle to HIGH
        tAsync.inputs = mutableListOf(Pin.LOW, Pin.HIGH, Pin.LOW, Pin.LOW)
        tAsync.evaluate()
        tAsync.inputs = mutableListOf(Pin.LOW, Pin.HIGH, Pin.HIGH, Pin.LOW)
        eval = tAsync.evaluate()
        assertEquals(listOf(Pin.HIGH, Pin.LOW), eval)

        // T=HIGH on next rising edge -> Toggle to LOW
        tAsync.inputs = mutableListOf(Pin.LOW, Pin.HIGH, Pin.LOW, Pin.LOW)
        tAsync.evaluate()
        tAsync.inputs = mutableListOf(Pin.LOW, Pin.HIGH, Pin.HIGH, Pin.LOW)
        eval = tAsync.evaluate()
        assertEquals(listOf(Pin.LOW, Pin.HIGH), eval)
    }
}
