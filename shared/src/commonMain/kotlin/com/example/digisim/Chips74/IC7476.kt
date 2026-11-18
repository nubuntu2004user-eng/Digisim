package com.example.digisim.Chips74

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import com.example.digisim.ComponentsDrawing.drawIC7476
import com.example.digisim.ParsingLogic.Component
import com.example.digisim.ParsingLogic.ComponentType
import com.example.digisim.SettingsViewModel
import logicGates.SimulationComponent

class IC7476(
    id: Int,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialInputCount: Int = 12,
    initialOutputCount: Int = 4,
    label: String = "",
    override val abstractedLogic: MutableList<MutableList<SimulationComponent>>? = null,
    override val customComponentName: String? = null
) : Component(id) {

    override var label: String by mutableStateOf(label)
    override var x: Float by mutableStateOf(initialX)
    override var y: Float by mutableStateOf(initialY)
    override var width: Float by mutableStateOf(120f)
    override var height: Float by mutableStateOf(180f)
    override var inputCount: Int by mutableStateOf(initialInputCount)
    override var outputCount: Int by mutableStateOf(initialOutputCount)
    override var delay: Float? = null

    override val componentType = ComponentType.IC_7476

    override fun findPortOffset() = Offset(0f, 0f)

    override fun inputPortPositions(): List<Offset> = listOf(
        Offset(x, y + 20f),          // 0: Pin 1 (1PRE)
        Offset(x, y + 40f),          // 1: Pin 2 (1CLR)
        Offset(x, y + 60f),          // 2: Pin 3 (1J)
        Offset(x, y + 80f),          // 3: Pin 4 (1CLK)
        Offset(x, y + 100f),         // 4: Pin 5 (1K)
        Offset(x, y + 160f),         // 5: Pin 8 (GND)
        Offset(x + width, y + 40f),  // 6: Pin 15 (2PRE)
        Offset(x + width, y + 60f),  // 7: Pin 14 (2CLR)
        Offset(x + width, y + 80f),  // 8: Pin 13 (2J)
        Offset(x + width, y + 100f), // 9: Pin 12 (2CLK)
        Offset(x + width, y + 120f), // 10: Pin 11 (2K)
        Offset(x + width, y + 20f)   // 11: Pin 16 (VCC)
    )

    override fun outputPortPositions(): List<Offset> = listOf(
        Offset(x, y + 120f),         // 0: Pin 6 (1Q)
        Offset(x, y + 140f),         // 1: Pin 7 (1~Q)
        Offset(x + width, y + 140f), // 2: Pin 10 (2Q)
        Offset(x + width, y + 160f)  // 3: Pin 9 (2~Q)
    )

    fun draw(drawScope: DrawScope, textMeasurer: TextMeasurer, settings: SettingsViewModel = SettingsViewModel.default) {
        with(drawScope) {
            drawIC7476(this@IC7476, textMeasurer, settings)
        }
    }
}
