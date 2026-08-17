package engineLogic

import logicGates.BasicComponent
import logicGates.Pin

 class SimulationState {

    var CircuitData = mutableListOf<MutableList<BasicComponent>>()

    var outputs = mutableListOf<Pin>()  // refactor this, namely add a function that will be managing the flow of simulation and will sava only the last output

}