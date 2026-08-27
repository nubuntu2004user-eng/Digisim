package com.example.digisim

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import logicGates.Pin

class SettingsViewModel(drawText: Boolean = true) : ViewModel() {
    companion object {
        var default = SettingsViewModel(true)
    }

    var drawText: Boolean by mutableStateOf(drawText)

    var highPinColor: Color by mutableStateOf(Color(0xFF2E7D32))
    var lowPinColor: Color by mutableStateOf(Color(0xFF81C784))
    var errorPinColor: Color by mutableStateOf(Color(0xFFE57373))
    var undefinedPinColor: Color by mutableStateOf(Color(0xFFC8E6C9))
    var gateOutlineColor: Color by mutableStateOf(Color(0xFF4CAF50))
    var portColor: Color by mutableStateOf(Color.Black)
    var wireHighlightColor: Color by mutableStateOf(Color.Red)
    var textHighColor: Color by mutableStateOf(Color.Black)
    var textLowColor: Color by mutableStateOf(Color.White)

    fun getComponentInnerRectColor(pin: Pin): Color {
        return when (pin) {
            Pin.HIGH -> highPinColor
            Pin.LOW -> lowPinColor
            Pin.ERROR -> errorPinColor
            Pin.UNDEFINED -> undefinedPinColor
        }
    }

    fun getComponentTextColor(pin: Pin): Color {
        return if (pin == Pin.LOW) textLowColor else textHighColor
    }
}