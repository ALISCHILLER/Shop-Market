package com.msa.eshop.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.msa.eshop.R

// Set of Material typography styles to start with
val iranianSansFont = Font(R.font.iransans_medium)

//val GilroyFontFamily = FontFamily(
//    Font(R.font.gilroy_black, FontWeight.Black),
//    Font(R.font.gilroy_extrabold, FontWeight.ExtraBold),
//    Font(R.font.gilroy_bold, FontWeight.Bold),
//    Font(R.font.gilroy_semibold, FontWeight.SemiBold),
//    Font(R.font.gilroy_medium, FontWeight.Medium),
//    Font(R.font.gilroy_regular, FontWeight.W400),
//)
val EShopFontFamily = FontFamily(
    Font(R.font.iransans_medium, FontWeight.Black)
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily(iranianSansFont),
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleSmall = TextStyle(
        fontFamily =  FontFamily(iranianSansFont),
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp

    ),
    titleLarge = TextStyle(
        fontFamily =  FontFamily(iranianSansFont),
        fontWeight = FontWeight.Normal,
        fontSize = 19.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelLarge =TextStyle(
        fontFamily =  FontFamily(iranianSansFont),
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily =  FontFamily(iranianSansFont),
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 19.sp,
        letterSpacing = 0.5.sp,
    ),
)