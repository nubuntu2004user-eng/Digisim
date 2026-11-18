package tests

import arithmetic.*
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import logicGates.*
import memory.*
import org.junit.Test
import plexers.*
import wiring.*

class LogisimModulesTests {

    @Test
    fun testBuffersAndParityGates() = runBlocking {
        val buf = Buffer(1, mutableListOf(Pin.HIGH), mutableListOf(), inputFrom = mutableListOf(), outputTo = mutableListOf())
        assertEquals(listOf(Pin.HIGH), buf.evaluate())
        buf.inputs[0] = Pin.LOW
        assertEquals(listOf(Pin.LOW), buf.evaluate())

        val triBuf = ControlledBuffer(2, mutableListOf(Pin.HIGH, Pin.LOW), mutableListOf(), inputFrom = mutableListOf(), outputTo = mutableListOf())
        assertEquals(listOf(Pin.UNDEFINED), triBuf.evaluate())
        triBuf.inputs[1] = Pin.HIGH
        assertEquals(listOf(Pin.HIGH), triBuf.evaluate())

        val triInv = ControlledInverter(3, mutableListOf(Pin.HIGH, Pin.HIGH), mutableListOf(), inputFrom = mutableListOf(), outputTo = mutableListOf())
        assertEquals(listOf(Pin.LOW), triInv.evaluate())
        triInv.inputs[1] = Pin.LOW
        assertEquals(listOf(Pin.UNDEFINED), triInv.evaluate())

        val oddPar = OddParity(4, mutableListOf(Pin.HIGH, Pin.LOW, Pin.HIGH), mutableListOf(), inputCount = 3, inputFrom = mutableListOf(), outputTo = mutableListOf())
        assertEquals(listOf(Pin.LOW), oddPar.evaluate())
        oddPar.inputs[1] = Pin.HIGH
        assertEquals(listOf(Pin.HIGH), oddPar.evaluate())

        val evenPar = EvenParity(5, mutableListOf(Pin.HIGH, Pin.LOW, Pin.HIGH), mutableListOf(), inputCount = 3, inputFrom = mutableListOf(), outputTo = mutableListOf())
        assertEquals(listOf(Pin.HIGH), evenPar.evaluate())
        evenPar.inputs[1] = Pin.HIGH
        assertEquals(listOf(Pin.LOW), evenPar.evaluate())
    }

    @Test
    fun testPlexersLibrary() = runBlocking {
        // 2:1 MUX: [D0, D1, SEL]
        val mux2 = Multiplexer2To1(10, mutableListOf(Pin.HIGH, Pin.LOW, Pin.LOW), mutableListOf(), inputFrom = mutableListOf(), outputTo = mutableListOf())
        assertEquals(listOf(Pin.HIGH), mux2.evaluate())
        mux2.inputs[2] = Pin.HIGH
        assertEquals(listOf(Pin.LOW), mux2.evaluate())

        // 4:1 MUX: [D0..D3, SEL0, SEL1]
        val mux4 = Multiplexer4To1(
            11,
            mutableListOf(Pin.LOW, Pin.LOW, Pin.HIGH, Pin.LOW, Pin.LOW, Pin.HIGH), // D2=HIGH, SEL=2 (S0=0, S1=1)
            mutableListOf(),
            inputFrom = mutableListOf(),
            outputTo = mutableListOf()
        )
        assertEquals(listOf(Pin.HIGH), mux4.evaluate())

        // 1:2 DEMUX: [D, SEL]
        val demux2 = Demultiplexer1To2(12, mutableListOf(Pin.HIGH, Pin.HIGH), mutableListOf(), inputFrom = mutableListOf(), outputTo = mutableListOf())
        assertEquals(listOf(Pin.LOW, Pin.HIGH), demux2.evaluate())

        // 1:4 DEMUX: [D, SEL0, SEL1]
        val demux4 = Demultiplexer1To4(13, mutableListOf(Pin.HIGH, Pin.HIGH, Pin.HIGH), mutableListOf(), inputFrom = mutableListOf(), outputTo = mutableListOf())
        assertEquals(listOf(Pin.LOW, Pin.LOW, Pin.LOW, Pin.HIGH), demux4.evaluate())

        // 2:4 Decoder: [A0, A1, EN]
        val dec24 = Decoder2To4(14, mutableListOf(Pin.HIGH, Pin.LOW, Pin.HIGH), mutableListOf(), inputFrom = mutableListOf(), outputTo = mutableListOf())
        assertEquals(listOf(Pin.LOW, Pin.HIGH, Pin.LOW, Pin.LOW), dec24.evaluate())

        // 3:8 Decoder: [A0, A1, A2, EN] -> select 6 (A0=0, A1=1, A2=1)
        val dec38 = Decoder3To8(15, mutableListOf(Pin.LOW, Pin.HIGH, Pin.HIGH, Pin.HIGH), mutableListOf(), inputFrom = mutableListOf(), outputTo = mutableListOf())
        assertEquals(List(8) { idx -> if (idx == 6) Pin.HIGH else Pin.LOW }, dec38.evaluate())

        // 4:2 Priority Encoder: [D0, D1, D2, D3] -> D1=1, D2=1 -> highest is 2 (A0=0, A1=1, VALID=1)
        val priEnc = PriorityEncoder4To2(16, mutableListOf(Pin.LOW, Pin.HIGH, Pin.HIGH, Pin.LOW), mutableListOf(), inputFrom = mutableListOf(), outputTo = mutableListOf())
        assertEquals(listOf(Pin.LOW, Pin.HIGH, Pin.HIGH), priEnc.evaluate())
    }

