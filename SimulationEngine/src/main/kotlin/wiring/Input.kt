package wiring

import logicGates.BasicComponent
import logicGates.ComponentType
import logicGates.Pin
import logicGates.inputWire
import logicGates.outputWire

class Input(id : Int,
            override var inputs: MutableList<Pin>,
            override var output: MutableList<Pin>,
            override var inputCount: Int = 0,
            override val inputFrom: MutableList<inputWire>,
            override val outputTo: MutableList<outputWire>,
            override val componentType : ComponentType = ComponentType.INPUT,
            override var delayTicks: Float? = null,
            override var highDuration: Int? = null,
            override var lowDuration: Int? = null
): BasicComponent(id) {
    override suspend fun evaluate(): MutableList<Pin> {
            return output.toMutableList()
    }
    fun switch(){
        if (output[0] == Pin.LOW) output = mutableListOf(Pin.HIGH)
        else if (output[0] == Pin.HIGH) output = mutableListOf(Pin.LOW)

    }
}