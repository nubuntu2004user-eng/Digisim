package com.example.digisim.UiUtils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.digisim.LibraryLoader.addCustomComponent
import com.example.digisim.LibraryLoader.parseComponent
import com.example.digisim.Persistance.persistanceRepository.defaultDataDir
import kotlinx.serialization.Serializable


import com.example.digisim.Persistance.AppStateDto
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AppState: ViewModel(
) {

        var currentLanguage by mutableStateOf(Language.ENGLISH)
        var showSettings by mutableStateOf(false)

        var agenticMode by mutableStateOf(false)

        var libraries = mutableListOf<LibrariesDto>()

        var apiKey by mutableStateOf("")

        var isUsingLocalModel by mutableStateOf(true)

        var baseUrl by mutableStateOf("http://127.0.0.1:9931")

        var modelList = mutableListOf<String>()

        var currentModel by mutableStateOf("")

        var openAiSettings by mutableStateOf(true)
        fun applyDto(dto: AppStateDto) {
            this.currentLanguage = dto.currentLanguage
            this.libraries = dto.librariesDto
            this.isUsingLocalModel = dto.isUsingLocalModel
            this.apiKey = dto.apiKey
            this.baseUrl = dto.baseUrl
            this.modelList = dto.modelList
            this.currentModel = dto.currentModel
        }
        fun loadLibraries(librariesViewModel: ComponentLibrariesViewModel , scope: CoroutineScope , appState: AppState){
                scope.launch {
                for (i in libraries){
                        for (component in i.contents){
                                parseComponent(component , appState = appState , libraryViewModel = librariesViewModel , library = i.name)
                                }
                        }
                }
        }
}
@Serializable
data class LibrariesDto(
        val name : String,
        val contents : MutableList<PlatformFile>
)