package chips74

import logicGates.SimulationComponent
import logicGates.ComponentType
import logicGates.Pin
import logicGates.inputWire
import logicGates.outputWire

class IC7474(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 10,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.IC_7474,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
    var q1: Pin = Pin.LOW,
    var q2: Pin = Pin.LOW,
    var prevClk1: Pin = Pin.LOW,
    var prevClk2: Pin = Pin.LOW
) : SimulationComponent(id) {

    override suspend fun evaluate(): MutableList<Pin> {
        // Inputs: [0: 1CLR, 1: 1D, 2: 1CLK, 3: 1PRE, 4: GND, 5: 2CLR, 6: 2D, 7: 2CLK, 8: 2PRE, 9: VCC]
        val clr1Raw = inputs.getOrElse(0) { Pin.LOW }
        val clr1 = if (clr1Raw == Pin.UNDEFINED) Pin.LOW else clr1Raw
        val d1 = inputs.getOrElse(1) { Pin.LOW }
        val clk1 = inputs.getOrElse(2) { Pin.HIGH }
        val pre1Raw = inputs.getOrElse(3) { Pin.LOW }
        val pre1 = if (pre1Raw == Pin.UNDEFINED) Pin.LOW else pre1Raw
        val gnd = inputs.getOrElse(4) { Pin.LOW }
        val clr2Raw = inputs.getOrElse(5) { Pin.LOW }
        val clr2 = if (clr2Raw == Pin.UNDEFINED) Pin.LOW else clr2Raw
        val d2 = inputs.getOrElse(6) { Pin.LOW }
        val clk2 = inputs.getOrElse(7) { Pin.HIGH }
        val pre2Raw = inputs.getOrElse(8) { Pin.LOW }
        val pre2 = if (pre2Raw == Pin.UNDEFINED) Pin.LOW else pre2Raw
        val vcc = inputs.getOrElse(9) { Pin.HIGH }

        if (!isChipPowered(gnd, vcc)) {
            output = mutableListOf(Pin.ERROR, Pin.ERROR, Pin.ERROR, Pin.ERROR)
            return output
        }

        q1 = evalDWithPreClr(pre1, clr1, d1, clk1, prevClk1, q1)
        prevClk1 = clk1

        q2 = evalDWithPreClr(pre2, clr2, d2, clk2, prevClk2, q2)
        prevClk2 = clk2

        val q1Not = evalNot(q1)
        val q2Not = evalNot(q2)

        output = mutableListOf(q1, q1Not, q2, q2Not)
        return output
    }

    private fun evalDWithPreClr(pre: Pin, clr: Pin, d: Pin, clk: Pin, prevClk: Pin, currentQ: Pin): Pin {
        val risingEdge = clk == Pin.HIGH && prevClk != Pin.HIGH
        return when {
            pre == Pin.ERROR || clr == Pin.ERROR -> Pin.ERROR
            pre == Pin.HIGH && clr == Pin.HIGH -> Pin.ERROR
            pre == Pin.HIGH && clr == Pin.LOW -> Pin.HIGH
            pre == Pin.LOW && clr == Pin.HIGH -> Pin.LOW
            risingEdge -> when (d) {
                Pin.HIGH -> Pin.HIGH
                Pin.LOW -> Pin.LOW
                else -> Pin.ERROR
            }
            else -> currentQ
        }
    }
}
