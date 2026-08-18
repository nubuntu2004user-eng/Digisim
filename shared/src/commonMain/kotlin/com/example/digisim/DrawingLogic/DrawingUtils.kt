package com.example.digisim.DrawingLogic

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.digisim.ComponentsDrawing.drawAnd
import com.example.digisim.LogicGates.And
import com.example.digisim.LogicGates.Nand
import com.example.digisim.LogicGates.Nor
import com.example.digisim.LogicGates.Not
import com.example.digisim.LogicGates.Or
import com.example.digisim.LogicGates.XNor
import com.example.digisim.LogicGates.Xor
import com.example.digisim.ParsingLogic.Component
import com.example.digisim.ParsingLogic.ComponentType
import com.example.digisim.ParsingLogic.Input
import com.example.digisim.ParsingLogic.Output
import com.example.digisim.ComponentsDrawing.drawAnd
import com.example.digisim.ComponentsDrawing.drawInput
import com.example.digisim.ComponentsDrawing.drawNand
import com.example.digisim.ComponentsDrawing.drawNor
import com.example.digisim.ComponentsDrawing.drawNot
import com.example.digisim.ComponentsDrawing.drawOr
import com.example.digisim.ComponentsDrawing.drawOutput
import com.example.digisim.ComponentsDrawing.drawXnor
import com.example.digisim.ComponentsDrawing.drawXor

//internal fun DrawScope.drawGate(component: Component, textMeasurer: TextMeasurer) {
//    val x = component.x
//    val y = component.y
//    val w = component.width
//    val h = component.height
//
//    // Outline
//    drawRect(
//        color = Color(0xFF4CAF50),
//        topLeft = Offset(x, y),
//        size = Size(w, h),
//        style = Stroke(width = 2f)
//    )
//    // Fill
//    drawRect(
//        color = Color(0xFFC8E6C9),
//        topLeft = Offset(x + 2f, y + 2f),
//        size = Size(w - 4f, h - 4f)
//    )
//
//    // Label – using Compose's drawText (multiplatform)
//    val label = component.type.label
//    val textStyle = TextStyle(
//        color = Color.Black,
//        fontSize = 20.sp,
//        fontWeight = FontWeight.Medium
//    )
//    val textLayout = textMeasurer.measure(label, style = textStyle)
//    drawText(
//        textMeasurer = textMeasurer,
//        text = label,
//        style = textStyle,
//        topLeft = Offset(
//            x + w / 2 - textLayout.size.width / 2,
//            y + h / 2 - textLayout.size.height / 2
//        )
//    )
//
//    // Input ports
//    component.inputPortPositions().forEach { pos ->
//        drawCircle(color = Color.Black, radius = 6f, center = pos)
//    }
//    // Output ports
//    component.outputPortPositions().forEach { pos ->
//        drawCircle(color = Color.Black, radius = 6f, center = pos)
//    }
//}

internal fun DrawScope.drawWire(
    sourceComponent: Component,
    sourcePortIndex: Int,
    targetComponent: Component,
    targetPortIndex: Int
) {
    val sourcePos = sourceComponent.outputPortPositions()[sourcePortIndex]
    val targetPos = targetComponent.inputPortPositions()[targetPortIndex]

    val midX = (sourcePos.x + targetPos.x) / 2f
    val path = Path().apply {
        moveTo(sourcePos.x, sourcePos.y)
        lineTo(midX, sourcePos.y)
        lineTo(midX, targetPos.y)
        lineTo(targetPos.x, targetPos.y)
    }

    drawPath(
        path = path,
        color = Color.Blue,
        style = Stroke(width = 3f)
    )
}

//internal fun DrawScope.drawInputOrOutput(drawable : Component, textMeasurer: TextMeasurer){
//    val x = drawable.x
//    val y = drawable.y
//    val w = drawable.width
//    val h = drawable.height
//
//    val textStyle = TextStyle(
//        color = Color.Black,
//        fontSize = 20.sp,
//        fontWeight = FontWeight.Medium
//    )
//
//    // Outline
//    drawRect(
//        color = Color(0xFF4CAF50),
//        topLeft = Offset(x, y),
//        size = Size(w, h),
//        style = Stroke(width = 2f)
//    )
//    // Fill
//    drawRect(
//        color = Color(0xFFC8E6C9),
//        topLeft = Offset(x + 2f, y + 2f),
//        size = Size(w - 4f, h - 4f)
//    )
//
//    drawCircle(color = Color.Black, radius = 6f, center = drawable.findPortOffset())
//
//    val label = drawable.ID.toString()
//    val textLayout = textMeasurer.measure(label, style = textStyle)
//    drawText(
//        textMeasurer = textMeasurer,
//        text = label,
//        style = textStyle,
//        topLeft = Offset(
//            x + w / 2 - textLayout.size.width / 2,
//            y + h / 2 - textLayout.size.height / 2
//        )
//    )
//
//}


 fun DrawScope.drawComponent(component: Component, textMeasurer: TextMeasurer){
        when (component.componentType){
            ComponentType.AND -> { drawAnd(component , textMeasurer)}
            ComponentType.OR -> { drawOr(component , textMeasurer)}
            ComponentType.NAND -> { drawNand(component , textMeasurer)}
            ComponentType.NOR -> { drawNor(component , textMeasurer)}
            ComponentType.XOR -> { drawXor(component , textMeasurer)}
            ComponentType.XNOR -> { drawXnor(component , textMeasurer)}
            ComponentType.NOT -> { drawNot(component , textMeasurer)}
            ComponentType.OUTPUT -> { drawOutput(component , textMeasurer)}
            ComponentType.INPUT -> { drawInput(component , textMeasurer)}


        }
}



