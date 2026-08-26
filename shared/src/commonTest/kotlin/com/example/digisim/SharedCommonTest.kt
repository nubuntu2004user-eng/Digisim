package com.example.digisim

import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.digisim.LogicGates.And
import com.example.digisim.LogicGates.Input
import com.example.digisim.LogicGates.Output
import com.example.digisim.ParsingLogic.Wire
import com.example.digisim.SimulationHandling.SimulationViewModel
import kotlin.test.Test

class SharedCommonTest {
//Logic tests

    @Test
    fun testSimulation()  {
        val viewModel = CanvasViewModel()
        viewModel.components.addAll(listOf(
            Input(0 , 0f , 0f , 0 ,1),
            Input(1 , 0f , 0f , 0 , 1),
            And(2, 0f, 0f , 2 , 1),
            Output(3 , 0f , 0f , 1 ,0)
             )
        )
        viewModel.wires.addAll(listOf(
            Wire(4 , 0 , 0 ,2 , 0),
            Wire(5 , 1 ,0 , 2, 1),
            Wire(6 , 2 , 0 ,3 , 0)

        ))
        SimulationViewModel().runSimulation(viewModel)
    }

}