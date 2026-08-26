package logicGates

class Input(id : Int,
            override var inputs: MutableList<Pin>,
            override var output: MutableList<Pin>,
            override var inputCount: Int = 0,
            override val inputFrom: MutableList<inputWire>,
            override val outputTo: MutableList<outputWire>,
            override val componentType : ComponentType = ComponentType.INPUT
    ): BasicComponent(id) {
    override fun evaluate(): MutableList<Pin> {
            return output.toMutableList()
    }
    fun switch(){
        if (output[0] == Pin.LOW) output = mutableListOf(Pin.HIGH)
        else if (output[0] == Pin.HIGH) output = mutableListOf(Pin.LOW)

    }
}