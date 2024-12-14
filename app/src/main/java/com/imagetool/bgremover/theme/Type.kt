package com.imagetool.bgremover.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.imagetool.bgremover.R

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.inter_font, weight = FontWeight.W500)),
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),

    titleLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.inter_font, weight = FontWeight.W700)),
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.inter_font, weight = FontWeight.W500)),
        fontSize = 18.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    /* Other default text styles to override

       labelSmall = TextStyle(
           fontFamily = FontFamily.Default,
           fontWeight = FontWeight.Medium,
           fontSize = 11.sp,
           lineHeight = 16.sp,
           letterSpacing = 0.5.sp
       )
       */
)