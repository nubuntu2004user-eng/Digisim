package com.example.digisim.Chips74

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import com.example.digisim.ComponentsDrawing.drawIC7474
import com.example.digisim.ParsingLogic.Component
import com.example.digisim.ParsingLogic.ComponentType
import com.example.digisim.SettingsViewModel
import logicGates.SimulationComponent

class IC7474(
    id: Int,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialInputCount: Int = 10,
    initialOutputCount: Int = 4,
    label: String = "",
    override val abstractedLogic: MutableList<MutableList<SimulationComponent>>? = null,
    override val customComponentName: String? = null
) : Component(id) {

    override var label: String by mutableStateOf(label)
    override var x: Float by mutableStateOf(initialX)
    override var y: Float by mutableStateOf(initialY)
    override var width: Float by mutableStateOf(120f)
    override var height: Float by mutableStateOf(160f)
    override var inputCount: Int by mutableStateOf(initialInputCount)
    override var outputCount: Int by mutableStateOf(initialOutputCount)
    override var delay: Float? = null

    override val componentType = ComponentType.IC_7474

    override fun findPortOffset() = Offset(0f, 0f)

    override fun inputPortPositions(): List<Offset> = listOf(
        Offset(x, y + 20f),          // 0: Pin 1 (1CLR)
        Offset(x, y + 40f),          // 1: Pin 2 (1D)
        Offset(x, y + 60f),          // 2: Pin 3 (1CLK)
        Offset(x, y + 80f),          // 3: Pin 4 (1PRE)
        Offset(x, y + 140f),         // 4: Pin 7 (GND)
        Offset(x + width, y + 40f),  // 5: Pin 13 (2CLR)
        Offset(x + width, y + 60f),  // 6: Pin 12 (2D)
        Offset(x + width, y + 80f),  // 7: Pin 11 (2CLK)
        Offset(x + width, y + 100f), // 8: Pin 10 (2PRE)
        Offset(x + width, y + 20f)   // 9: Pin 14 (VCC)
    )

    override fun outputPortPositions(): List<Offset> = listOf(
        Offset(x, y + 100f),         // 0: Pin 5 (1Q)
        Offset(x, y + 120f),         // 1: Pin 6 (1~Q)
        Offset(x + width, y + 120f), // 2: Pin 9 (2Q)
        Offset(x + width, y + 140f)  // 3: Pin 8 (2~Q)
    )

    fun draw(drawScope: DrawScope, textMeasurer: TextMeasurer, settings: SettingsViewModel = SettingsViewModel.default) {
        with(drawScope) {
            drawIC7474(this@IC7474, textMeasurer, settings)
        }
    }
}
