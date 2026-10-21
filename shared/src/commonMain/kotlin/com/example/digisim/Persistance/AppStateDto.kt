package com.example.digisim.Persistance

import com.example.digisim.UiUtils.AppState
import com.example.digisim.UiUtils.Language
import com.example.digisim.UiUtils.LibrariesDto
import kotlinx.serialization.Serializable

@Serializable
data class AppStateDto(
    val currentLanguage: Language = Language.ENGLISH,
    val librariesDto: MutableList<LibrariesDto> = mutableListOf(),
    val isUsingLocalModel: Boolean = true,
    val apiKey: String = "",
    val baseUrl: String = "http://127.0.0.1:9931",
    val modelList: MutableList<String> = mutableListOf(),
    val currentModel: String = ""
)

internal fun AppStateToDto(appState: AppState): AppStateDto {
    return AppStateDto(
        currentLanguage = appState.currentLanguage,
        librariesDto = appState.libraries,
        isUsingLocalModel = appState.isUsingLocalModel,
        apiKey = appState.apiKey,
        baseUrl = appState.baseUrl,
        modelList = appState.modelList,
        currentModel = appState.currentModel
    )
}
