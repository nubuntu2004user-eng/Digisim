package com.example.digisim.Chips74

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import com.example.digisim.ComponentsDrawing.drawIC74283
import com.example.digisim.ParsingLogic.Component
import com.example.digisim.ParsingLogic.ComponentType
import com.example.digisim.SettingsViewModel
import logicGates.SimulationComponent

class IC74283(
    id: Int,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialInputCount: Int = 11,
    initialOutputCount: Int = 5,
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

    override val componentType = ComponentType.IC_74283

    override fun findPortOffset() = Offset(0f, 0f)

    override fun inputPortPositions(): List<Offset> = listOf(
        Offset(x, y + 20f),          // 0: Pin 1 (A1)
        Offset(x, y + 40f),          // 1: Pin 2 (B1)
        Offset(x, y + 60f),          // 2: Pin 3 (A2)
        Offset(x, y + 80f),          // 3: Pin 4 (B2)
        Offset(x, y + 100f),         // 4: Pin 5 (A3)
        Offset(x, y + 120f),         // 5: Pin 6 (B3)
        Offset(x + width, y + 40f),  // 6: Pin 15 (A4)
        Offset(x + width, y + 60f),  // 7: Pin 14 (B4)
        Offset(x, y + 140f),         // 8: Pin 7 (C0)
        Offset(x, y + 160f),         // 9: Pin 8 (GND)
        Offset(x + width, y + 20f)   // 10: Pin 16 (VCC)
    )

    override fun outputPortPositions(): List<Offset> = listOf(
        Offset(x + width, y + 80f),  // 0: Pin 13 (S1)
        Offset(x + width, y + 100f), // 1: Pin 12 (S2)
        Offset(x + width, y + 120f), // 2: Pin 11 (S3)
        Offset(x + width, y + 140f), // 3: Pin 10 (S4)
        Offset(x + width, y + 160f)  // 4: Pin 9 (C4)
    )

    fun draw(drawScope: DrawScope, textMeasurer: TextMeasurer, settings: SettingsViewModel = SettingsViewModel.default) {
        with(drawScope) {
            drawIC74283(this@IC74283, textMeasurer, settings)
        }
    }
}
