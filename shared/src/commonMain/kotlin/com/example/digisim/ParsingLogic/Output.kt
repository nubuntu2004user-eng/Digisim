package com.example.digisim.ParsingLogic

import androidx.compose.ui.geometry.Offset

class Output(
             id : Int,
             override var x: Float,
             override var y: Float): InputOrOutput(id ) {
             override val width = 40f
             override val height = 40f



    val portOffset = Offset(0f , height / 2)

    override fun findPortOffset() = portOffset + Offset(x , y)

}