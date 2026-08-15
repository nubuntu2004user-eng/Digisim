package engineLogic

import logicGates.BasicComponent
import logicGates.Pin

internal class SimulationState {

    var CircuitData = mutableListOf<List<BasicComponent>>()

    var outputs = mutableListOf<Pin>()

}