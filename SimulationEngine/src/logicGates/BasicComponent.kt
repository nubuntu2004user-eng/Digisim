package logicGates

abstract class BasicComponent(val id : String) {
    abstract val inputs : List<Pin>
    abstract val output: Pin
    abstract fun evaluate(): Pin
}
enum class Pin { HIGH, LOW , UNDEFINED , ERROR}