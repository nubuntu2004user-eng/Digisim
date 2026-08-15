package engineLogic

import logicGates.BasicComponent
import logicGates.Pin

 class SimulationState {

    var CircuitData = mutableListOf<MutableList<BasicComponent>>()

    var outputs = mutableListOf<Pin>()

}