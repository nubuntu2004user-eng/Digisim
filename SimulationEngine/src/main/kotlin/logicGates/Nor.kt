package logicGates

class Nor(id : Int,
          override var inputs: MutableList<Pin>,
          override var output: MutableList<Pin>,
          override var inputCount: Int,
          override val inputFrom: MutableList<inputWire>,
          override val outputTo: MutableList<outputWire>,
          override val componentType: ComponentType = ComponentType.NOR

):BasicComponent(id) {
    override fun evaluate(): MutableList<Pin> {
        val result = when{
            inputs.any { it == Pin.ERROR || it == Pin.UNDEFINED} -> Pin.ERROR
            inputs.all { it == Pin.LOW } -> Pin.HIGH
            else -> Pin.LOW
        }
        return mutableListOf(result)
    }
}