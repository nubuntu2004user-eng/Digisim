package tests

import engineLogic.computeSimulation
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import logicGates.*
import org.junit.Test

class ComplexLogicTests {

    private fun createInput(
        id: Int,
        initialPin: Pin = Pin.LOW,
        outputWires: MutableList<outputWire> = mutableListOf()
    ): Input {
        return Input(
            id = id,
            inputs = mutableListOf(),
            output = mutableListOf(initialPin),
            inputCount = 0,
            inputFrom = mutableListOf(),
            outputTo = outputWires,
            componentType = ComponentType.INPUT
        )
    }

    private fun createOutput(
        id: Int,
        inputWires: MutableList<inputWire> = mutableListOf()
    ): Output {
        return Output(
            id = id,
            inputs = mutableListOf(),
            output = mutableListOf(Pin.UNDEFINED),
            inputCount = inputWires.size,
            inputFrom = inputWires,
            outputTo = mutableListOf(),
            componentType = ComponentType.OUTPUT
        )
    }

    private fun createAnd(
        id: Int,
        inputWires: MutableList<inputWire> = mutableListOf(),
        outputWires: MutableList<outputWire> = mutableListOf()
    ): And {
        return And(
            id = id,
            inputs = mutableListOf(),
            output = mutableListOf(Pin.UNDEFINED),
            inputCount = inputWires.size,
            inputFrom = inputWires,
            outputTo = outputWires,
            componentType = ComponentType.AND
        )
    }

    private fun createOr(
        id: Int,
        inputWires: MutableList<inputWire> = mutableListOf(),
        outputWires: MutableList<outputWire> = mutableListOf()
    ): Or {
        return Or(
            id = id,
            inputs = mutableListOf(),
            output = mutableListOf(Pin.UNDEFINED),
            inputCount = inputWires.size,
            inputFrom = inputWires,
            outputTo = outputWires,
            componentType = ComponentType.OR
        )
    }

    private fun createNand(
        id: Int,
        inputWires: MutableList<inputWire> = mutableListOf(),
        outputWires: MutableList<outputWire> = mutableListOf()
    ): Nand {
        return Nand(
            id = id,
            inputs = mutableListOf(),
            output = mutableListOf(Pin.UNDEFINED),
            inputCount = inputWires.size,
            inputFrom = inputWires,
            outputTo = outputWires,
            componentType = ComponentType.NAND
        )
    }

    private fun createNor(
        id: Int,
        inputWires: MutableList<inputWire> = mutableListOf(),
        outputWires: MutableList<outputWire> = mutableListOf()
    ): Nor {
        return Nor(
            id = id,
            inputs = mutableListOf(),
            output = mutableListOf(Pin.UNDEFINED),
            inputCount = inputWires.size,
            inputFrom = inputWires,
            outputTo = outputWires,
            componentType = ComponentType.NOR
        )
    }

    private fun createXor(
        id: Int,
        inputWires: MutableList<inputWire> = mutableListOf(),
        outputWires: MutableList<outputWire> = mutableListOf()
    ): Xor {
        return Xor(
            id = id,
            inputs = mutableListOf(),
            output = mutableListOf(Pin.UNDEFINED),
            inputCount = inputWires.size,
            inputFrom = inputWires,
            outputTo = outputWires,
            componentType = ComponentType.XOR
        )
    }

    private fun createXnor(
        id: Int,
        inputWires: MutableList<inputWire> = mutableListOf(),
        outputWires: MutableList<outputWire> = mutableListOf()
    ): XNor {
        return XNor(
            id = id,
            inputs = mutableListOf(),
            output = mutableListOf(Pin.UNDEFINED),
            inputCount = inputWires.size,
            inputFrom = inputWires,
            outputTo = outputWires,
            componentType = ComponentType.XNOR
        )
    }

    private fun createInvertor(
        id: Int,
        inputWires: MutableList<inputWire> = mutableListOf(),
        outputWires: MutableList<outputWire> = mutableListOf()
    ): Invertor {
        return Invertor(
            id = id,
            inputs = mutableListOf(),
            output = mutableListOf(Pin.UNDEFINED),
            inputCount = inputWires.size,
            inputFrom = inputWires,
            outputTo = outputWires,
            componentType = ComponentType.NOT
        )
    }

