package com.example.digisim.ParsingLogic

import androidx.compose.ui.geometry.Offset

class Output(
             id : Int,
             override var x: Float,
             override  var y: Float): Component(id) {
              override val componentType = ComponentType.OUTPUT
              override var width = 40f
              override  var height = 40f
            override var inputCount = 1
            override var outputCount = 0
    val isInput = false


    val portOffset = Offset(0f , height / 2)

    override fun inputPortPositions(): List<Offset> = emptyList()


    override fun outputPortPositions(): List<Offset> = emptyList()

    override fun findPortOffset() = portOffset + Offset(x , y)

}