    @Test
    fun testArithmeticLibrary() = runBlocking {
        val halfAdder = HalfAdder(20, mutableListOf(Pin.HIGH, Pin.HIGH), mutableListOf(), inputFrom = mutableListOf(), outputTo = mutableListOf())
        assertEquals(listOf(Pin.LOW, Pin.HIGH), halfAdder.evaluate()) // SUM=0, CARRY=1

        val fullAdder = FullAdder(21, mutableListOf(Pin.HIGH, Pin.HIGH, Pin.HIGH), mutableListOf(), inputFrom = mutableListOf(), outputTo = mutableListOf())
        assertEquals(listOf(Pin.HIGH, Pin.HIGH), fullAdder.evaluate()) // SUM=1, COUT=1

        // 4-bit Adder: 6 (0110) + 5 (0101) + 1 = 12 (1100, COUT=0)
        val adder4 = Adder4Bit(
            22,
            mutableListOf(
                Pin.LOW, Pin.HIGH, Pin.HIGH, Pin.LOW, // A = 6
                Pin.HIGH, Pin.LOW, Pin.HIGH, Pin.LOW, // B = 5
                Pin.HIGH                              // CIN = 1
            ),
            mutableListOf(),
            inputFrom = mutableListOf(),
            outputTo = mutableListOf()
        )
        assertEquals(listOf(Pin.LOW, Pin.LOW, Pin.HIGH, Pin.HIGH, Pin.LOW), adder4.evaluate())

        // 4-bit Subtractor: 3 (0011) - 5 (0101) - 0 = -2 -> 14 (1110) with BOUT=1
        val sub4 = Subtractor4Bit(
            23,
            mutableListOf(
                Pin.HIGH, Pin.HIGH, Pin.LOW, Pin.LOW, // A = 3
                Pin.HIGH, Pin.LOW, Pin.HIGH, Pin.LOW, // B = 5
                Pin.LOW                               // BIN = 0
            ),
            mutableListOf(),
            inputFrom = mutableListOf(),
            outputTo = mutableListOf()
        )
        assertEquals(listOf(Pin.LOW, Pin.HIGH, Pin.HIGH, Pin.HIGH, Pin.HIGH), sub4.evaluate())

        // 4-bit Comparator: A = 10 (1010), B = 10 (1010) -> [GT=0, EQ=1, LT=0]
        val comp4 = Comparator4Bit(
            24,
            mutableListOf(
                Pin.LOW, Pin.HIGH, Pin.LOW, Pin.HIGH,
                Pin.LOW, Pin.HIGH, Pin.LOW, Pin.HIGH
            ),
            mutableListOf(),
            inputFrom = mutableListOf(),
            outputTo = mutableListOf()
        )
        assertEquals(listOf(Pin.LOW, Pin.HIGH, Pin.LOW), comp4.evaluate())

        // 4-bit Negator: A = 5 (0101) -> -5 mod 16 = 11 (1011 -> Y0=1, Y1=1, Y2=0, Y3=1)
        val neg4 = Negator4Bit(25, mutableListOf(Pin.HIGH, Pin.LOW, Pin.HIGH, Pin.LOW), mutableListOf(), inputFrom = mutableListOf(), outputTo = mutableListOf())
        assertEquals(listOf(Pin.HIGH, Pin.HIGH, Pin.LOW, Pin.HIGH), neg4.evaluate())
    }

