package logicGates

class Invertor {

    fun invert(input : Pin):Pin{
        val result = when (input) {
            Pin.HIGH -> Pin.LOW
            Pin.LOW -> Pin.HIGH
            else -> Pin.ERROR
        }
        return result
    }


    }
