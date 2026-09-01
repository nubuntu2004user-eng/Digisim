package com.example.digisim.DrawingLogic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
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
import com.example.digisim.Wiring.Input
import com.example.digisim.Wiring.Output
import com.example.digisim.ParsingLogic.PortHit
import com.example.digisim.ParsingLogic.Wire
import com.example.digisim.Wiring.Clock
import kotlin.math.sqrt

class CanvasViewModel: ViewModel() {
    enum class editingMode { POKE , DRAG , WIRE , EDIT}

    var currentMode by mutableStateOf(editingMode.DRAG)

    var currentWiringColor = Color.Blue

    var selectedGateId by mutableStateOf<Int?>(null)

    val components = mutableStateListOf<Component>()

    val wires = mutableStateListOf<Wire>()
    var nextId by  mutableStateOf(0)

    var dragState by  mutableStateOf<DragState?>(null)
    var wireSource by mutableStateOf<PortHit?>(null)
    var pendingComponent by mutableStateOf<Component?>(null)

    var viewportX by mutableStateOf(0f)
    var viewportY by mutableStateOf(0f)
//    var isPanning by mutableStateOf(false)

    fun addComponent(type: ComponentType) {
        pendingComponent = createComponent(type, id = -1, x = -1000f, y = -1000f)
    }



    // Manual distance calculation
    fun distance(a: Offset, b: Offset): Float =
        sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y))

    fun findPortAt(position: Offset): PortHit? {
        for (component in components) {
            if (component.componentType == ComponentType.INPUT || component.componentType == ComponentType.CLOCK){
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

fun createComponent(
    type: ComponentType,
    id: Int = -1,
    x: Float = 0f,
    y: Float = 0f
): Component {
    return when (type) {
        ComponentType.AND -> And(id, x, y, 2, 1)
        ComponentType.OR -> Or(id, x, y, 2, 1)
        ComponentType.NAND -> Nand(id, x, y, 2, 1)
        ComponentType.NOR -> Nor(id, x, y, 2, 1)
        ComponentType.XOR -> Xor(id, x, y, 2, 1)
        ComponentType.XNOR -> XNor(id, x, y, 2, 1)
        ComponentType.NOT -> Not(id, x, y, 1, 1)
        ComponentType.OUTPUT -> Output(id, x, y, 1, 0)
        ComponentType.INPUT -> Input(id, x, y, 0, 1)
        ComponentType.CLOCK -> Clock(id , x , y ,0 , 1, 999.0f)
    }
}