package logicGates

abstract class Component(val id : String) {
    abstract val inputs : List<Pin>
    abstract val output : MutableList<Pin>
    abstract fun evaluate():List<Pin>
}
enum class Pin { HIGH, LOW , UNDEFINED , ERROR}