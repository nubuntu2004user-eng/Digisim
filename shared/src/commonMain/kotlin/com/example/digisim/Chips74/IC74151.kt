package com.example.digisim.Chips74

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import com.example.digisim.ComponentsDrawing.drawIC74151
import com.example.digisim.ParsingLogic.Component
import com.example.digisim.ParsingLogic.ComponentType
import com.example.digisim.SettingsViewModel
import logicGates.SimulationComponent

class IC74151(
    id: Int,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialInputCount: Int = 14,
    initialOutputCount: Int = 2,
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

    override val componentType = ComponentType.IC_74151

    override fun findPortOffset() = Offset(0f, 0f)

    override fun inputPortPositions(): List<Offset> = listOf(
        Offset(x, y + 80f),          // 0: Pin 4 (D0)
        Offset(x, y + 60f),          // 1: Pin 3 (D1)
        Offset(x, y + 40f),          // 2: Pin 2 (D2)
        Offset(x, y + 20f),          // 3: Pin 1 (D3)
        Offset(x + width, y + 40f),  // 4: Pin 15 (D4)
        Offset(x + width, y + 60f),  // 5: Pin 14 (D5)
        Offset(x + width, y + 80f),  // 6: Pin 13 (D6)
        Offset(x + width, y + 100f), // 7: Pin 12 (D7)
        Offset(x + width, y + 120f), // 8: Pin 11 (A)
        Offset(x + width, y + 140f), // 9: Pin 10 (B)
        Offset(x + width, y + 160f), // 10: Pin 9 (C)
        Offset(x, y + 140f),         // 11: Pin 7 (~G)
        Offset(x, y + 160f),         // 12: Pin 8 (GND)
        Offset(x + width, y + 20f)   // 13: Pin 16 (VCC)
    )

    override fun outputPortPositions(): List<Offset> = listOf(
        Offset(x, y + 100f),         // 0: Pin 5 (Y)
        Offset(x, y + 120f)          // 1: Pin 6 (W)
    )

    fun draw(drawScope: DrawScope, textMeasurer: TextMeasurer, settings: SettingsViewModel = SettingsViewModel.default) {
        with(drawScope) {
            drawIC74151(this@IC74151, textMeasurer, settings)
        }
    }
}
