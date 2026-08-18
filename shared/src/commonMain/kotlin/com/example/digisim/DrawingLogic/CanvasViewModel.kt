package com.example.digisim.DrawingLogic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import kotlin.math.sqrt

class CanvasViewModel: ViewModel() {
    val gates =  mutableStateListOf<Gate>()
    val wires = mutableStateListOf<Wire>()
    var nextId by  mutableStateOf(0)

    var dragState by  mutableStateOf<DragState?>(null)
    var wireSource by mutableStateOf<PortHit?>(null)

    fun addGate(type: GateType) {
        val x = 50f + gates.size * 20f
        val y = 50f + gates.size * 20f
        gates.add(Gate(nextId++, type, x, y))
    }

    // Manual distance calculation
    fun distance(a: Offset, b: Offset): Float =
        sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y))

    fun findPortAt(position: Offset): PortHit? {
        for (gate in gates) {
            gate.inputPortPositions().forEachIndexed { idx, portPos ->
                if (distance(position, portPos) < 12f) {
                    return PortHit(gate.id, idx, true, portPos)
                }
            }
            gate.outputPortPositions().forEachIndexed { idx, portPos ->
                if (distance(position, portPos) < 12f) {
                    return PortHit(gate.id, idx, false, portPos)
                }
            }
        }
        return null
    }

    fun findGate(id: Int): Gate? = gates.find { it.id == id }
}