    @Test
    fun testSimulationReversesStages() = runBlocking {
        val inputComp = createInput(1, Pin.HIGH)
        val andComp = createAnd(2, mutableListOf(inputWire(1, 0, Pin.HIGH)))
        val outputComp = createOutput(3, mutableListOf(inputWire(2, 0, Pin.HIGH)))

        val stage0 = mutableListOf<BasicComponent>(outputComp)
        val stage1 = mutableListOf<BasicComponent>(andComp)
        val stage2 = mutableListOf<BasicComponent>(inputComp)

        val simulationInput = mutableListOf(stage0, stage1, stage2)
        val result = computeSimulation(simulationInput)

        assertEquals(3, result.size)
        assertEquals(inputComp.id, result[0][0].id)
        assertEquals(andComp.id, result[1][0].id)
        assertEquals(outputComp.id, result[2][0].id)
    }

    @Test
    fun testInputComponentEvaluation() = runBlocking {
        val outWire1 = outputWire(2, 0)
        val outWire2 = outputWire(3, 0)
        val inputHigh = createInput(1, Pin.HIGH, mutableListOf(outWire1, outWire2))

        val outWire3 = outputWire(4, 0)
        val inputLow = createInput(2, Pin.LOW, mutableListOf(outWire3))

        val simulationInput = mutableListOf(mutableListOf<BasicComponent>(inputHigh, inputLow))
        computeSimulation(simulationInput)

        assertEquals(Pin.HIGH, outWire1.value)
        assertEquals(Pin.HIGH, outWire2.value)
        assertEquals(Pin.LOW, outWire3.value)
    }

    @Test
    fun testInputComponentSwitch() = runBlocking {
        val outWire = outputWire(2, 0)
        val input = createInput(1, Pin.LOW, mutableListOf(outWire))

        input.switch()
        assertEquals(listOf(Pin.HIGH), input.output)

        computeSimulation(mutableListOf(mutableListOf<BasicComponent>(input)))
        assertEquals(Pin.HIGH, outWire.value)

        input.switch()
        assertEquals(listOf(Pin.LOW), input.output)

        computeSimulation(mutableListOf(mutableListOf<BasicComponent>(input)))
        assertEquals(Pin.LOW, outWire.value)
    }

    @Test
    fun testInverterGateSimulation() = runBlocking {
        // Test LOW -> HIGH
        val notOutWire1 = outputWire(3, 0)
        val notGate1 = createInvertor(2, mutableListOf(inputWire(1, 0, Pin.LOW)), mutableListOf(notOutWire1))
        val outputComp1 = createOutput(3, mutableListOf(inputWire(2, 0, Pin.HIGH)))

        val stageOutputs1 = mutableListOf<BasicComponent>(outputComp1)
        val stageGates1 = mutableListOf<BasicComponent>(notGate1)
        computeSimulation(mutableListOf(stageOutputs1, stageGates1))

        assertEquals(listOf(Pin.LOW), notGate1.inputs)
        assertEquals(Pin.HIGH, notOutWire1.value)
        assertEquals(listOf(Pin.HIGH), outputComp1.evaluate())

        // Test HIGH -> LOW
        val notOutWire2 = outputWire(5, 0)
        val notGate2 = createInvertor(4, mutableListOf(inputWire(1, 0, Pin.HIGH)), mutableListOf(notOutWire2))
        val outputComp2 = createOutput(5, mutableListOf(inputWire(4, 0, Pin.LOW)))

        val stageOutputs2 = mutableListOf<BasicComponent>(outputComp2)
        val stageGates2 = mutableListOf<BasicComponent>(notGate2)
        computeSimulation(mutableListOf(stageOutputs2, stageGates2))

        assertEquals(listOf(Pin.HIGH), notGate2.inputs)
        assertEquals(Pin.LOW, notOutWire2.value)
        assertEquals(listOf(Pin.LOW), outputComp2.evaluate())

        // Test ERROR input
        val notOutWire3 = outputWire(7, 0)
        val notGate3 = createInvertor(6, mutableListOf(inputWire(1, 0, Pin.ERROR)), mutableListOf(notOutWire3))
        computeSimulation(mutableListOf(mutableListOf<BasicComponent>(notGate3)))
        assertEquals(Pin.ERROR, notOutWire3.value)
    }

