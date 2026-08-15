package logicGates

class Xor(id : String,
    override var inputs: List<Pin>,
    override var output: List<Pin>,
          override var inputCount: Int

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