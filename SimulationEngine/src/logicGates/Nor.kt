package logicGates

class Nor(id : String,
    override var inputs: List<Pin>,
    override var output: List<Pin>
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