    @Test
    fun testAndGateSimulationConditions() = runBlocking {
        val testCases = listOf(
            Triple(Pin.HIGH, Pin.HIGH, Pin.HIGH),
            Triple(Pin.HIGH, Pin.LOW, Pin.LOW),
            Triple(Pin.LOW, Pin.HIGH, Pin.LOW),
            Triple(Pin.LOW, Pin.LOW, Pin.LOW),
            Triple(Pin.HIGH, Pin.ERROR, Pin.ERROR),
            Triple(Pin.HIGH, Pin.UNDEFINED, Pin.ERROR)
        )

        for ((pinA, pinB, expected) in testCases) {
            val outWire = outputWire(10, 0)
            val andGate = createAnd(
                id = 1,
                inputWires = mutableListOf(inputWire(2, 0, pinA), inputWire(3, 0, pinB)),
                outputWires = mutableListOf(outWire)
            )
            val outputComp = createOutput(10, mutableListOf(inputWire(1, 0, expected)))

            val stageOut = mutableListOf<BasicComponent>(outputComp)
            val stageGate = mutableListOf<BasicComponent>(andGate)

            computeSimulation(mutableListOf(stageOut, stageGate))

            assertEquals("AND($pinA, $pinB) should be $expected", expected, outWire.value)
            assertEquals(listOf(pinA, pinB), andGate.inputs)
            assertEquals(listOf(expected), outputComp.evaluate())
        }
    }

    @Test
    fun testOrGateSimulationConditions() = runBlocking {
        val testCases = listOf(
            Triple(Pin.HIGH, Pin.HIGH, Pin.HIGH),
            Triple(Pin.HIGH, Pin.LOW, Pin.HIGH),
            Triple(Pin.LOW, Pin.HIGH, Pin.HIGH),
            Triple(Pin.LOW, Pin.LOW, Pin.LOW),
            Triple(Pin.LOW, Pin.ERROR, Pin.ERROR),
            Triple(Pin.LOW, Pin.UNDEFINED, Pin.ERROR)
        )

        for ((pinA, pinB, expected) in testCases) {
            val outWire = outputWire(10, 0)
            val orGate = createOr(
                id = 1,
                inputWires = mutableListOf(inputWire(2, 0, pinA), inputWire(3, 0, pinB)),
                outputWires = mutableListOf(outWire)
            )

            computeSimulation(mutableListOf(mutableListOf<BasicComponent>(orGate)))

            assertEquals("OR($pinA, $pinB) should be $expected", expected, outWire.value)
            assertEquals(listOf(pinA, pinB), orGate.inputs)
        }
    }

    @Test
    fun testNandGateSimulationConditions() = runBlocking {
        val testCases = listOf(
            Triple(Pin.HIGH, Pin.HIGH, Pin.LOW),
            Triple(Pin.HIGH, Pin.LOW, Pin.HIGH),
            Triple(Pin.LOW, Pin.HIGH, Pin.HIGH),
            Triple(Pin.LOW, Pin.LOW, Pin.HIGH),
            Triple(Pin.HIGH, Pin.ERROR, Pin.ERROR),
            Triple(Pin.HIGH, Pin.UNDEFINED, Pin.ERROR)
        )

        for ((pinA, pinB, expected) in testCases) {
            val outWire = outputWire(10, 0)
            val nandGate = createNand(
                id = 1,
                inputWires = mutableListOf(inputWire(2, 0, pinA), inputWire(3, 0, pinB)),
                outputWires = mutableListOf(outWire)
            )

            computeSimulation(mutableListOf(mutableListOf<BasicComponent>(nandGate)))

            assertEquals("NAND($pinA, $pinB) should be $expected", expected, outWire.value)
            assertEquals(listOf(pinA, pinB), nandGate.inputs)
        }
    }

