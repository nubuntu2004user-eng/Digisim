package tests

import logicGates.And
import logicGates.Invertor
import logicGates.Nand
import logicGates.Or
import logicGates.Pin
import logicGates.XNor
import logicGates.Xor
import org.junit.Test
import junit.framework.TestCase.assertEquals
import logicGates.ComponentType


class BasicComponentTests{
    @Test
    fun testNandHigh() {
    val nand = Nand(1, mutableListOf(Pin.LOW, Pin.LOW), mutableListOf(Pin.UNDEFINED) , 2 , mutableListOf() , mutableListOf() ,
        ComponentType.NAND)
    val nand2 = Nand(1, mutableListOf(Pin.LOW, Pin.HIGH), mutableListOf(Pin.UNDEFINED) , 2, mutableListOf() , mutableListOf() ,
        ComponentType.NAND)
    val nand3 = Nand(3, mutableListOf(Pin.HIGH, Pin.LOW), mutableListOf(Pin.UNDEFINED) , 2, mutableListOf() , mutableListOf() ,
        ComponentType.NAND)
    assertEquals(listOf(Pin.HIGH), nand.evaluate())
    assertEquals(listOf(Pin.HIGH), nand2.evaluate())
    assertEquals(listOf(Pin.HIGH), nand3.evaluate())
}
    @Test
    fun testNandLow() {
        val nand = Nand(4, mutableListOf(Pin.HIGH, Pin.HIGH), mutableListOf(Pin.UNDEFINED) , 2, mutableListOf() , mutableListOf() ,
            ComponentType.NAND)
        assertEquals(listOf(Pin.LOW), nand.evaluate())
    }
    @Test
    fun testNandError() {
        val nand = Nand(5, mutableListOf(Pin.ERROR, Pin.ERROR), mutableListOf(Pin.UNDEFINED) , 2, mutableListOf() , mutableListOf() ,
            ComponentType.NAND)
        assertEquals(listOf(Pin.ERROR), nand.evaluate())
    }

    @Test
    fun testAndHigh(){
        val and = And(6, mutableListOf(Pin.HIGH, Pin.HIGH), mutableListOf(Pin.UNDEFINED) , 2, mutableListOf() , mutableListOf() ,
            ComponentType.AND)
        assertEquals(listOf(Pin.HIGH), and.evaluate())
    }

    @Test
    fun testAndLow(){
        val and = And(7, mutableListOf(Pin.LOW, Pin.LOW), mutableListOf(Pin.UNDEFINED) , 2, mutableListOf() , mutableListOf() ,
            ComponentType.AND)
        val and2 = And(8, mutableListOf(Pin.LOW, Pin.HIGH), mutableListOf(Pin.UNDEFINED) , 2, mutableListOf() , mutableListOf() ,
            ComponentType.AND)
        assertEquals(listOf(Pin.LOW), and.evaluate())
        assertEquals(listOf(Pin.LOW), and2.evaluate())
    }

    @Test
    fun testAndError(){
        val and = And(9, mutableListOf(Pin.ERROR, Pin.ERROR), mutableListOf(Pin.UNDEFINED) , 2, mutableListOf() , mutableListOf() ,
            ComponentType.AND)
        assertEquals(listOf(Pin.ERROR), and.evaluate())
    }

    @Test
    fun testOrHigh(){
        val or = Or(10, mutableListOf(Pin.HIGH, Pin.HIGH), mutableListOf(Pin.UNDEFINED) , 2, mutableListOf() , mutableListOf() ,
            ComponentType.OR)
        val or2 = Or(11, mutableListOf(Pin.LOW, Pin.HIGH), mutableListOf(Pin.UNDEFINED) , 2, mutableListOf() , mutableListOf() ,
            ComponentType.OR)
        assertEquals(listOf(Pin.HIGH), or.evaluate())
        assertEquals(listOf(Pin.HIGH), or2.evaluate())
    }
    @Test
    fun testOrLow(){
        val or = Or(12, mutableListOf(Pin.LOW, Pin.LOW), mutableListOf(Pin.UNDEFINED) , 2, mutableListOf() , mutableListOf() ,
            ComponentType.OR)
        assertEquals(listOf(Pin.LOW), or.evaluate())
    }
    @Test
    fun testOrError(){
        val or = Or(13, mutableListOf(Pin.ERROR, Pin.ERROR), mutableListOf(Pin.UNDEFINED) , 2, mutableListOf() , mutableListOf() ,
            ComponentType.OR)
        assertEquals(listOf(Pin.ERROR), or.evaluate())
    }

    @Test
    fun testXorHigh(){
        val xor = Xor(14, mutableListOf(Pin.HIGH, Pin.LOW), mutableListOf(Pin.UNDEFINED) , 2, mutableListOf() , mutableListOf() ,
            ComponentType.XOR)
        assertEquals(listOf(Pin.HIGH), xor.evaluate())
    }

    @Test
    fun testXorLow(){
        val xor = Xor(15, mutableListOf(Pin.LOW, Pin.LOW), mutableListOf(Pin.UNDEFINED) , 2, mutableListOf() , mutableListOf() ,
            ComponentType.XOR)
        assertEquals(listOf(Pin.LOW), xor.evaluate())
    }

    @Test
    fun testXorError(){
        val xor = Xor(16, mutableListOf(Pin.ERROR, Pin.ERROR), mutableListOf(Pin.UNDEFINED) , 2, mutableListOf() , mutableListOf() ,
            ComponentType.XOR)
        assertEquals(listOf(Pin.ERROR), xor.evaluate())
    }

    @Test
    fun testXNorLow(){
        val xnor = XNor(17, mutableListOf(Pin.HIGH, Pin.LOW), mutableListOf(Pin.UNDEFINED) , 2, mutableListOf() , mutableListOf() ,
            ComponentType.XNOR)
        assertEquals(listOf(Pin.LOW), xnor.evaluate())
    }

    @Test
    fun testXNorHigh(){
        val xnor = XNor(18, mutableListOf(Pin.LOW, Pin.LOW), mutableListOf(Pin.UNDEFINED) , 2, mutableListOf() , mutableListOf() ,
            ComponentType.XNOR)
        assertEquals(listOf(Pin.HIGH), xnor.evaluate())
    }

    @Test
    fun testXNorError(){
        val xnor = XNor(19, mutableListOf(Pin.ERROR, Pin.ERROR), mutableListOf(Pin.UNDEFINED) , 2, mutableListOf() , mutableListOf() ,
            ComponentType.XNOR)
        assertEquals(listOf(Pin.ERROR), xnor.evaluate())
    }

    @Test
    fun testInversion(){
        val testLow = Invertor(20 , mutableListOf(Pin.HIGH) , mutableListOf(Pin.UNDEFINED) , 1, mutableListOf() , mutableListOf() ,
            ComponentType.XNOR)
        val testHigh = Invertor(21 , mutableListOf(Pin.LOW) , mutableListOf(Pin.UNDEFINED) , 1, mutableListOf() , mutableListOf() ,
            ComponentType.XNOR)
        val testError = Invertor(22 , mutableListOf(Pin.ERROR , Pin.UNDEFINED) , mutableListOf(Pin.UNDEFINED) , 1, mutableListOf() , mutableListOf() ,
            ComponentType.XNOR)
        assertEquals(mutableListOf(Pin.LOW) , testLow.evaluate())
        assertEquals(mutableListOf(Pin.HIGH) , testHigh.evaluate())
        assertEquals( mutableListOf(Pin.ERROR , Pin.ERROR) , testError.evaluate())
    }


}


