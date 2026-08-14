package logicGates

 class Nand(id : String,
            override val inputs: List<Pin>,
            override val output: Pin
 ):BasicComponent(id) {

     override fun evaluate(): Pin {
         val result: Pin = when{
             inputs.any { it == Pin.ERROR || it == Pin.UNDEFINED} -> Pin.ERROR
             inputs.all { it == Pin.HIGH } -> Pin.LOW
             else -> Pin.HIGH
         }
         return result
     }
}