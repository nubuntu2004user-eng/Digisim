package logicGates

class Invertor(id : String,
               override var inputs: List<Pin>,
               override var output: List<Pin>
): BasicComponent(id) {

    override fun evaluate(): MutableList<Pin> {
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
