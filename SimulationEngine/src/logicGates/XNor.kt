package logicGates

class XNor(id : String,
    override val inputs: List<Pin>,
    override val output: Pin
    ):BasicComponent(id) {
    override fun evaluate(): Pin {
        val result = when{
            inputs.all { it == Pin.LOW } -> Pin.HIGH
            inputs.all { it == Pin.HIGH } -> Pin.HIGH
            inputs.any { it == Pin.ERROR || it == Pin.UNDEFINED} -> Pin.ERROR
            else -> Pin.LOW
        }
        return result
    }
}