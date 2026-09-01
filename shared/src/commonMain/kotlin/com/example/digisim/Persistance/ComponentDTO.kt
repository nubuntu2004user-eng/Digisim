package com.example.digisim.Persistance

import com.example.digisim.ParsingLogic.ComponentType
import kotlinx.serialization.Serializable

@Serializable
data class ComponentDto(
    val id: Int,
    val type: ComponentType,
    val x: Float,
    val y: Float,
    val inputCount: Int,
    val outputCount: Int
)