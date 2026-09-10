package com.example.digisim.LibraryLoader

import com.example.digisim.ParsingLogic.ComponentType
import com.example.digisim.Persistance.persistanceRepository
import com.example.digisim.Persistance.toComponent
import com.example.digisim.SimulationHandling.convertCircuit
import com.example.digisim.UiUtils.AppState
import com.example.digisim.UiUtils.ComponentLibrariesViewModel
import com.example.digisim.UiUtils.LibrariesDto
import com.example.digisim.UiUtils.LibraryComponent
import engineLogic.ClockManager
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.name

suspend fun addCustomComponent(libraryViewModel: ComponentLibrariesViewModel , library : String , appState: AppState){
    val file = pickFile()
    if (file != null) {
        parseComponent(file = file , appState = appState , libraryViewModel = libraryViewModel , library = library)
    }
}

private suspend fun pickFile(): PlatformFile?{
    val file = FileKit.openFilePicker()
    return file
}

internal suspend fun parseComponent(file : PlatformFile , appState: AppState , libraryViewModel: ComponentLibrariesViewModel , library: String){
    val data = persistanceRepository.load(file)
    if (data != null) {

        val componentName = file.name.substringBeforeLast(".")

        // Input components as per SimulationEngine
        val inputTypes = listOf(
            ComponentType.INPUT,
            ComponentType.BUTTON,
            ComponentType.CLOCK
        )
        val inputCount = data.components.count { it.type in inputTypes }
        val outputCount = data.components.count { it.type == ComponentType.OUTPUT }

        // Map loaded components to abstracted logic
        val clockManager = ClockManager()
        val componentsList = data.components.map { it.toComponent() }
        val stages = mutableListOf(componentsList.toMutableList())
        val abstractedLogic = convertCircuit(stages, data.wires, clockManager)
        var alreadyExists = false
        if (appState.libraries.any { it.name == library }) {
            appState.libraries.forEach { library ->
                if (library.contents.any { it == file } ) {
                    alreadyExists = true
                }
            }
            if(!alreadyExists){
                appState.libraries.find { it.name == library }?.let { it.contents.add(file) }
            }
        }
        else{
            appState.libraries.add(LibrariesDto(library , mutableListOf(file)))
        }
        libraryViewModel.addCustomComponent(
            LibraryComponent(
                name = componentName,
                description = "Custom component: $componentName",
                icon = null,
                type = ComponentType.CUSTOM,
                abstractedLogic = abstractedLogic,
                inputCount = inputCount,
                outputCount = outputCount
            ),
            selectedLibrary = library,
        )
    }
}