    @Test
    fun testNorGateSimulationConditions() = runBlocking {
        val testCases = listOf(
            Triple(Pin.LOW, Pin.LOW, Pin.HIGH),
            Triple(Pin.HIGH, Pin.LOW, Pin.LOW),
            Triple(Pin.LOW, Pin.HIGH, Pin.LOW),
            Triple(Pin.HIGH, Pin.HIGH, Pin.LOW),
            Triple(Pin.LOW, Pin.ERROR, Pin.ERROR),
            Triple(Pin.LOW, Pin.UNDEFINED, Pin.ERROR)
        )

        for ((pinA, pinB, expected) in testCases) {
            val outWire = outputWire(10, 0)
            val norGate = createNor(
                id = 1,
                inputWires = mutableListOf(inputWire(2, 0, pinA), inputWire(3, 0, pinB)),
                outputWires = mutableListOf(outWire)
            )

            computeSimulation(mutableListOf(mutableListOf<BasicComponent>(norGate)))

            assertEquals("NOR($pinA, $pinB) should be $expected", expected, outWire.value)
            assertEquals(listOf(pinA, pinB), norGate.inputs)
        }
    }

    @Test
    fun testXorGateSimulationConditions() = runBlocking {
        val testCases = listOf(
            Triple(Pin.LOW, Pin.LOW, Pin.LOW),
            Triple(Pin.HIGH, Pin.HIGH, Pin.LOW),
            Triple(Pin.HIGH, Pin.LOW, Pin.HIGH),
            Triple(Pin.LOW, Pin.HIGH, Pin.HIGH),
            Triple(Pin.HIGH, Pin.ERROR, Pin.ERROR),
            Triple(Pin.HIGH, Pin.UNDEFINED, Pin.ERROR)
        )

        for ((pinA, pinB, expected) in testCases) {
            val outWire = outputWire(10, 0)
            val xorGate = createXor(
                id = 1,
                inputWires = mutableListOf(inputWire(2, 0, pinA), inputWire(3, 0, pinB)),
                outputWires = mutableListOf(outWire)
            )

            computeSimulation(mutableListOf(mutableListOf<BasicComponent>(xorGate)))

            assertEquals("XOR($pinA, $pinB) should be $expected", expected, outWire.value)
            assertEquals(listOf(pinA, pinB), xorGate.inputs)
        }
    }

    @Test
    fun testXnorGateSimulationConditions() = runBlocking {
        val testCases = listOf(
            Triple(Pin.LOW, Pin.LOW, Pin.HIGH),
            Triple(Pin.HIGH, Pin.HIGH, Pin.HIGH),
            Triple(Pin.HIGH, Pin.LOW, Pin.LOW),
            Triple(Pin.LOW, Pin.HIGH, Pin.LOW),
            Triple(Pin.HIGH, Pin.ERROR, Pin.ERROR),
            Triple(Pin.HIGH, Pin.UNDEFINED, Pin.ERROR)
        )

        for ((pinA, pinB, expected) in testCases) {
            val outWire = outputWire(10, 0)
            val xnorGate = createXnor(
                id = 1,
                inputWires = mutableListOf(inputWire(2, 0, pinA), inputWire(3, 0, pinB)),
                outputWires = mutableListOf(outWire)
            )

            computeSimulation(mutableListOf(mutableListOf<BasicComponent>(xnorGate)))

            assertEquals("XNOR($pinA, $pinB) should be $expected", expected, outWire.value)
            assertEquals(listOf(pinA, pinB), xnorGate.inputs)
        }
    }

