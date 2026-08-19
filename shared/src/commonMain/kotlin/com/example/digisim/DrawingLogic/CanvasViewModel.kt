package com.example.digisim.DrawingLogic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import com.example.digisim.LogicGates.And
import com.example.digisim.LogicGates.Nand
import com.example.digisim.LogicGates.Nor
import com.example.digisim.LogicGates.Not
import com.example.digisim.LogicGates.Or
import com.example.digisim.LogicGates.XNor
import com.example.digisim.LogicGates.Xor
import com.example.digisim.ParsingLogic.Component
import com.example.digisim.ParsingLogic.ComponentType
import com.example.digisim.ParsingLogic.DragState
import com.example.digisim.LogicGates.Input
import com.example.digisim.LogicGates.Output
import com.example.digisim.ParsingLogic.PortHit
import com.example.digisim.ParsingLogic.Wire
import kotlin.math.sqrt

class CanvasViewModel: ViewModel() {
    enum class editingMode { POKE , DRAG , WIRE , EDIT}

    var currentMode by mutableStateOf(editingMode.POKE)

    var selectedGateId by mutableStateOf<Int?>(null)

    val components = mutableStateListOf<Component>()

//    val inputsAndOutputs = mutableStateListOf<InputOrOutput>()

//    val gates =  mutableStateListOf<Gate>()
    val wires = mutableStateListOf<Wire>()
    var nextId by  mutableStateOf(0)

    var dragState by  mutableStateOf<DragState?>(null)
    var wireSource by mutableStateOf<PortHit?>(null)

    fun addComponent(type: ComponentType) {
        val x = 50f + components.size * 20f
        val y = 50f + components.size * 20f
        when (type){
            ComponentType.AND -> { components.add(And(nextId++, x, y, 2, 1))}
            ComponentType.OR -> { components.add(Or(nextId++, x, y, 2, 1))}
            ComponentType.NAND -> { components.add(Nand(nextId++, x, y, 2, 1))}
            ComponentType.NOR -> { components.add(Nor(nextId++, x, y, 2, 1))}
            ComponentType.XOR -> { components.add(Xor(nextId++, x, y, 2, 1))}
            ComponentType.XNOR -> { components.add(XNor(nextId++, x, y, 2, 1))}
            ComponentType.NOT -> { components.add(Not(nextId++, x, y, 1, 1))}
            ComponentType.OUTPUT -> { components.add(Output(nextId++, x, y , 1 , 0))}
            ComponentType.INPUT -> { components.add(Input(nextId++, x, y , 0 , 1))}

        }
    }



    // Manual distance calculation
    fun distance(a: Offset, b: Offset): Float =
        sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y))

    fun findPortAt(position: Offset): PortHit? {
        for (component in components) {
            if (component.componentType == ComponentType.INPUT){
                if (distance(position, component.findPortOffset()) < 12f){
                return   PortHit(component.ID, 0, false, component.findPortOffset())
            }
            }
            if (component.componentType == ComponentType.OUTPUT){
                if (distance(position, component.findPortOffset()) < 12f) {
                return   PortHit(component.ID, 0, true, component.findPortOffset())
            }}
            component.inputPortPositions().forEachIndexed { idx, portPos ->
                if (distance(position, portPos) < 12f) {

                    return PortHit(component.ID, idx, true, portPos)
                }
            }
            component.outputPortPositions().forEachIndexed { idx, portPos ->
                if (distance(position, portPos) < 12f) {
                    return PortHit(component.ID, idx, false, portPos)
                }
            }
        }



        return null
    }

    fun findGate(id: Int): Component? = components.find { it.ID == id }
}