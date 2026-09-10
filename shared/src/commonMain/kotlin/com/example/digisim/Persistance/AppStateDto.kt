package com.example.digisim.Persistance

import com.example.digisim.UiUtils.AppState
import com.example.digisim.UiUtils.Language
import com.example.digisim.UiUtils.LibrariesDto
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.serialization.Serializable

@Serializable
class AppStateDto(
    val currentLanguage : Language,
    val librariesDto: MutableList<LibrariesDto>
)
internal fun AppStateToDto(appState: AppState): AppStateDto{
    return AppStateDto(
        currentLanguage = appState.currentLanguage,
        librariesDto = appState.libraries
    )
}