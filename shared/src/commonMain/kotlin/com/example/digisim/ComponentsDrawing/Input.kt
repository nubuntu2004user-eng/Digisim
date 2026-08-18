package com.example.digisim.ComponentsDrawing

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.digisim.ParsingLogic.Component

internal fun DrawScope.drawInput(component : Component , textMeasurer: TextMeasurer){
    val x = component.x
    val y = component.y
    val w = component.width
    val h = component.height

    val textStyle = TextStyle(
        color = Color.Black,
        fontSize = 20.sp,
        fontWeight = FontWeight.Medium
    )

    // Outline
    drawRect(
        color = Color(0xFF4CAF50),
        topLeft = Offset(x, y),
        size = Size(w, h),
        style = Stroke(width = 2f)
    )
    // Fill
    drawRect(
        color = Color(0xFFC8E6C9),
        topLeft = Offset(x + 2f, y + 2f),
        size = Size(w - 4f, h - 4f)
    )

    drawCircle(color = Color.Black, radius = 6f, center = component.findPortOffset())

    val label = component.ID.toString()
    val textLayout = textMeasurer.measure(label, style = textStyle)
    drawText(
        textMeasurer = textMeasurer,
        text = label,
        style = textStyle,
        topLeft = Offset(
            x + w / 2 - textLayout.size.width / 2,
            y + h / 2 - textLayout.size.height / 2
        )
    )


}

