package logicGates

import engineLogic.computeSimulation
import kotlinx.serialization.Serializable

@Serializable
class SimulationCustomComponent(
    val idParam: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    val abstractedLogic: MutableList<MutableList<SimulationComponent>>,
    override val componentType: ComponentType = ComponentType.CUSTOM,
    override var delayTicks: Float? = null,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
) : SimulationComponent(idParam) {

    // Pre-map Input and Output components to avoid searching every tick
    private val inputComponents: List<SimulationComponent>
    private val outputComponents: List<SimulationComponent>

    init {
        val allComponents = abstractedLogic.flatten()
        inputComponents = allComponents.filter { it.componentType == ComponentType.INPUT }
            .sortedBy { it.id } // Ensure consistent order
        outputComponents = allComponents.filter { it.componentType == ComponentType.OUTPUT }
            .sortedBy { it.id }
    }

    override suspend fun evaluate(): MutableList<Pin> {
        // 1. Map top-level inputs to internal Input components' inputs
        for (i in inputComponents.indices) {
            if (i < inputs.size) {
                inputComponents[i].inputs = mutableListOf(inputs[i])
            }
        }

        // 2. Run simulation on internal logic
        try {
            computeSimulation(abstractedLogic)
        } catch (e: Exception) {
            // Partial propagation: Fill output with ERROR if simulation fails
            output = MutableList(output.size) { Pin.ERROR }
            return output
        }

        // 3. Map internal Output components to top-level outputs
        val newOutput = mutableListOf<Pin>()
        for (outComp in outputComponents) {
            newOutput.add(outComp.inputs.firstOrNull() ?: Pin.ERROR)
        }
        
        output = newOutput
        return output
    }
}
