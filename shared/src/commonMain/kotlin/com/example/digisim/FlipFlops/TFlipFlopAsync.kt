package com.example.digisim.FlipFlops

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import com.example.digisim.ComponentsDrawing.drawTFlipFlopAsync
import com.example.digisim.ParsingLogic.Component
import com.example.digisim.ParsingLogic.ComponentType
import com.example.digisim.SettingsViewModel
import logicGates.SimulationComponent

class TFlipFlopAsync(
    id: Int,
    initialX: Float = 0f,
    initialY: Float = 0f,
    initialInputCount: Int = 4,
    initialOutputCount: Int = 2,
    override val abstractedLogic: MutableList<MutableList<SimulationComponent>>? = null,
    override val customComponentName: String? = null
) : Component(id) {

    override var x: Float by mutableStateOf(initialX)
    override var y: Float by mutableStateOf(initialY)
    override var width: Float by mutableStateOf(100f)
    override var height: Float by mutableStateOf(100f)
    override var inputCount: Int by mutableStateOf(initialInputCount)
    override var outputCount: Int by mutableStateOf(initialOutputCount)
    override var delay: Float? = null

    override val componentType = ComponentType.T_FLIP_FLOP_ASYNC

    override fun findPortOffset() = Offset(0f, 0f)

    override fun inputPortPositions(): List<Offset> {
        val spacing = height / (inputCount + 1)
        return (1..inputCount).map { i -> Offset(x, y + spacing * i) }
    }

    override fun outputPortPositions(): List<Offset> {
        val spacing = height / (outputCount + 1)
        return (1..outputCount).map { i -> Offset(x + width, y + spacing * i) }
    }

    fun draw(drawScope: DrawScope, textMeasurer: TextMeasurer, settings: SettingsViewModel = SettingsViewModel.default) {
        with(drawScope) {
            drawTFlipFlopAsync(this@TFlipFlopAsync, textMeasurer, settings)
        }
    }
}
