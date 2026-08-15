package logicGates

class And(id : String,
    override var inputs: List<Pin>,
    override var output: List<Pin>

):BasicComponent(id) {
    override fun evaluate(): MutableList<Pin> {
        val result:Pin = when{
            inputs.any { it == Pin.ERROR || it == Pin.UNDEFINED} -> Pin.ERROR
            inputs.any { it == Pin.LOW } -> Pin.LOW
            else -> Pin.HIGH
        }
        return mutableListOf(result)
    }
}