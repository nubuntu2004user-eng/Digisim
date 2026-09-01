package wiring

import logicGates.BasicComponent
import logicGates.ComponentType
import logicGates.Pin
import logicGates.inputWire
import logicGates.outputWire

class Button(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 0,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.BUTTON,
    override var delayTicks: Float? = null,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null
) : BasicComponent(id) {

    override suspend fun evaluate(): MutableList<Pin> {
        val currentOut = output.toMutableList()
        // If it was pressed (HIGH), provide HIGH for this 1 tick, then automatically reset to LOW
        if (output.firstOrNull() == Pin.HIGH) {
            output = mutableListOf(Pin.LOW)
        }
        return currentOut
    }

    fun press() {
        output = mutableListOf(Pin.HIGH)
    }
}
