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
import logicGates.Nand
import logicGates.Or
import logicGates.Pin
import org.junit.Test
import kotlin.collections.listOf


class ComplexLogicTests {
    val state = SimulationState()


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


        parseSimulation(listOf(tick1, tick2), state)

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
        computeSimulation(testInput , state , mutableListOf(Pin.HIGH , Pin.LOW , Pin.HIGH , Pin.HIGH))
        assertEquals(listOf(Pin.HIGH) , state.outputs)
    }

    @Test
    fun testComplexCircuitHigh() = runTest {
        val testInput = listOf(
            listOf(
                And("Test26", listOf(Pin.HIGH, Pin.LOW), listOf(Pin.UNDEFINED) , 2),
                And("Test27", listOf(Pin.HIGH, Pin.HIGH), listOf(Pin.UNDEFINED) , 2)
            ),
            listOf(
                Or("Test28", listOf(Pin.UNDEFINED, Pin.UNDEFINED), listOf(Pin.UNDEFINED) , 2)
            )
        )
         val firstSimTree = computeSimulation(testInput , state , mutableListOf(Pin.HIGH , Pin.LOW , Pin.HIGH , Pin.HIGH))
        val testInput2 = listOf(
            listOf(
                Or("Test29" , listOf(Pin.UNDEFINED , Pin.UNDEFINED) , listOf(Pin.UNDEFINED) , 2 )
            )
        )
        val secondSimTree = computeSimulation(testInput2 , state , mutableListOf(Pin.HIGH , Pin.LOW))

        val testInput3 = listOf(
            listOf(
            Nand("Test30" , listOf(Pin.UNDEFINED , Pin.UNDEFINED) , listOf(Pin.UNDEFINED) , 2)
            )
        )
        val finalInput: MutableList<Pin> = (firstSimTree + secondSimTree) as MutableList<Pin>
        val finalSimTree = computeSimulation(testInput3 , state , finalInput )
        assertEquals(listOf(Pin.LOW) , finalSimTree)
    }



    @Test
    fun testComplexCircuitLow() = runTest {
        val testInput = listOf(
            listOf(
                And("Test31", listOf(Pin.HIGH, Pin.LOW), listOf(Pin.UNDEFINED) , 2),
                And("Test32", listOf(Pin.HIGH, Pin.LOW), listOf(Pin.UNDEFINED) , 2)
            ),
            listOf(
                Or("Test33", listOf(Pin.UNDEFINED, Pin.UNDEFINED), listOf(Pin.UNDEFINED) , 2)
            )
        )
        val firstSimTree = computeSimulation(testInput , state , mutableListOf(Pin.HIGH , Pin.LOW , Pin.HIGH , Pin.HIGH))
        val testInput2 = listOf(
            listOf(
                Or("Test34" , listOf(Pin.UNDEFINED , Pin.UNDEFINED) , listOf(Pin.UNDEFINED) , 2 )
            )
        )
        val secondSimTree = computeSimulation(testInput2 , state , mutableListOf(Pin.LOW , Pin.LOW))

        val testInput3 = listOf(
            listOf(
                Nand("Test35" , listOf(Pin.UNDEFINED , Pin.UNDEFINED) , listOf(Pin.UNDEFINED) , 2)
            )
        )
        val finalInput: MutableList<Pin> = (firstSimTree + secondSimTree) as MutableList<Pin>
        val finalSimTree = computeSimulation(testInput3 , state , finalInput )
        assertEquals(listOf(Pin.HIGH) , finalSimTree)
    }
    }




