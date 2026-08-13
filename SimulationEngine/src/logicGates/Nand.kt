package logicGates

 class Nand(id : String,
            override val inputs: List<Pin>,
            override val output: MutableList<Pin>
 ):Component(id) {

     override fun evaluate(): List<Pin> {
         var result: Pin = when{
             inputs.any { it == Pin.ERROR || it == Pin.UNDEFINED} -> Pin.ERROR
             inputs.all { it == Pin.HIGH } -> Pin.LOW
             else -> Pin.HIGH
         }
         output.add(result)
         return output
     }
}