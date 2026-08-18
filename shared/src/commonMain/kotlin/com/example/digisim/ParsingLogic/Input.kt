package com.example.digisim.ParsingLogic

import androidx.compose.ui.geometry.Offset

class Input(
    id : Int,
    override var x: Float,
    override var y: Float): InputOrOutput(id ) {
    override val width = 40f
    override val height = 40f



    val portOffset = Offset(width , height / 2)

    override fun findPortOffset() = portOffset + Offset(x , y)

}