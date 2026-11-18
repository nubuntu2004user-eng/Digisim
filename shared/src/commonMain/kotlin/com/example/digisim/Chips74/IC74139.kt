package com.example.digisim.Chips74

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import com.example.digisim.ComponentsDrawing.drawIC74139
import com.example.digisim.ParsingLogic.Component
import com.example.digisim.ParsingLogic.ComponentType
import com.example.digisim.SettingsViewModel
import logicGates.SimulationComponent

class IC74139(
    id: Int,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialInputCount: Int = 8,
    initialOutputCount: Int = 8,
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

    override val componentType = ComponentType.IC_74139

    override fun findPortOffset() = Offset(0f, 0f)

    override fun inputPortPositions(): List<Offset> = listOf(
        Offset(x, y + 20f),          // 0: Pin 1 (~1G)
        Offset(x, y + 40f),          // 1: Pin 2 (1A)
        Offset(x, y + 60f),          // 2: Pin 3 (1B)
        Offset(x, y + 160f),         // 3: Pin 8 (GND)
        Offset(x + width, y + 40f),  // 4: Pin 15 (~2G)
        Offset(x + width, y + 60f),  // 5: Pin 14 (2A)
        Offset(x + width, y + 80f),  // 6: Pin 13 (2B)
        Offset(x + width, y + 20f)   // 7: Pin 16 (VCC)
    )

    override fun outputPortPositions(): List<Offset> = listOf(
        Offset(x, y + 80f),          // 0: Pin 4 (1Y0)
        Offset(x, y + 100f),         // 1: Pin 5 (1Y1)
        Offset(x, y + 120f),         // 2: Pin 6 (1Y2)
        Offset(x, y + 140f),         // 3: Pin 7 (1Y3)
        Offset(x + width, y + 100f), // 4: Pin 12 (2Y0)
        Offset(x + width, y + 120f), // 5: Pin 11 (2Y1)
        Offset(x + width, y + 140f), // 6: Pin 10 (2Y2)
        Offset(x + width, y + 160f)  // 7: Pin 9 (2Y3)
    )

    fun draw(drawScope: DrawScope, textMeasurer: TextMeasurer, settings: SettingsViewModel = SettingsViewModel.default) {
        with(drawScope) {
            drawIC74139(this@IC74139, textMeasurer, settings)
        }
    }
}
