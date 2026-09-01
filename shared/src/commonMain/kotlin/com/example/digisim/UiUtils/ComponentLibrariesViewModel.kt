package com.example.digisim.UiUtils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import com.example.digisim.ParsingLogic.ComponentType

class ComponentLibrariesViewModel: ViewModel() {
    var currentLibraryIndex by mutableStateOf(0)
    val gates = listOf(
        LibraryComponent("NOT Gate", "add NOT logic gate", null, ComponentType.NOT),
        LibraryComponent("AND Gate", "add AND logic gate", null, ComponentType.AND),
        LibraryComponent("OR Gate", "add OR logic gate", null, ComponentType.OR),
        LibraryComponent("NAND Gate", "add NAND logic gate", null, ComponentType.NAND),
        LibraryComponent("NOR Gate", "add NOR logic gate", null, ComponentType.NOR),
        LibraryComponent("XOR Gate", "add XOR logic gate", null, ComponentType.XOR),
        LibraryComponent("XNOR Gate", "add XNOR logic gate", null, ComponentType.XNOR),
    )
    val wiring = listOf(
        LibraryComponent("Input pin", "add Input pin", null, ComponentType.INPUT),
        LibraryComponent("Button", "add Push Button (1-tick pulse)", null, ComponentType.BUTTON),
        LibraryComponent("Output pin", "add Output pin", null, ComponentType.OUTPUT),
        LibraryComponent("Clock", "add Clock", null, ComponentType.CLOCK)
    )
    val flipFlops = listOf(
        LibraryComponent("RS Flip-Flop", "add RS Flip-Flop", null, ComponentType.RS_FLIP_FLOP),
        LibraryComponent("JK Flip-Flop", "add JK Flip-Flop", null, ComponentType.JK_FLIP_FLOP),
        LibraryComponent("D Flip-Flop", "add D Flip-Flop", null, ComponentType.D_FLIP_FLOP),
        LibraryComponent("T Flip-Flop", "add T Flip-Flop", null, ComponentType.T_FLIP_FLOP)
    )
    val libraries = listOf(
        Libraries("Gates", gates),
        Libraries("Wiring", wiring),
        Libraries("Flip-Flops", flipFlops)
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
}
  class Libraries(
    val name : String,
    val components : List<LibraryComponent>
)
class LibraryComponent(
    val name: String,
    val description : String ,
    val icon : ImageVector? ,
    val type : ComponentType
)