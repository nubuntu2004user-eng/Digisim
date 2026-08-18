package com.example.digisim.LogicGates

import androidx.compose.ui.geometry.Offset
import com.example.digisim.ParsingLogic.Component
import com.example.digisim.ParsingLogic.ComponentType


data class Not(
    val id: Int,
    override var x: Float,       // top‑left x
    override var y: Float,        // top‑left y
    override var inputCount : Int,
    override var outputCount : Int
): Component(id) {
    override val componentType = ComponentType.NOT
    override var width = 80f
    override var height = 60f

    override fun findPortOffset() = Offset(0f, 0f)

    fun inputPortOffsets(): List<Offset> {
        val spacing = height / (inputCount + 1)
        return (1..inputCount).map { i -> Offset(0f, spacing * i) }
    }

    fun outputPortOffsets(): List<Offset> {
        val spacing = height / (inputCount + 1)
        return (1..inputCount).map { i -> Offset(width, spacing * i) }
    }

    override  fun inputPortPositions(): List<Offset> =
        inputPortOffsets().map { it + Offset(x, y) }

    override fun outputPortPositions(): List<Offset> =
        outputPortOffsets().map { it + Offset(x, y) }
}