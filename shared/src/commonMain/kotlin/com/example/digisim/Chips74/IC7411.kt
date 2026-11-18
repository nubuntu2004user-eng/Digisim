package com.example.digisim.Chips74

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import com.example.digisim.ComponentsDrawing.drawIC7411
import com.example.digisim.ParsingLogic.Component
import com.example.digisim.ParsingLogic.ComponentType
import com.example.digisim.SettingsViewModel
import logicGates.SimulationComponent

class IC7411(
    id: Int,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialInputCount: Int = 11,
    initialOutputCount: Int = 3,
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

    override val componentType = ComponentType.IC_7411

    override fun findPortOffset() = Offset(0f, 0f)

    override fun inputPortPositions(): List<Offset> = listOf(
        Offset(x, y + 20f),          // 0: Pin 1 (1A)
        Offset(x, y + 40f),          // 1: Pin 2 (1B)
        Offset(x, y + 60f),          // 2: Pin 3 (2A)
        Offset(x, y + 80f),          // 3: Pin 4 (2B)
        Offset(x, y + 100f),         // 4: Pin 5 (2C)
        Offset(x, y + 140f),         // 5: Pin 7 (GND)
        Offset(x + width, y + 120f), // 6: Pin 9 (3A)
        Offset(x + width, y + 100f), // 7: Pin 10 (3B)
        Offset(x + width, y + 80f),  // 8: Pin 11 (3C)
        Offset(x + width, y + 40f),  // 9: Pin 13 (1C)
        Offset(x + width, y + 20f)   // 10: Pin 14 (VCC)
    )

    override fun outputPortPositions(): List<Offset> = listOf(
        Offset(x + width, y + 60f),  // 0: Pin 12 (1Y)
        Offset(x, y + 120f),         // 1: Pin 6 (2Y)
        Offset(x + width, y + 140f)  // 2: Pin 8 (3Y)
    )

    fun draw(drawScope: DrawScope, textMeasurer: TextMeasurer, settings: SettingsViewModel = SettingsViewModel.default) {
        with(drawScope) {
            drawIC7411(this@IC7411, textMeasurer, settings)
        }
    }
}
