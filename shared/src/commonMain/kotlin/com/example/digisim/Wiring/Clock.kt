package com.example.digisim.Wiring

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import com.example.digisim.ParsingLogic.Component
import com.example.digisim.ParsingLogic.ComponentType
import logicGates.SimulationComponent

class Clock(
    id: Int,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialInputCount: Int = 0,
    initialOutputCount: Int = 1,
    initialDelay: Float = 999f,
    label: String = "",
    override val abstractedLogic: MutableList<MutableList<SimulationComponent>>? = null,
    override val customComponentName: String? = null
) : Component(id) {

    override var label: String by mutableStateOf(label)
    override var x: Float by mutableStateOf(initialX)
    override var y: Float by mutableStateOf(initialY)
    override var width: Float by mutableStateOf(80f)
    override var height: Float by mutableStateOf(60f)
    override var inputCount: Int by mutableStateOf(initialInputCount)
    override var outputCount: Int by mutableStateOf(initialOutputCount)
    override var delay: Float? by mutableStateOf(initialDelay)

    override val componentType = ComponentType.CLOCK
    val portOffset = Offset(width , height / 2)

    override fun inputPortPositions(): List<Offset> = emptyList()


    override fun outputPortPositions(): List<Offset> = emptyList()


    override fun findPortOffset() = portOffset + Offset(x , y)

    }
