package chips74

import logicGates.SimulationComponent
import logicGates.ComponentType
import logicGates.Pin
import logicGates.inputWire
import logicGates.outputWire

class IC7476(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 12,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.IC_7476,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
    var q1: Pin = Pin.LOW,
    var q2: Pin = Pin.LOW,
    var prevClk1: Pin = Pin.LOW,
    var prevClk2: Pin = Pin.LOW
) : SimulationComponent(id) {

    override suspend fun evaluate(): MutableList<Pin> {
        // Inputs: [0: 1PRE, 1: 1CLR, 2: 1J, 3: 1CLK, 4: 1K, 5: GND, 6: 2PRE, 7: 2CLR, 8: 2J, 9: 2CLK, 10: 2K, 11: VCC]
        val pre1Raw = inputs.getOrElse(0) { Pin.LOW }
        val pre1 = if (pre1Raw == Pin.UNDEFINED) Pin.LOW else pre1Raw
        val clr1Raw = inputs.getOrElse(1) { Pin.LOW }
        val clr1 = if (clr1Raw == Pin.UNDEFINED) Pin.LOW else clr1Raw
        val j1 = inputs.getOrElse(2) { Pin.LOW }
        val clk1 = inputs.getOrElse(3) { Pin.HIGH }
        val k1 = inputs.getOrElse(4) { Pin.LOW }
        val gnd = inputs.getOrElse(5) { Pin.LOW }

        val pre2Raw = inputs.getOrElse(6) { Pin.LOW }
        val pre2 = if (pre2Raw == Pin.UNDEFINED) Pin.LOW else pre2Raw
        val clr2Raw = inputs.getOrElse(7) { Pin.LOW }
        val clr2 = if (clr2Raw == Pin.UNDEFINED) Pin.LOW else clr2Raw
        val j2 = inputs.getOrElse(8) { Pin.LOW }
        val clk2 = inputs.getOrElse(9) { Pin.HIGH }
        val k2 = inputs.getOrElse(10) { Pin.LOW }
        val vcc = inputs.getOrElse(11) { Pin.HIGH }

        if (!isChipPowered(gnd, vcc)) {
            output = mutableListOf(Pin.ERROR, Pin.ERROR, Pin.ERROR, Pin.ERROR)
            return output
        }

        q1 = evalJKWithPreClr(pre1, clr1, j1, clk1, k1, prevClk1, q1)
        prevClk1 = clk1

        q2 = evalJKWithPreClr(pre2, clr2, j2, clk2, k2, prevClk2, q2)
        prevClk2 = clk2

        val q1Not = evalNot(q1)
        val q2Not = evalNot(q2)

        output = mutableListOf(q1, q1Not, q2, q2Not)
        return output
    }

    private fun evalJKWithPreClr(pre: Pin, clr: Pin, j: Pin, clk: Pin, k: Pin, prevClk: Pin, currentQ: Pin): Pin {
        val risingEdge = clk == Pin.HIGH && prevClk != Pin.HIGH
        return when {
            pre == Pin.ERROR || clr == Pin.ERROR -> Pin.ERROR
            pre == Pin.HIGH && clr == Pin.HIGH -> Pin.ERROR
            pre == Pin.HIGH && clr == Pin.LOW -> Pin.HIGH
            pre == Pin.LOW && clr == Pin.HIGH -> Pin.LOW
            risingEdge -> when {
                j == Pin.ERROR || k == Pin.ERROR || clk == Pin.ERROR -> Pin.ERROR
                j == Pin.LOW && k == Pin.LOW -> currentQ
                j == Pin.HIGH && k == Pin.LOW -> Pin.HIGH
                j == Pin.LOW && k == Pin.HIGH -> Pin.LOW
                j == Pin.HIGH && k == Pin.HIGH -> evalNot(currentQ)
                else -> Pin.ERROR
            }
            else -> currentQ
        }
    }
}
