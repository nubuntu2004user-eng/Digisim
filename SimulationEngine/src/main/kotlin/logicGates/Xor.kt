package logicGates

class Xor(id : Int,
          override var inputs: MutableList<Pin>,
          override var output: MutableList<Pin>,
          override var inputCount: Int,
          override val inputFrom: MutableList<inputWire>,
          override val outputTo: MutableList<outputWire>,
          override val componentType: ComponentType = ComponentType.XOR,
          override var highDuration: Int?= null,
          override var lowDuration: Int? = null,
          override var delay: Int? = null


):BasicComponent(id) {
    override suspend fun evaluate(): MutableList<Pin> {
        val result  =  when{
            inputs.any { it == Pin.ERROR || it == Pin.UNDEFINED} -> Pin.ERROR
            inputs.all { it == Pin.LOW } -> Pin.LOW
            inputs.all {it == Pin.HIGH} -> Pin.LOW
            else -> Pin.HIGH
        }
        return mutableListOf(result)
    }
}