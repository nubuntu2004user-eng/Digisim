package logicGates

class Nor(id : String,
    override val inputs: List<Pin>,
    override val output: Pin
):BasicComponent(id) {
    override fun evaluate(): Pin {
        val result = when{
            inputs.any { it == Pin.ERROR || it == Pin.UNDEFINED} -> Pin.ERROR
            inputs.all { it == Pin.LOW } -> Pin.HIGH
            else -> Pin.LOW
        }
        return result
    }
}