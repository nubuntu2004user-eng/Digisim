package logicGates

class And(id : Int,
          override var inputs: MutableList<Pin>,
          override var output: MutableList<Pin>,
          override var inputCount: Int,
          override val inputFrom: MutableList<inputWire>,
          override val outputTo: MutableList<outputWire>,
          override val componentType: ComponentType = ComponentType.AND,
          override var highDuration: Int? = null,
          override var lowDuration: Int? = null,
          override var delayTicks: Float? = null

):BasicComponent(id) {
    override suspend fun evaluate(): MutableList<Pin> {
        val result:Pin = when{
            inputs.any { it == Pin.ERROR || it == Pin.UNDEFINED} -> Pin.ERROR
            inputs.any { it == Pin.LOW } -> Pin.LOW
            else -> Pin.HIGH
        }
        return mutableListOf(result)
    }
}