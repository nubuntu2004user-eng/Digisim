package flipFlops

import logicGates.BasicComponent
import logicGates.ComponentType
import logicGates.Pin
import logicGates.inputWire
import logicGates.outputWire

class TFlipFlop(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 2,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.T_FLIP_FLOP,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
    var currentQ: Pin = Pin.LOW,
    var prevClock: Pin = Pin.LOW
) : BasicComponent(id) {

    override suspend fun evaluate(): MutableList<Pin> {
        val t = inputs.getOrElse(0) { Pin.LOW }
        val clk = if (inputCount >= 2) inputs.getOrElse(1) { Pin.HIGH } else Pin.HIGH

        val isRisingEdge = clk == Pin.HIGH && prevClock != Pin.HIGH

        if (isRisingEdge) {
            currentQ = when (t) {
                Pin.HIGH -> {
                    when (currentQ) {
                        Pin.HIGH -> Pin.LOW
                        Pin.LOW -> Pin.HIGH
                        else -> Pin.ERROR
                    }
                }
                Pin.LOW -> currentQ // Hold
                Pin.ERROR -> Pin.ERROR
                Pin.UNDEFINED -> Pin.ERROR
            }
        }

        prevClock = clk

        val qNot = when (currentQ) {
            Pin.HIGH -> Pin.LOW
            Pin.LOW -> Pin.HIGH
            else -> Pin.ERROR
        }

        output = mutableListOf(currentQ, qNot)
        return output
    }
}