    @Test
    fun testHalfAdderCircuitSimulation() = runBlocking {
        // Half Adder: Sum = XOR(A, B), Carry = AND(A, B)
        val truthTable = listOf(
            // A, B, Expected Sum, Expected Carry
            listOf(Pin.LOW, Pin.LOW, Pin.LOW, Pin.LOW),
            listOf(Pin.LOW, Pin.HIGH, Pin.HIGH, Pin.LOW),
            listOf(Pin.HIGH, Pin.LOW, Pin.HIGH, Pin.LOW),
            listOf(Pin.HIGH, Pin.HIGH, Pin.LOW, Pin.HIGH)
        )

        for (row in truthTable) {
            val inA = row[0]
            val inB = row[1]
            val expectedSum = row[2]
            val expectedCarry = row[3]

            val sumOutWire = outputWire(5, 0)
            val carryOutWire = outputWire(6, 0)

            val xorGate = createXor(
                id = 3,
                inputWires = mutableListOf(inputWire(1, 0, inA), inputWire(2, 0, inB)),
                outputWires = mutableListOf(sumOutWire)
            )
            val andGate = createAnd(
                id = 4,
                inputWires = mutableListOf(inputWire(1, 0, inA), inputWire(2, 0, inB)),
                outputWires = mutableListOf(carryOutWire)
            )

            val sumOutput = createOutput(5, mutableListOf(inputWire(3, 0, expectedSum)))
            val carryOutput = createOutput(6, mutableListOf(inputWire(4, 0, expectedCarry)))

            val stageOutputs = mutableListOf<BasicComponent>(sumOutput, carryOutput)
            val stageGates = mutableListOf<BasicComponent>(xorGate, andGate)

            computeSimulation(mutableListOf(stageOutputs, stageGates))

            assertEquals("Half adder Sum for ($inA, $inB)", expectedSum, sumOutWire.value)
            assertEquals("Half adder Carry for ($inA, $inB)", expectedCarry, carryOutWire.value)
            assertEquals(listOf(expectedSum), sumOutput.evaluate())
            assertEquals(listOf(expectedCarry), carryOutput.evaluate())
        }
    }

    @Test
    fun testCompositeMultiStageCircuit() = runBlocking {
        val testScenarios = listOf(
            listOf(Pin.HIGH, Pin.HIGH, Pin.LOW, Pin.LOW, Pin.HIGH),
            listOf(Pin.LOW, Pin.HIGH, Pin.HIGH, Pin.HIGH, Pin.HIGH),
            listOf(Pin.LOW, Pin.HIGH, Pin.HIGH, Pin.LOW, Pin.LOW),
            listOf(Pin.LOW, Pin.LOW, Pin.LOW, Pin.LOW, Pin.LOW)
        )

        for (scenario in testScenarios) {
            val a = scenario[0]
            val b = scenario[1]
            val c = scenario[2]
            val d = scenario[3]
            val expectedOutput = scenario[4]

            val and1OutWire = outputWire(7, 0)
            val and2OutWire = outputWire(7, 1)
            val orOutWire = outputWire(8, 0)

            val andGate1 = createAnd(
                id = 5,
                inputWires = mutableListOf(inputWire(1, 0, a), inputWire(2, 0, b)),
                outputWires = mutableListOf(and1OutWire)
            )
            val andGate2 = createAnd(
                id = 6,
                inputWires = mutableListOf(inputWire(3, 0, c), inputWire(4, 0, d)),
                outputWires = mutableListOf(and2OutWire)
            )

            val and1Val = if (a == Pin.HIGH && b == Pin.HIGH) Pin.HIGH else Pin.LOW
            val and2Val = if (c == Pin.HIGH && d == Pin.HIGH) Pin.HIGH else Pin.LOW

            val orGate = createOr(
                id = 7,
                inputWires = mutableListOf(inputWire(5, 0, and1Val), inputWire(6, 0, and2Val)),
                outputWires = mutableListOf(orOutWire)
            )

            val finalOutput = createOutput(8, mutableListOf(inputWire(7, 0, expectedOutput)))

            // Stages: Output -> Stage 2 (OR) -> Stage 1 (ANDs)
            val stage3 = mutableListOf<BasicComponent>(finalOutput)
            val stage2 = mutableListOf<BasicComponent>(orGate)
            val stage1 = mutableListOf<BasicComponent>(andGate1, andGate2)

            val result = computeSimulation(mutableListOf(stage3, stage2, stage1))

            assertEquals(3, result.size)
            assertEquals("AND1 output", and1Val, and1OutWire.value)
            assertEquals("AND2 output", and2Val, and2OutWire.value)
            assertEquals("OR output", expectedOutput, orOutWire.value)
            assertEquals(listOf(expectedOutput), finalOutput.evaluate())
        }
    }

