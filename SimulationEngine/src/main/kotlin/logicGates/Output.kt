package logicGates

class Output(id : Int ,
             override var inputs: List<Pin>,
             override var output: List<Pin>,
             override var inputCount: Int = 0 ,
             override val inputFrom: MutableList<inputWire>,
             override val outputTo: MutableList<outputWire>
    ): BasicComponent(id) {
    override fun evaluate(): MutableList<Pin> {
        return inputs.toMutableList()
    }
}