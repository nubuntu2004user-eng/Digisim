package flipFlops

import logicGates.BasicComponent
import logicGates.ComponentType
import logicGates.Pin
import logicGates.inputWire
import logicGates.outputWire

class RSFlipFlop(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 2,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.RS_FLIP_FLOP,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
    var currentQ: Pin = Pin.LOW
) : BasicComponent(id) {

    override suspend fun evaluate(): MutableList<Pin> {
        val s = inputs.getOrElse(0) { Pin.LOW }
        val r = inputs.getOrElse(1) { Pin.LOW }

        currentQ = when {
            s == Pin.ERROR || r == Pin.ERROR -> Pin.ERROR
            s == Pin.HIGH && r == Pin.HIGH -> Pin.ERROR // Invalid condition in RS latch
            s == Pin.HIGH && r == Pin.LOW -> Pin.HIGH
            s == Pin.LOW && r == Pin.HIGH -> Pin.LOW
            s == Pin.LOW && r == Pin.LOW -> currentQ // Hold state
            else -> Pin.ERROR
        }

        val qNot = when (currentQ) {
            Pin.HIGH -> Pin.LOW
            Pin.LOW -> Pin.HIGH
            else -> Pin.ERROR
        }

        output = mutableListOf(currentQ, qNot)
        return output
    }
}
