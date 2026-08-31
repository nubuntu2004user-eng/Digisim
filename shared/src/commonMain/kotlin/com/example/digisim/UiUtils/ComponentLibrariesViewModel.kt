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
        LibraryComponent("AND Gate", "add AND logic gate", null, ComponentType.AND),
        LibraryComponent("OR Gate", "add OR logic gate", null, ComponentType.OR),
        LibraryComponent("NAND Gate", "add NAND logic gate", null, ComponentType.NAND),
        LibraryComponent("NOR Gate", "add NOR logic gate", null, ComponentType.NOR),
        LibraryComponent("XOR Gate", "add XOR logic gate", null, ComponentType.XOR),
        LibraryComponent("XNOR Gate", "add XNOR logic gate", null, ComponentType.XNOR),
    )
    val wiring = listOf(
        LibraryComponent("Input pin", "add Input pin", null, ComponentType.INPUT),
        LibraryComponent("Output pin", "add Output pin", null, ComponentType.OUTPUT),
        LibraryComponent("Clock" , "add Clock" , null , ComponentType.CLOCK)
    )
    val libraries = listOf(
        Libraries("Gates", gates),
        Libraries("Wiring", wiring)
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