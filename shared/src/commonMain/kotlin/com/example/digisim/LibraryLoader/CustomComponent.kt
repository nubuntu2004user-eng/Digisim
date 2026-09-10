package com.example.digisim.LibraryLoader

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import com.example.digisim.ParsingLogic.Component
import com.example.digisim.ParsingLogic.ComponentType
import logicGates.SimulationComponent

class CustomComponent(
    id : Int,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialInputCount: Int,
    initialOutputCount: Int,
    override var label: String = "",
    override val abstractedLogic: MutableList<MutableList<SimulationComponent>>?,
    override val customComponentName: String?
): Component(id) {

    override var x: Float by mutableStateOf(initialX)
    override var y: Float by mutableStateOf(initialY)
    override var width: Float by mutableStateOf(80f)
    override var height: Float by mutableStateOf(60f)
    override var inputCount: Int by mutableStateOf(initialInputCount)
    override var outputCount: Int by mutableStateOf(initialOutputCount)
    override var delay: Float? = null

    override val componentType = ComponentType.CUSTOM

    override fun findPortOffset(): Offset {
        TODO("Not yet implemented")
    }

    override fun inputPortPositions(): List<Offset> {
        TODO("Not yet implemented")
    }

    override fun outputPortPositions(): List<Offset> {
        TODO("Not yet implemented")
    }

}