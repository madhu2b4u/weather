package com.demo.core.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.demo.core.ui.font.FontProvider

@Composable
fun TextWidget(
    text: String,
    fontSize: TextUnit = 16.sp,
    lineHeight: TextUnit = fontSize,
    fontWeight: FontWeight = FontWeight.Normal,
    color: Color = Color.Black,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    fontFamily: FontFamily = FontProvider.poppinsFontFamily
) {
    Text(
        text = text,
        fontSize = fontSize,
        lineHeight = lineHeight,
        fontWeight = fontWeight,
        color = color,
        style = style.copy(fontFamily = fontFamily)
    )
}