package com.example.digisim.UiUtils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.digisim.Persistance.persistanceRepository.defaultDataDir
import kotlinx.serialization.Serializable


import com.example.digisim.Persistance.AppStateDto

class AppState: ViewModel(
) {

        var currentLanguage by mutableStateOf(Language.ENGLISH)
        var showSettings by mutableStateOf(false)

        fun applyDto(dto: AppStateDto) {
            this.currentLanguage = dto.currentLanguage
        }
}