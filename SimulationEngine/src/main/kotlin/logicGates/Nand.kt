package logicGates

 class Nand(id : Int,
            override var inputs: MutableList<Pin>,
            override var output: MutableList<Pin>,
            override var inputCount: Int,
            override val inputFrom: MutableList<inputWire>,
            override val outputTo: MutableList<outputWire>,
            override val componentType: ComponentType = ComponentType.NAND

 ):BasicComponent(id) {

     override fun evaluate(): MutableList<Pin> {
         val result: Pin = when{
             inputs.any { it == Pin.ERROR || it == Pin.UNDEFINED} -> Pin.ERROR
             inputs.all { it == Pin.HIGH } -> Pin.LOW
             else -> Pin.HIGH
         }
         return mutableListOf(result)
     }
}