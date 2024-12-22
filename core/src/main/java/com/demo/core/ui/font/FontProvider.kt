package com.demo.core.ui.font

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.demo.core.R

object FontProvider {
    val poppinsFontFamily = FontFamily(
        Font(R.font.poppins_regular, weight = FontWeight.Normal, style = FontStyle.Normal),
        Font(R.font.poppins_bold, weight = FontWeight.Bold, style = FontStyle.Normal),
        Font(R.font.poppins_medium, weight = FontWeight.Bold, style = FontStyle.Normal)
    )

    val poppinsFontFamilyBold = FontFamily(
        Font(R.font.poppins_bold, weight = FontWeight.Bold, style = FontStyle.Normal),
        Font(R.font.poppins_medium, weight = FontWeight.Bold, style = FontStyle.Normal)
    )
}