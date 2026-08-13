import junit.framework.TestCase.assertEquals
import logicGates.Nand
import logicGates.Pin
import org.junit.Test


class ComponentTests{
    @Test
    fun testNandHigh() {
    val nand = Nand("Test1", listOf(Pin.LOW, Pin.LOW), mutableListOf())
    val nand2 = Nand("Test2", listOf(Pin.LOW, Pin.HIGH), mutableListOf())
    val nand3 = Nand("Test3", listOf(Pin.HIGH, Pin.LOW), mutableListOf())
    assertEquals(listOf(Pin.HIGH), nand.evaluate())
    assertEquals(listOf(Pin.HIGH), nand2.evaluate())
    assertEquals(listOf(Pin.HIGH), nand3.evaluate())
}
    @Test
    fun testNandLow() {
        val nand = Nand("Test4", listOf(Pin.HIGH, Pin.HIGH), mutableListOf())
        assertEquals(listOf(Pin.LOW), nand.evaluate())
    }


}


