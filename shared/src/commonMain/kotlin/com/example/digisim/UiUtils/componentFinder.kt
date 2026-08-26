package com.example.digisim.UiUtils

import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.digisim.SimulationHandling.SimulationViewModel
import logicGates.BasicComponent

internal fun findComponent(id : Int?, simulationViewModel: SimulationViewModel): MutableList<BasicComponent>{
    val result = mutableListOf<BasicComponent>()
    for (i in simulationViewModel.componentsState){
        result += i.filter { it.id == id }
    }
    return result
}