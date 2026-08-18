package com.example.digisim.ParsingLogic

import androidx.compose.ui.geometry.Offset

abstract class InputOrOutput(val id : Int) {
    abstract var x : Float
    abstract var y : Float

    abstract val width : Float

    abstract val height : Float


    abstract fun findPortOffset(): Offset
}