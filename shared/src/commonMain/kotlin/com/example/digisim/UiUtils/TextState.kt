package com.example.digisim.UiUtils

import androidx.compose.ui.text.font.FontFamily

 class TextState {
    var language: chosenLanguage = chosenLanguage.English
    var font: FontFamily = FontFamily.Monospace
}
enum class chosenLanguage { English , Deutsch , Ukrainian}
