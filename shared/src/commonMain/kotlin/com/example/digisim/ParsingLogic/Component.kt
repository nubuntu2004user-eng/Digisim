package com.example.digisim.ParsingLogic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import kotlinx.serialization.Serializable
import logicGates.SimulationComponent
import logicGates.Pin
@Serializable
abstract class Component(val ID : Int) {
    abstract var x : Float
    abstract var y : Float
    abstract var width : Float
    abstract var height : Float

    abstract var inputCount : Int

    abstract var outputCount : Int

    abstract val componentType: ComponentType

    open var outputPin: Pin by mutableStateOf(Pin.LOW)
    open var label: String by mutableStateOf("")

    abstract var delay: Float?

    abstract val abstractedLogic : MutableList<MutableList<SimulationComponent>>?

    abstract val customComponentName : String?
    abstract fun inputPortPositions():List<Offset>

    abstract fun outputPortPositions():List<Offset>

    abstract fun findPortOffset() : Offset //for elements with one pin
}

enum class ComponentType {
    AND, NAND, NOR, OR, XNOR, XOR, NOT,
    INPUT, OUTPUT, CLOCK, BUTTON, CUSTOM,
    RS_FLIP_FLOP, JK_FLIP_FLOP, D_FLIP_FLOP, T_FLIP_FLOP,
    JK_FLIP_FLOP_ASYNC, SR_FLIP_FLOP_ASYNC, D_FLIP_FLOP_ASYNC, T_FLIP_FLOP_ASYNC,
    IC_7400, IC_7402, IC_7404, IC_7407, IC_7408,
    IC_7410, IC_7411, IC_7420, IC_7421, IC_7427, IC_7430,
    IC_7432, IC_7473, IC_7474, IC_7476, IC_7486,
    IC_74138, IC_74139, IC_74151, IC_74157, IC_74266, IC_74283
}