package com.example.digisim.UiUtils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import com.example.digisim.ParsingLogic.ComponentType
import logicGates.SimulationComponent

class ComponentLibrariesViewModel: ViewModel() {
    var currentLibraryIndex by mutableStateOf(0)
    val gates = mutableStateListOf(
        LibraryComponent("NOT Gate", "add NOT logic gate", null, ComponentType.NOT),
        LibraryComponent("AND Gate", "add AND logic gate", null, ComponentType.AND),
        LibraryComponent("OR Gate", "add OR logic gate", null, ComponentType.OR),
        LibraryComponent("NAND Gate", "add NAND logic gate", null, ComponentType.NAND),
        LibraryComponent("NOR Gate", "add NOR logic gate", null, ComponentType.NOR),
        LibraryComponent("XOR Gate", "add XOR logic gate", null, ComponentType.XOR),
        LibraryComponent("XNOR Gate", "add XNOR logic gate", null, ComponentType.XNOR),
    )
    val wiring = mutableStateListOf(
        LibraryComponent("Input pin", "add Input pin", null, ComponentType.INPUT),
        LibraryComponent("Button", "add Push Button (1-tick pulse)", null, ComponentType.BUTTON),
        LibraryComponent("Output pin", "add Output pin", null, ComponentType.OUTPUT),
        LibraryComponent("Clock", "add Clock", null, ComponentType.CLOCK)
    )
    val flipFlops = mutableStateListOf(
        LibraryComponent("RS Flip-Flop", "add RS Flip-Flop", null, ComponentType.RS_FLIP_FLOP),
        LibraryComponent("JK Flip-Flop", "add JK Flip-Flop", null, ComponentType.JK_FLIP_FLOP),
        LibraryComponent("D Flip-Flop", "add D Flip-Flop", null, ComponentType.D_FLIP_FLOP),
        LibraryComponent("T Flip-Flop", "add T Flip-Flop", null, ComponentType.T_FLIP_FLOP),
        LibraryComponent("JK Flip-Flop (Async)", "add Asynchronous JK Flip-Flop (with Set/Reset)", null, ComponentType.JK_FLIP_FLOP_ASYNC),
        LibraryComponent("SR Flip-Flop (Async)", "add Asynchronous SR Flip-Flop (with Set/Reset)", null, ComponentType.SR_FLIP_FLOP_ASYNC),
        LibraryComponent("D Flip-Flop (Async)", "add Asynchronous D Flip-Flop (with Set/Reset)", null, ComponentType.D_FLIP_FLOP_ASYNC),
        LibraryComponent("T Flip-Flop (Async)", "add Asynchronous T Flip-Flop (with Set/Reset)", null, ComponentType.T_FLIP_FLOP_ASYNC)
    )
    val chips74 = mutableStateListOf(
        LibraryComponent("7400", "add 7400 Quad 2-input NAND Gate IC", null, ComponentType.IC_7400),
        LibraryComponent("7402", "add 7402 Quad 2-input NOR Gate IC", null, ComponentType.IC_7402),
        LibraryComponent("7404", "add 7404 Hex Inverter IC", null, ComponentType.IC_7404),
        LibraryComponent("7408", "add 7408 Quad 2-input AND Gate IC", null, ComponentType.IC_7408),
        LibraryComponent("7432", "add 7432 Quad 2-input OR Gate IC", null, ComponentType.IC_7432),
        LibraryComponent("7486", "add 7486 Quad 2-input XOR Gate IC", null, ComponentType.IC_7486)
    )
    val libraries = mutableStateListOf(
        Libraries("Gates", gates , false),
        Libraries("Wiring", wiring , false),
        Libraries("Flip-Flops", flipFlops , false),
        Libraries("74**", chips74 , false),
    )

     fun nextLib() {
        if ((currentLibraryIndex + 1) < libraries.size) {
            currentLibraryIndex++
        }
         else currentLibraryIndex = 0
    }

     fun previousLib() {
        if ((currentLibraryIndex - 1) >= 0) {
            currentLibraryIndex--
        }
         else currentLibraryIndex = libraries.lastIndex
    }

    fun addCustomComponent(component: LibraryComponent , selectedLibrary : String) {
        val customLibrary = libraries.find { it.name == selectedLibrary }
        (customLibrary?.components as? MutableList<LibraryComponent>)?.add(component)
    }
}
  class Libraries(
      val name : String,
      val components : SnapshotStateList<LibraryComponent>,
      val isMutable : Boolean
  )

class LibraryComponent(
    val name: String,
    val description : String ,
    val icon : ImageVector? ,
    val type : ComponentType,
    val abstractedLogic: MutableList<MutableList<SimulationComponent>>? = null,
    val inputCount: Int = 0,
    val outputCount: Int = 0
)