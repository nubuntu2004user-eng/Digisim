package com.example.digisim.Persistance

import com.example.digisim.ParsingLogic.ComponentType
import kotlinx.serialization.Serializable
import logicGates.SimulationComponent

@Serializable
data class ComponentDto(
    val id: Int,
    val type: ComponentType,
    val x: Float,
    val y: Float,
    val inputCount: Int,
    val outputCount: Int,
    val label: String,
    val customLogic: MutableList<MutableList<SimulationComponent>>?,
    val customName: String?,
    val rotationSteps: Int = 0
)