package logicGates

class Input(id : Int,
            override var inputs: List<Pin>,
            override var output: List<Pin>,
            override var inputCount: Int = 0 ,
            override val inputFrom: MutableList<inputWire>,
            override val outputTo: MutableList<outputWire>
    ): BasicComponent(id) {
    override fun evaluate(): MutableList<Pin> {
            return output.toMutableList()
    }
    fun switch(){
        if (output[0] == Pin.LOW) output = listOf(Pin.HIGH)
        else if (output[0] == Pin.HIGH) output = listOf(Pin.LOW)

    }
}