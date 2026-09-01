package com.example.digisim.View

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.digisim.DrawingLogic.createComponent
import com.example.digisim.DrawingLogic.drawComponent
import com.example.digisim.ParsingLogic.Component
import com.example.digisim.ParsingLogic.ComponentType
import com.example.digisim.SettingsViewModel
import com.example.digisim.UiUtils.ComponentLibrariesViewModel
import com.example.digisim.UiUtils.TranslationViewModel

@Composable
fun componentLibraries(viewModel : CanvasViewModel , libraryViewModel: ComponentLibrariesViewModel, translationViewModel: TranslationViewModel){
    val settingsViewModel = SettingsViewModel.default
    val selectedLibrary = libraryViewModel.libraries[libraryViewModel.currentLibraryIndex]
    val componentList = selectedLibrary.components
    val currentLibraryName = selectedLibrary.name
    val previewComponents = remember(componentList) {
        componentList.associate { it.type to createComponent(it.type, id = -1, x = 0f, y = 0f) }
    }
    val previewSettings = remember { SettingsViewModel(drawText = false) }
    val textMeasurer = rememberTextMeasurer()
    Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.3f)) {
        Row(modifier = Modifier.fillMaxWidth()){
            Spacer(modifier = Modifier.fillMaxWidth(0.2f))
            IconButton(onClick = {libraryViewModel.previousLib()} , modifier = Modifier){
                Icon(
                    imageVector = Icons.Filled.ChevronLeft,
                    contentDescription = null
                )}
                Text(translationViewModel.getString("Library: ") + translationViewModel.getString(currentLibraryName), color = settingsViewModel.textColor , modifier = Modifier.padding(10.dp))
                IconButton(onClick = {libraryViewModel.nextLib()} , modifier = Modifier){
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = null
                    )

            }
        }

        FlowRow(modifier = Modifier.verticalScroll(rememberScrollState()) ) {
            for ( i in componentList){
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
                    tooltip = {
                        PlainTooltip {
                            Text(translationViewModel.getString(i.description))
                        }
                    },
                    state = rememberTooltipState()
                ){
                ElevatedButton(onClick = {viewModel.addComponent(i.type)} , modifier = Modifier.padding(5.dp)){
                    if (i.icon !== null){
                        Icon(
                            imageVector = i.icon,
                            contentDescription = null
                        )
                    }else{
                        val preview = previewComponents[i.type]
                            if (preview != null) {
                                Canvas(modifier = Modifier.size(36.dp, 28.dp)) {
                                    val totalW = preview.width + 16f
                                    val totalH = preview.height + 16f
                                    val scale = minOf(size.width / totalW, size.height / totalH)
                                    val offsetX = (size.width - preview.width * scale) / 2f
                                    val offsetY = (size.height - preview.height * scale) / 2f

                                    withTransform({
                                        translate(left = offsetX, top = offsetY)
                                        scale(scale, scale, pivot = Offset.Zero)
                                    }) {
                                        drawComponent(preview, textMeasurer, settings = previewSettings)
                                    }
                                }
                            }
                        Text(translationViewModel.getString(i.name), color = settingsViewModel.textColor , modifier = Modifier.padding(10.dp))
                         }
                        }
                    }
                }
            }
        }
    }



