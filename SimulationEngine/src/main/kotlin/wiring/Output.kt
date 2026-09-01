package wiring

import logicGates.BasicComponent
import logicGates.ComponentType
import logicGates.Pin
import logicGates.inputWire
import logicGates.outputWire

class Output(id : Int,
             override var inputs: MutableList<Pin>,
             override var output: MutableList<Pin>,
             override var inputCount: Int = 0,
             override val inputFrom: MutableList<inputWire>,
             override val outputTo: MutableList<outputWire>,
             override val componentType: ComponentType = ComponentType.OUTPUT,
             override var highDuration: Int? = null,
             override var lowDuration: Int? = null,
             override var delayTicks: Float? = null
    ): BasicComponent(id) {
    override suspend fun evaluate(): MutableList<Pin> {
        return inputs.toMutableList()
    }
}