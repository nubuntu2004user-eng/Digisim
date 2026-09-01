package com.example.digisim.UiUtils

import com.example.digisim.DrawingLogic.CanvasViewModel

fun undoOnce(viewModel: CanvasViewModel){
    val foundComponent = viewModel.components.find { it.ID == viewModel.nextId - 1 }
    if (foundComponent == null){
        val foundWire = viewModel.wires.find { it.id == viewModel.nextId - 1 }
        if (foundWire !== null){
            viewModel.wires.remove(foundWire)
        }
    }else{
        viewModel.components.remove(foundComponent)
    }
    viewModel.nextId --
    if (viewModel.nextId < 0 ) viewModel.nextId = 0
}