package flipFlops

import logicGates.BasicComponent
import logicGates.ComponentType
import logicGates.Pin
import logicGates.inputWire
import logicGates.outputWire

class SRFlipFlopAsync(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 5,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.SR_FLIP_FLOP_ASYNC,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
    var currentQ: Pin = Pin.LOW,
    var prevClock: Pin = Pin.LOW
) : BasicComponent(id) {

    override suspend fun evaluate(): MutableList<Pin> {
        // Pin layout: 0: SET, 1: S, 2: CLK, 3: R, 4: RESET
        val setRaw = inputs.getOrElse(0) { Pin.LOW }
        val set = if (setRaw == Pin.UNDEFINED) Pin.LOW else setRaw

        val s = inputs.getOrElse(1) { Pin.LOW }
        val clk = inputs.getOrElse(2) { Pin.HIGH }
        val r = inputs.getOrElse(3) { Pin.LOW }

        val resetRaw = inputs.getOrElse(4) { Pin.LOW }
        val reset = if (resetRaw == Pin.UNDEFINED) Pin.LOW else resetRaw

        val isRisingEdge = clk == Pin.HIGH && prevClock != Pin.HIGH

        currentQ = when {
            set == Pin.ERROR || reset == Pin.ERROR -> Pin.ERROR
            set == Pin.HIGH && reset == Pin.HIGH -> Pin.ERROR // Invalid condition
            set == Pin.HIGH && reset == Pin.LOW -> Pin.HIGH // Asynchronous Set overrides clock
            set == Pin.LOW && reset == Pin.HIGH -> Pin.LOW // Asynchronous Reset overrides clock
            set == Pin.LOW && reset == Pin.LOW -> {
                if (isRisingEdge) {
                    when {
                        s == Pin.ERROR || r == Pin.ERROR || clk == Pin.ERROR -> Pin.ERROR
                        s == Pin.HIGH && r == Pin.HIGH -> Pin.ERROR // Invalid condition in SR flip-flop
                        s == Pin.HIGH && r == Pin.LOW -> Pin.HIGH
                        s == Pin.LOW && r == Pin.HIGH -> Pin.LOW
                        s == Pin.LOW && r == Pin.LOW -> currentQ // Hold
                        else -> Pin.ERROR
                    }
                } else {
                    currentQ // Hold state when no clock edge
                }
            }
            else -> Pin.ERROR
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
