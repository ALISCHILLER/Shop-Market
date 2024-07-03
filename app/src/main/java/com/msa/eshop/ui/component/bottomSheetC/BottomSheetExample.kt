@file:OptIn(ExperimentalMaterial3Api::class)

package com.msa.eshop.ui.component.bottomSheetC

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BottomSheetExample(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,

    ) {
    val modalBottomSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showAnimation by remember { mutableStateOf(false) }
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = {
            showAnimation = true
            onDismissRequest()


        },
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        content()
    }

    if (showAnimation) {
        CircularDismissAnimation {
            showAnimation = false
            onDismissRequest()
        }
    }

}

@Composable
fun CircularDismissAnimation(onAnimationEnd: () -> Unit) {
    val size = remember { Animatable(1f) }
    val yOffset = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // اجرای انیمیشن‌ها به طور همزمان
        launch {
            size.animateTo(
                targetValue = 0f,
                animationSpec = tween(500, easing = FastOutSlowInEasing)
            )
        }
        launch {
            yOffset.animateTo(
                targetValue = 2000f,
                animationSpec = tween(500, easing = FastOutSlowInEasing)
            )
        }

        // منتظر ماندن تا هر دو انیمیشن به پایان برسند
        delay(500)
        onAnimationEnd()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Canvas(
            modifier = Modifier
                .size(200.dp * size.value)
                .offset { IntOffset(0, yOffset.value.toInt()) }
                .clip(CircleShape),
            onDraw = {
                val circleSize = size.value * 200f
                drawRoundRect(
                    color = Color.Red,
                    topLeft = Offset.Zero,
                    size = Size(circleSize, circleSize),
                    cornerRadius = CornerRadius(circleSize / 2, circleSize / 2)
                )
            }
        )
    }
}


@Composable
fun CountryList() {
    val countries = listOf(
        Pair("United States", "\uD83C\uDDFA\uD83C\uDDF8"),
        Pair("Canada", "\uD83C\uDDE8\uD83C\uDDE6"),
        Pair("India", "\uD83C\uDDEE\uD83C\uDDF3"),
        Pair("Germany", "\uD83C\uDDE9\uD83C\uDDEA"),
        Pair("France", "\uD83C\uDDEB\uD83C\uDDF7"),
        Pair("Japan", "\uD83C\uDDEF\uD83C\uDDF5"),
        Pair("China", "\uD83C\uDDE8\uD83C\uDDF3"),
        Pair("Brazil", "\uD83C\uDDE7\uD83C\uDDF7"),
        Pair("Australia", "\uD83C\uDDE6\uD83C\uDDFA"),
        Pair("Russia", "\uD83C\uDDF7\uD83C\uDDFA"),
        Pair("United Kingdom", "\uD83C\uDDEC\uD83C\uDDE7"),
    )

    LazyColumn {
        items(countries) { (country, flag) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 20.dp)
            ) {
                Text(
                    text = flag,
                    modifier = Modifier.padding(end = 20.dp)
                )
                Text(text = country)
            }
        }
    }
}

@Preview
@Composable
private fun BottomSheetExamplePreview() {
    BottomSheetExample(
        onDismissRequest = {}
    ) {
        CountryList()
    }
}
