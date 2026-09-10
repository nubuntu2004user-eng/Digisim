package com.example.digisim.Chips74

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import com.example.digisim.ComponentsDrawing.drawIC7408
import com.example.digisim.ParsingLogic.Component
import com.example.digisim.ParsingLogic.ComponentType
import com.example.digisim.SettingsViewModel
import logicGates.SimulationComponent

class IC7408(
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

    override val componentType = ComponentType.IC_7408

    override fun findPortOffset() = Offset(0f, 0f)

    override fun inputPortPositions(): List<Offset> = listOf(
        Offset(x, y + 20f),          // Pin 1 (1A)
        Offset(x, y + 40f),          // Pin 2 (1B)
        Offset(x, y + 80f),          // Pin 4 (2A)
        Offset(x, y + 100f),         // Pin 5 (2B)
        Offset(x, y + 140f),         // Pin 7 (GND)
        Offset(x + width, y + 120f), // Pin 9 (3A)
        Offset(x + width, y + 100f), // Pin 10 (3B)
        Offset(x + width, y + 60f),  // Pin 12 (4A)
        Offset(x + width, y + 40f),  // Pin 13 (4B)
        Offset(x + width, y + 20f)   // Pin 14 (VCC)
    )

    override fun outputPortPositions(): List<Offset> = listOf(
        Offset(x, y + 60f),          // Pin 3 (1Y)
        Offset(x, y + 120f),         // Pin 6 (2Y)
        Offset(x + width, y + 140f), // Pin 8 (3Y)
        Offset(x + width, y + 80f)   // Pin 11 (4Y)
    )

    fun draw(drawScope: DrawScope, textMeasurer: TextMeasurer, settings: SettingsViewModel = SettingsViewModel.default) {
        with(drawScope) {
            drawIC7408(this@IC7408, textMeasurer, settings)
        }
    }
}
