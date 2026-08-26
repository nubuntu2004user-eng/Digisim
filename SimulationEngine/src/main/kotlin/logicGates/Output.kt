package logicGates

class Output(id : Int,
             override var inputs: MutableList<Pin>,
             override var output: MutableList<Pin>,
             override var inputCount: Int = 0,
             override val inputFrom: MutableList<inputWire>,
             override val outputTo: MutableList<outputWire>,
             override val componentType: ComponentType = ComponentType.OUTPUT
    ): BasicComponent(id) {
    override fun evaluate(): MutableList<Pin> {
        return inputs.toMutableList()
    }
}