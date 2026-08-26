package logicGates

abstract class BasicComponent(val id : Int) {
    abstract var inputs: MutableList<Pin>
    abstract var output: MutableList<Pin>

    abstract var inputCount : Int

    abstract val inputFrom : MutableList<inputWire>

    abstract val outputTo : MutableList<outputWire>

    abstract val componentType : ComponentType
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

enum class ComponentType { NAND , AND , OR , NOR , XOR , XNOR , NOT , INPUT , OUTPUT}

data class inputWire(
    val sourceGateId : Int,
    val portId : Int,
    var value : Pin = Pin.UNDEFINED
)

data class outputWire(
    val targetGateId : Int,
    val portId : Int,
    var value: Pin = Pin.UNDEFINED
)