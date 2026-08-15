package tests

import logicGates.And
import logicGates.Invertor
import logicGates.Nand
import logicGates.Or
import logicGates.Pin
import logicGates.XNor
import logicGates.Xor
import kotlinx.coroutines.test.runTest
import org.junit.Test
import engineLogic.simulationTick
import junit.framework.TestCase.assertEquals


class BasicComponentTests{
    @Test
    fun testNandHigh() {
    val nand = Nand("Test1", listOf(Pin.LOW, Pin.LOW), listOf(Pin.UNDEFINED))
    val nand2 = Nand("Test2", listOf(Pin.LOW, Pin.HIGH), listOf(Pin.UNDEFINED))
    val nand3 = Nand("Test3", listOf(Pin.HIGH, Pin.LOW), listOf(Pin.UNDEFINED))
    assertEquals(listOf(Pin.HIGH), nand.evaluate())
    assertEquals(listOf(Pin.HIGH), nand2.evaluate())
    assertEquals(listOf(Pin.HIGH), nand3.evaluate())
}
    @Test
    fun testNandLow() {
        val nand = Nand("Test4", listOf(Pin.HIGH, Pin.HIGH), listOf(Pin.UNDEFINED))
        assertEquals(listOf(Pin.LOW), nand.evaluate())
    }
    @Test
    fun testNandError() {
        val nand = Nand("Test5", listOf(Pin.ERROR, Pin.ERROR), listOf(Pin.UNDEFINED))
        assertEquals(listOf(Pin.ERROR), nand.evaluate())
    }

    @Test
    fun testAndHigh(){
        val and = And("Test6", listOf(Pin.HIGH, Pin.HIGH), listOf(Pin.UNDEFINED))
        assertEquals(listOf(Pin.HIGH), and.evaluate())
    }

    @Test
    fun testAndLow(){
        val and = And("Test7", listOf(Pin.LOW, Pin.LOW), listOf(Pin.UNDEFINED))
        val and2 = And("Test8", listOf(Pin.LOW, Pin.HIGH), listOf(Pin.UNDEFINED))
        assertEquals(listOf(Pin.LOW), and.evaluate())
        assertEquals(listOf(Pin.LOW), and2.evaluate())
    }

    @Test
    fun testAndError(){
        val and = And("Test9", listOf(Pin.ERROR, Pin.ERROR), listOf(Pin.UNDEFINED))
        assertEquals(listOf(Pin.ERROR), and.evaluate())
    }

    @Test
    fun testOrHigh(){
        val or = Or("Test10", listOf(Pin.HIGH, Pin.HIGH), listOf(Pin.UNDEFINED))
        val or2 = Or("Test11", listOf(Pin.LOW, Pin.HIGH), listOf(Pin.UNDEFINED))
        assertEquals(listOf(Pin.HIGH), or.evaluate())
        assertEquals(listOf(Pin.HIGH), or2.evaluate())
    }
    @Test
    fun testOrLow(){
        val or = Or("Test12", listOf(Pin.LOW, Pin.LOW), listOf(Pin.UNDEFINED))
        assertEquals(listOf(Pin.LOW), or.evaluate())
    }
    @Test
    fun testOrError(){
        val or = Or("Test13", listOf(Pin.ERROR, Pin.ERROR), listOf(Pin.UNDEFINED))
        assertEquals(listOf(Pin.ERROR), or.evaluate())
    }

    @Test
    fun testXorHigh(){
        val xor = Xor("Test14", listOf(Pin.HIGH, Pin.LOW), listOf(Pin.UNDEFINED))
        assertEquals(listOf(Pin.HIGH), xor.evaluate())
    }

    @Test
    fun testXorLow(){
        val xor = Xor("Test15", listOf(Pin.LOW, Pin.LOW), listOf(Pin.UNDEFINED))
        assertEquals(listOf(Pin.LOW), xor.evaluate())
    }

    @Test
    fun testXorError(){
        val xor = Xor("Test16", listOf(Pin.ERROR, Pin.ERROR), listOf(Pin.UNDEFINED))
        assertEquals(listOf(Pin.ERROR), xor.evaluate())
    }

    @Test
    fun testXNorLow(){
        val xnor = XNor("Test17", listOf(Pin.HIGH, Pin.LOW), listOf(Pin.UNDEFINED))
        assertEquals(listOf(Pin.LOW), xnor.evaluate())
    }

    @Test
    fun testXNorHigh(){
        val xnor = XNor("Test18", listOf(Pin.LOW, Pin.LOW), listOf(Pin.UNDEFINED))
        assertEquals(listOf(Pin.HIGH), xnor.evaluate())
    }

    @Test
    fun testXNorError(){
        val xnor = XNor("Test19", listOf(Pin.ERROR, Pin.ERROR), listOf(Pin.UNDEFINED))
        assertEquals(listOf(Pin.ERROR), xnor.evaluate())
    }

    @Test
    fun testInversion(){
        val testLow = Invertor("Test20" , listOf(Pin.HIGH) , listOf(Pin.UNDEFINED))
        val testHigh = Invertor("Test21" , listOf(Pin.LOW) , listOf(Pin.UNDEFINED))
        val testError = Invertor("Test22" , listOf(Pin.ERROR , Pin.UNDEFINED) , listOf(Pin.UNDEFINED))
        assertEquals(mutableListOf(Pin.LOW) , testLow.evaluate())
        assertEquals(mutableListOf(Pin.HIGH) , testHigh.evaluate())
        assertEquals( mutableListOf(Pin.ERROR , Pin.ERROR) , testError.evaluate())
    }


}


