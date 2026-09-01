package com.example.digisim.Persistance

import com.example.digisim.DrawingLogic.CanvasViewModel
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.dialogs.openFileSaver

suspend fun loadWrapper(viewModel: CanvasViewModel){
    val file = FileKit.openFilePicker()
    loadFile(viewModel , file)
}

internal suspend fun saveFileAs(viewModel: CanvasViewModel) {
    val file = FileKit.openFileSaver(
        suggestedName = "circuit1",
        extension = "json"
    )
    file ?: return
val componentDto = mutableListOf<ComponentDto>()
    viewModel.components.forEach { component ->
        componentDto += component.toDto()
    }
    persistanceRepository.saveAs(
        file = file,
        components = componentDto,
        wires = viewModel.wires
    )
}