    @Test
    fun testSequentialMemoryLibrary() = runBlocking {
        // 4-bit Register: [D0..D3, CLK, EN, CLR]
        val reg = Register4Bit(
            30,
            mutableListOf(Pin.HIGH, Pin.LOW, Pin.HIGH, Pin.HIGH, Pin.HIGH, Pin.HIGH, Pin.LOW), // D = 13 (1101), CLK rising
            mutableListOf(),
            inputFrom = mutableListOf(),
            outputTo = mutableListOf()
        )
        assertEquals(listOf(Pin.HIGH, Pin.LOW, Pin.HIGH, Pin.HIGH), reg.evaluate())
        // Clear register
        reg.inputs[6] = Pin.HIGH
        assertEquals(listOf(Pin.LOW, Pin.LOW, Pin.LOW, Pin.LOW), reg.evaluate())

        // 4-bit Counter: [0: CLK, 1: EN, 2: UP_DOWN, 3: CLR, 4: LOAD, 5..8: D0..D3]
        val counter = Counter4Bit(
            31,
            mutableListOf(Pin.HIGH, Pin.HIGH, Pin.HIGH, Pin.LOW, Pin.LOW, Pin.LOW, Pin.LOW, Pin.LOW, Pin.LOW),
            mutableListOf(),
            inputFrom = mutableListOf(),
            outputTo = mutableListOf()
        )
        // First rising edge -> count = 1
        assertEquals(listOf(Pin.HIGH, Pin.LOW, Pin.LOW, Pin.LOW, Pin.LOW), counter.evaluate())
        // Bring CLK LOW then HIGH -> count = 2
        counter.inputs[0] = Pin.LOW
        counter.evaluate()
        counter.inputs[0] = Pin.HIGH
        assertEquals(listOf(Pin.LOW, Pin.HIGH, Pin.LOW, Pin.LOW, Pin.LOW), counter.evaluate())

        // 4-bit Shift Register: [0: SER_IN, 1: CLK, 2: LOAD, 3: CLR, 4..7: D0..D3]
        val shiftReg = ShiftRegister4Bit(
            32,
            mutableListOf(Pin.HIGH, Pin.HIGH, Pin.LOW, Pin.LOW, Pin.LOW, Pin.LOW, Pin.LOW, Pin.LOW),
            mutableListOf(),
            inputFrom = mutableListOf(),
            outputTo = mutableListOf()
        )
        assertEquals(listOf(Pin.HIGH, Pin.LOW, Pin.LOW, Pin.LOW), shiftReg.evaluate())
        shiftReg.inputs[1] = Pin.LOW
        shiftReg.evaluate()
        shiftReg.inputs[0] = Pin.LOW
        shiftReg.inputs[1] = Pin.HIGH
        assertEquals(listOf(Pin.LOW, Pin.HIGH, Pin.LOW, Pin.LOW), shiftReg.evaluate())

        // 16x4 RAM: write 10 (1010) to address 3 (0011)
        val ram = Ram16x4(
            33,
            mutableListOf(
                Pin.HIGH, Pin.HIGH, Pin.LOW, Pin.LOW, // ADDR = 3
                Pin.LOW, Pin.HIGH, Pin.LOW, Pin.HIGH, // DIN = 10
                Pin.HIGH, Pin.HIGH                    // WE = 1, CLK = 1
            ),
            mutableListOf(),
            inputFrom = mutableListOf(),
            outputTo = mutableListOf()
        )
        assertEquals(listOf(Pin.LOW, Pin.HIGH, Pin.LOW, Pin.HIGH), ram.evaluate())

        // 16x4 ROM: preload address 7 with value 12 (1100)
        val rom = Rom16x4(
            34,
            mutableListOf(Pin.HIGH, Pin.HIGH, Pin.HIGH, Pin.LOW), // ADDR = 7
            mutableListOf(),
            inputFrom = mutableListOf(),
            outputTo = mutableListOf()
        )
        rom.statefulMemory.addAll(List(16) { idx -> if (idx == 7) 12 else 0 })
        assertEquals(listOf(Pin.LOW, Pin.LOW, Pin.HIGH, Pin.HIGH), rom.evaluate())
    }
}
