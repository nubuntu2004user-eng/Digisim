package com.example.digisim.LogicGates

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import com.example.digisim.ParsingLogic.Component
import com.example.digisim.ParsingLogic.ComponentType
import com.example.digisim.DrawingLogic.calculateInputPinPositions

 class Xor(
     id: Int,
     initialX: Float = 0f,
     initialY: Float = 0f,
     initialInputCount: Int,
     initialOutputCount: Int
 ) : Component(id) {

     override var x: Float by mutableStateOf(initialX)
     override var y: Float by mutableStateOf(initialY)
     override var width: Float by mutableStateOf(80f)
     override var height: Float by mutableStateOf(60f)
     override var inputCount: Int by mutableStateOf(initialInputCount)
     override var outputCount: Int by mutableStateOf(initialOutputCount)

     override val componentType = ComponentType.XOR

    override fun findPortOffset() = Offset(0f, 0f)

    override fun inputPortPositions(): List<Offset> = calculateInputPinPositions(this)

    fun outputPortOffsets(): List<Offset> {
        val spacing = height / (outputCount + 1)
        return (1..outputCount).map { i -> Offset(width, spacing * i) }
    }

    override fun outputPortPositions(): List<Offset> =
        outputPortOffsets().map { it + Offset(x, y) }
}