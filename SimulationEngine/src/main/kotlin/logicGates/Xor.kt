package logicGates

class Xor(id : Int,
    override var inputs: List<Pin>,
    override var output: List<Pin>,
          override var inputCount: Int,
          override val inputFrom: MutableList<inputWire>,
          override val outputTo: MutableList<outputWire>


):BasicComponent(id) {
    override fun evaluate(): MutableList<Pin> {
        val result  =  when{
            inputs.any { it == Pin.ERROR || it == Pin.UNDEFINED} -> Pin.ERROR
            inputs.all { it == Pin.LOW } -> Pin.LOW
            inputs.all {it == Pin.HIGH} -> Pin.LOW
            else -> Pin.HIGH
        }
        return mutableListOf(result)
    }
}