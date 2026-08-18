package com.example.digisim.ParsingLogic

import androidx.compose.ui.geometry.Offset

abstract class Component(val ID : Int) {
    abstract var x : Float
    abstract var y : Float
    abstract var width : Float
    abstract var height : Float

    abstract var inputCount : Int

    abstract var outputCount : Int

    abstract val componentType: ComponentType

    abstract fun inputPortPositions():List<Offset>

    abstract fun outputPortPositions():List<Offset>

    abstract fun findPortOffset() : Offset //for elements with one pin
}

enum class ComponentType {AND , NAND , NOR , OR , XNOR , XOR , NOT , INPUT , OUTPUT}