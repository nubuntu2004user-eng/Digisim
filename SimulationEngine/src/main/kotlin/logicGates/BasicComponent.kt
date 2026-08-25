package logicGates

abstract class BasicComponent(val id : Int) {
    abstract var inputs : List<Pin>
    abstract var output: List<Pin>

    abstract var inputCount : Int

    abstract val inputFrom : MutableList<inputWire>

    abstract val outputTo : MutableList<outputWire>
    abstract fun evaluate(): MutableList<Pin>

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BasicComponent) return false
        if (this::class != other::class) return false
        return id == other.id && inputs == other.inputs && output == other.output
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + inputs.hashCode()
        result = 31 * result + output.hashCode()
        return result
    }
}

enum class Pin { HIGH, LOW , UNDEFINED , ERROR}

enum class ComponentType { NAND , AND , OR , NOR , XOR , XNOR , NOT}

//data class BasicComponentData(val id : String, //generated on front end (Ui)
//                              val type : ComponentType,
//                              val inputs : List<Pin>,
//                              var output : List<Pin>,
//                              var inputCount: Int = 2
//)

data class inputWire(
    val gateId : Int ,
    val portId : Int
)

data class outputWire(
    val gateId : Int ,
    val portId : Int
)