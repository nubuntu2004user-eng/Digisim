package tests

import engineLogic.SimulationState
import engineLogic.computeSimulation
import engineLogic.parseSimulation
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import logicGates.And
import logicGates.BasicComponent
import logicGates.BasicComponentData
import logicGates.ComponentType
import logicGates.Or
import logicGates.Pin
import org.junit.Test


class ComplexLogicTests {

    @Test
    fun testCircuitParsing() = runTest {
        val and1 = BasicComponentData("Test23", ComponentType.AND, listOf(Pin.HIGH, Pin.LOW), listOf(Pin.UNDEFINED))
        val and2 = BasicComponentData("Test24", ComponentType.AND, listOf(Pin.HIGH, Pin.HIGH), listOf(Pin.UNDEFINED))
        val tick1 = listOf(and1, and2)

        val or1 = BasicComponentData("Test25", ComponentType.OR, listOf(Pin.UNDEFINED, Pin.UNDEFINED), listOf(Pin.UNDEFINED))
        val tick2 = listOf(or1)

        val expected: List<List<BasicComponent>> = listOf(
            listOf(
                And("Test23", listOf(Pin.HIGH, Pin.LOW), listOf(Pin.UNDEFINED) , 2),
                And("Test24", listOf(Pin.HIGH, Pin.HIGH), listOf(Pin.UNDEFINED) , 2)
            ),
            listOf(
                Or("Test25", listOf(Pin.UNDEFINED, Pin.UNDEFINED), listOf(Pin.UNDEFINED) , 2)
            )
        )

        val state = SimulationState()

        // act
        parseSimulation(listOf(tick1, tick2), state)

        // assert
        assertEquals(2, state.CircuitData.size)
        assertEquals(expected, state.CircuitData)
    }


    @Test
    fun testComputing() = runTest {
        val testInput = listOf(
            listOf(
                And("Test23", listOf(Pin.HIGH, Pin.LOW), listOf(Pin.UNDEFINED) , 2),
                And("Test24", listOf(Pin.HIGH, Pin.HIGH), listOf(Pin.UNDEFINED) , 2)
            ),
            listOf(
                Or("Test25", listOf(Pin.UNDEFINED, Pin.UNDEFINED), listOf(Pin.UNDEFINED) , 2)
            )
        )
        computeSimulation(testInput)
    }


}

