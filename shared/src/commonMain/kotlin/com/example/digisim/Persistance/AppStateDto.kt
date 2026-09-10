package com.example.digisim.Persistance

import com.example.digisim.UiUtils.AppState
import com.example.digisim.UiUtils.Language
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.serialization.Serializable

@Serializable
class AppStateDto(
    val currentLanguage : Language,
)
internal fun AppStateToDto(appState: AppState): AppStateDto{
    return AppStateDto(
        currentLanguage = appState.currentLanguage,
    )
}