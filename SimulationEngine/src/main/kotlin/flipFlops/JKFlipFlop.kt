package flipFlops

import logicGates.BasicComponent
import logicGates.ComponentType
import logicGates.Pin
import logicGates.inputWire
import logicGates.outputWire

class JKFlipFlop(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 3,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.JK_FLIP_FLOP,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
    var currentQ: Pin = Pin.LOW,
    var prevClock: Pin = Pin.LOW
) : BasicComponent(id) {

    override suspend fun evaluate(): MutableList<Pin> {
        val j = inputs.getOrElse(0) { Pin.LOW }
        val clk = if (inputCount >= 3) inputs.getOrElse(1) { Pin.HIGH } else Pin.HIGH
        val k = if (inputCount >= 3) inputs.getOrElse(2) { Pin.LOW } else inputs.getOrElse(1) { Pin.LOW }

        val isRisingEdge = clk == Pin.HIGH && prevClock != Pin.HIGH

        if (isRisingEdge) {
            currentQ = when {
                j == Pin.ERROR || k == Pin.ERROR || clk == Pin.ERROR -> Pin.ERROR
                j == Pin.LOW && k == Pin.LOW -> currentQ // No change / Hold
                j == Pin.HIGH && k == Pin.LOW -> Pin.HIGH // Set
                j == Pin.LOW && k == Pin.HIGH -> Pin.LOW // Reset
                j == Pin.HIGH && k == Pin.HIGH -> { // Toggle
                    when (currentQ) {
                        Pin.HIGH -> Pin.LOW
                        Pin.LOW -> Pin.HIGH
                        else -> Pin.ERROR
                    }
                }
                else -> Pin.ERROR
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