    @Test
    fun testInverterChainMultiStage() = runBlocking {
        // Chain: INPUT -> NOT1 -> NOT2 -> NOT3 -> OUTPUT
        // LOW -> HIGH -> LOW -> HIGH
        val not1Out = outputWire(2, 0)
        val not2Out = outputWire(3, 0)
        val not3Out = outputWire(4, 0)

        val not1 = createInvertor(1, mutableListOf(inputWire(0, 0, Pin.LOW)), mutableListOf(not1Out))
        val not2 = createInvertor(2, mutableListOf(inputWire(1, 0, Pin.HIGH)), mutableListOf(not2Out))
        val not3 = createInvertor(3, mutableListOf(inputWire(2, 0, Pin.LOW)), mutableListOf(not3Out))
        val outputComp = createOutput(4, mutableListOf(inputWire(3, 0, Pin.HIGH)))

        val stageOutput = mutableListOf<BasicComponent>(outputComp)
        val stageNot3 = mutableListOf<BasicComponent>(not3)
        val stageNot2 = mutableListOf<BasicComponent>(not2)
        val stageNot1 = mutableListOf<BasicComponent>(not1)

        val result = computeSimulation(mutableListOf(stageOutput, stageNot3, stageNot2, stageNot1))

        assertEquals(4, result.size)
        assertEquals(Pin.HIGH, not1Out.value)
        assertEquals(Pin.LOW, not2Out.value)
        assertEquals(Pin.HIGH, not3Out.value)
        assertEquals(listOf(Pin.HIGH), outputComp.evaluate())
    }

    @Test
    fun testInputsListClearedAndUpdated() = runBlocking {
        // Ensure stale inputs on gates are cleared and replaced with inputFrom wire values
        val andGate = createAnd(
            id = 1,
            inputWires = mutableListOf(inputWire(10, 0, Pin.HIGH), inputWire(11, 0, Pin.HIGH))
        )
        // Set stale dirty input values
        andGate.inputs = mutableListOf(Pin.LOW, Pin.ERROR, Pin.UNDEFINED)

        computeSimulation(mutableListOf(mutableListOf<BasicComponent>(andGate)))

        assertEquals(listOf(Pin.HIGH, Pin.HIGH), andGate.inputs)
        assertEquals(listOf(Pin.HIGH), andGate.evaluate())
    }

    @Test
    fun testFanOutMultipleOutputWires() = runBlocking {
        // One gate's output connects to 3 different target ports/gates
        val outWire1 = outputWire(2, 0)
        val outWire2 = outputWire(3, 0)
        val outWire3 = outputWire(4, 0)

        val orGate = createOr(
            id = 1,
            inputWires = mutableListOf(inputWire(10, 0, Pin.HIGH), inputWire(11, 0, Pin.LOW)),
            outputWires = mutableListOf(outWire1, outWire2, outWire3)
        )

        computeSimulation(mutableListOf(mutableListOf<BasicComponent>(orGate)))

        assertEquals(Pin.HIGH, outWire1.value)
        assertEquals(Pin.HIGH, outWire2.value)
        assertEquals(Pin.HIGH, outWire3.value)
    }

    @Test
    fun testEmptySimulationInput() = runBlocking {
        val emptyInput = mutableListOf<MutableList<BasicComponent>>()
        val result = computeSimulation(emptyInput)
        assertTrue(result.isEmpty())
    }

    @Test
    fun testEmptyStagesInSimulation() = runBlocking {
        val emptyStage = mutableListOf<BasicComponent>()
        val result = computeSimulation(mutableListOf(emptyStage, emptyStage))
        assertEquals(2, result.size)
        assertTrue(result[0].isEmpty())
        assertTrue(result[1].isEmpty())
    }
}




