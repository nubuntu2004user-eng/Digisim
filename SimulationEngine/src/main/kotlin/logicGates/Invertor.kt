package logicGates

class Invertor(id : Int,
               override var inputs: MutableList<Pin>,
               override var output: MutableList<Pin>,
               override var inputCount: Int,
               override val inputFrom: MutableList<inputWire>,
               override val outputTo: MutableList<outputWire>,
               override val componentType: ComponentType = ComponentType.NOT,
               override var highDuration: Int? = null,
               override var lowDuration: Int? = null,
               override var delay: Int? = null

): BasicComponent(id) {

    override suspend fun evaluate(): MutableList<Pin> {
        val result = mutableListOf<Pin>()
        for (i in inputs){
            when(i){
                Pin.LOW -> result.add(Pin.HIGH)
                Pin.HIGH -> result.add(Pin.LOW)
                else -> result.add(Pin.ERROR)

            }

        }
        return result
    }


    }
