package logicGates

class Or(id : String,
         override var inputs: List<Pin>,
         override var output: List<Pin>,
         override var inputCount: Int
):BasicComponent(id) {
    override fun evaluate(): MutableList<Pin> {
        val result = when{
            inputs.any { it == Pin.ERROR || it == Pin.UNDEFINED} -> Pin.ERROR
            inputs.any { it == Pin.HIGH } -> Pin.HIGH
            else -> Pin.LOW
        }
        return mutableListOf(result)
    }
}