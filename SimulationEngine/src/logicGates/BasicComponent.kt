package logicGates

abstract class BasicComponent(val id : String) {
    abstract var inputs : List<Pin>
    abstract var output: List<Pin>
    abstract fun evaluate(): MutableList<Pin>
}

enum class Pin { HIGH, LOW , UNDEFINED , ERROR}

enum class ComponentType { NAND , AND , OR , NOR , XOR , XNOR , NOT}

data class BasicComponentData(val id : String, //generated on front end (Ui)
                              val type : ComponentType,
                              val inputs : List<Pin>,
                              var output : List<Pin>
)