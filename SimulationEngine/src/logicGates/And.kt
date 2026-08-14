package logicGates

class And(id : String,
    override val inputs: List<Pin>,
    override val output: Pin

):BasicComponent(id) {
    override fun evaluate(): Pin {
        val result:Pin = when{
            inputs.any { it == Pin.ERROR || it == Pin.UNDEFINED} -> Pin.ERROR
            inputs.any { it == Pin.LOW } -> Pin.LOW
            else -> Pin.HIGH
        }
        return result
    }
}