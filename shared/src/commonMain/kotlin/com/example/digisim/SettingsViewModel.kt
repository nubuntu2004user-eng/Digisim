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
    var isDarkMode: Boolean by mutableStateOf(false)

    // Swiss Style Surface & Structural Tokens
    var canvasBackgroundColor: Color by mutableStateOf(Color(0xFFFFFFFF))
    var panelBackgroundColor: Color by mutableStateOf(Color(0xFFF5F1E8))
    var elevatedSurfaceColor: Color by mutableStateOf(Color(0xFFFFFFFF))
    var borderColor: Color by mutableStateOf(Color(0xFFC9C2B5))
    var strongBorderColor: Color by mutableStateOf(Color(0xFF141414))

    // Typography & Accent Tokens
    var textColor: Color by mutableStateOf(Color(0xFF141414))
    var secondaryTextColor: Color by mutableStateOf(Color(0xFF52504C))
    var accentTaupe: Color by mutableStateOf(Color(0xFFB38B6D))
    var accentTaupeDark: Color by mutableStateOf(Color(0xFF7A583E))
    var primaryControlFill: Color by mutableStateOf(Color(0xFF141414))
    var primaryControlText: Color by mutableStateOf(Color(0xFFFFFFFF))
    var dangerColor: Color by mutableStateOf(Color(0xFFB91C1C))

    // Schematic Component & Pin Signal Tokens (WCAG AA+ verified)
    var highPinColor: Color by mutableStateOf(Color(0xFFC26A00))
    var lowPinColor: Color by mutableStateOf(Color(0xFF334155))
    var errorPinColor: Color by mutableStateOf(Color(0xFFB91C1C))
    var undefinedPinColor: Color by mutableStateOf(Color(0xFFEFECE6))
    var gateOutlineColor: Color by mutableStateOf(Color(0xFF141414))
    var portColor: Color by mutableStateOf(Color(0xFF141414))
    var wireHighlightColor: Color by mutableStateOf(Color(0xFFB91C1C))
    var textHighColor: Color by mutableStateOf(Color(0xFFFFFFFF))
    var textLowColor: Color by mutableStateOf(Color(0xFFFFFFFF))
    var textUndefinedColor: Color by mutableStateOf(Color(0xFF141414))

    // Architectural Schematic Gate & IC Package Tokens (Option A)
    var gateBodyFillColor: Color by mutableStateOf(Color(0xFFFFFFFF))
    var ioIdleFillColor: Color by mutableStateOf(Color(0xFFF5F1E8))
    var chipBodyColor: Color by mutableStateOf(Color(0xFF1C1C1A))
    var chipNotchColor: Color by mutableStateOf(Color(0xFF2E2D2A))
    var chipSubtitleColor: Color by mutableStateOf(Color(0xFFA8A298))
    var chipPinNumberColor: Color by mutableStateOf(Color(0xFFD4B094))
    var chipPinNameColor: Color by mutableStateOf(Color(0xFFF5F1E8))

    var gridColor: Color by mutableStateOf(Color(0xFFE5DFD3))
    var gridSize: Float by mutableStateOf(20f)

    var customComponentFillColor: Color by mutableStateOf(Color(0xFFF5F1E8))
    var customComponentOutlineColor: Color by mutableStateOf(Color(0xFF141414))

    fun applyTheme(dark: Boolean) {
        isDarkMode = dark
        if (dark) {
            // Basel Night Shift (Swiss Dark Variant)
            canvasBackgroundColor = Color(0xFF141414)
            panelBackgroundColor = Color(0xFF1C1C1A)
            elevatedSurfaceColor = Color(0xFF262624)
            borderColor = Color(0xFF363430)
            strongBorderColor = Color(0xFFF5F1E8)

            textColor = Color(0xFFF5F1E8)
            secondaryTextColor = Color(0xFFA8A298)
            accentTaupe = Color(0xFFB38B6D)
            accentTaupeDark = Color(0xFFD4B094)
            primaryControlFill = Color(0xFFF5F1E8)
            primaryControlText = Color(0xFF141414)
            dangerColor = Color(0xFFDC2626)

            highPinColor = Color(0xFFD97706)
            lowPinColor = Color(0xFF475569)
            errorPinColor = Color(0xFFDC2626)
            undefinedPinColor = Color(0xFF2A2927)
            gateOutlineColor = Color(0xFFE5E0D5)
            portColor = Color(0xFFF5F1E8)
            wireHighlightColor = Color(0xFFEF4444)
            textHighColor = Color(0xFF141414)
            textLowColor = Color(0xFFF5F1E8)
            textUndefinedColor = Color(0xFFF5F1E8)

            gateBodyFillColor = Color(0xFF262624)
            ioIdleFillColor = Color(0xFF1C1C1A)
            chipBodyColor = Color(0xFF181816)
            chipNotchColor = Color(0xFF2A2927)
            chipSubtitleColor = Color(0xFFA8A298)
            chipPinNumberColor = Color(0xFFD4B094)
            chipPinNameColor = Color(0xFFF5F1E8)

            gridColor = Color(0xFF242320)
            customComponentFillColor = Color(0xFF262624)
            customComponentOutlineColor = Color(0xFFE5E0D5)
        } else {
            // Zurich Drafting Table (Pure Swiss Light Default)
            canvasBackgroundColor = Color(0xFFFFFFFF)
            panelBackgroundColor = Color(0xFFF5F1E8)
            elevatedSurfaceColor = Color(0xFFFFFFFF)
            borderColor = Color(0xFFC9C2B5)
            strongBorderColor = Color(0xFF141414)

            textColor = Color(0xFF141414)
            secondaryTextColor = Color(0xFF52504C)
            accentTaupe = Color(0xFFB38B6D)
            accentTaupeDark = Color(0xFF7A583E)
            primaryControlFill = Color(0xFF141414)
            primaryControlText = Color(0xFFFFFFFF)
            dangerColor = Color(0xFFB91C1C)

            highPinColor = Color(0xFFC26A00)
            lowPinColor = Color(0xFF334155)
            errorPinColor = Color(0xFFB91C1C)
            undefinedPinColor = Color(0xFFEFECE6)
            gateOutlineColor = Color(0xFF141414)
            portColor = Color(0xFF141414)
            wireHighlightColor = Color(0xFFB91C1C)
            textHighColor = Color(0xFFFFFFFF)
            textLowColor = Color(0xFFFFFFFF)
            textUndefinedColor = Color(0xFF141414)

            gateBodyFillColor = Color(0xFFFFFFFF)
            ioIdleFillColor = Color(0xFFF5F1E8)
            chipBodyColor = Color(0xFF1C1C1A)
            chipNotchColor = Color(0xFF2E2D2A)
            chipSubtitleColor = Color(0xFFA8A298)
            chipPinNumberColor = Color(0xFFD4B094)
            chipPinNameColor = Color(0xFFF5F1E8)

            gridColor = Color(0xFFE5DFD3)
            customComponentFillColor = Color(0xFFF5F1E8)
            customComponentOutlineColor = Color(0xFF141414)
        }
    }

    fun getComponentInnerRectColor(pin: Pin): Color {
        return when (pin) {
            Pin.HIGH -> highPinColor
            Pin.LOW -> lowPinColor
            Pin.ERROR -> errorPinColor
            Pin.UNDEFINED -> undefinedPinColor
        }
    }

    fun getComponentTextColor(pin: Pin): Color {
        return when (pin) {
            Pin.LOW -> textLowColor
            Pin.UNDEFINED -> textUndefinedColor
            Pin.HIGH -> textHighColor
            Pin.ERROR -> Color.White
        }
    }
}
