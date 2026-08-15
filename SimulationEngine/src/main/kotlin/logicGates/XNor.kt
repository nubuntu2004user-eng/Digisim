package logicGates

class XNor(id : String,
    override var inputs: List<Pin>,
    override var output: List<Pin>,
           override var inputCount: Int
):BasicComponent(id) {
    override fun evaluate(): MutableList<Pin> {
        val result = when{
            inputs.all { it == Pin.LOW } -> Pin.HIGH
            inputs.all { it == Pin.HIGH } -> Pin.HIGH
            inputs.any { it == Pin.ERROR || it == Pin.UNDEFINED} -> Pin.ERROR
            else -> Pin.LOW
        }
        return mutableListOf(result)
    }
}