package com.msa.eshop.ui.screen.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.msa.eshop.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel()
) {
    val gradientBrush = Brush.linearGradient(
        colors = listOf(Color(0xFFE3152F), Color(0xFFB9081F), Color(0xFFFF2F49)),
        start = Offset(0f, Float.POSITIVE_INFINITY),
        end = Offset(Float.POSITIVE_INFINITY, 0f)
    )
    var visible by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        while (true) {
            visible = !visible
            delay(2000)
        // مدت زمان بین انیمیشن‌ها
        }

    }
    LaunchedEffect(Unit) {
        viewModel.splashCheck()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = gradientBrush),

            ){
            Image(
                painter = painterResource(id = R.drawable.zar_market),
                contentDescription = "logo",
                modifier = Modifier
                    .size(170.dp, 170.dp)
                    .align(Alignment.Center) // قرار دادن تصویر در وسط
            )
        }
        AnimatedVisibility(
            visible = visible,
//            enter = slideInHorizontally(
//                initialOffsetX = { fullWidth -> fullWidth },
//                animationSpec = tween(durationMillis = 1000)
//            ),
            exit = slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(durationMillis = 1000)
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(brush = gradientBrush)
            )
        }

    }

//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(brush = gradientBrush),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Image(
//            painter = painterResource(id = R.drawable.logozar),
//            contentDescription = "logo",
//            modifier = Modifier
//                .size(110.dp, 82.dp)
//                .layoutId("logo")
//        )
//
////        viewModel.splashCheck()
//    }

}

@Preview
@Composable
private fun SplashScreenPreview() {
    SplashScreen()
}