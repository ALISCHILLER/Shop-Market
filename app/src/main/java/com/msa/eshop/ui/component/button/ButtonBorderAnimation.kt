package com.msa.eshop.ui.component.button

import androidx.compose.animation.core.EaseOutSine
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.msa.eshop.ui.theme.RoyalPurple
import com.msa.eshop.ui.theme.RoyalRed

@Composable
fun BorderAnimation(
    cardShape: RoundedCornerShape,
    borderWidth: Dp,
    loading: Boolean,
    backgroundColor: Color,
    borderColors: List<Color>,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val rotate by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = EaseOutSine
            )
        ),
        label = ""
    )
    Column(
        modifier = Modifier.wrapContentSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .wrapContentSize()
                .padding(10.dp)
                .clip(cardShape)
                .padding(borderWidth)
                .drawWithContent {
                    rotate(
                        if (loading) {
                            rotate
                        } else {
                            0f
                        }
                    ) {
                        drawCircle(
                            brush = if (loading) {
                                Brush.sweepGradient(
                                    borderColors
                                )
                            } else {
                                Brush.linearGradient(
                                    listOf(
                                        backgroundColor,
                                        backgroundColor
                                    )
                                )
                            },
                            radius = size.width,
                            blendMode = BlendMode.SrcIn,
                        )
                    }
                    drawContent()
                }
                .background(backgroundColor, cardShape)
        ) {
            content()
        }
    }
}


@Composable
fun ButtonBorderAnmation(
    modifier: Modifier,
    text: String,
    enabled: Boolean,
    loading: Boolean,
    onClick: () -> Unit
) {
    val circleColors = listOf(
        Color(0xFFFF5252), // قرمز روشن
        Color(0xFFFF4081), // صورتی
        Color(0xFFFF5722), // نارنجی روشن
        Color(0xFFFF7043), // نارنجی تیره
        Color(0xFFFFA726), // نارنجی طلایی
        Color(0xFF42A5F5), // آبی روشن
        Color(0xFFFF5252)  // قرمز روشن
    )
    BorderAnimation(
        cardShape = RoundedCornerShape(6.dp),
        borderWidth = 6.dp,
        loading = loading,
        backgroundColor = Color.Red,
        borderColors = circleColors,
    ) {
        Button(
            modifier = modifier,
            enabled = true,
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(
                contentColor = RoyalRed,
                containerColor = RoyalRed,
                disabledContainerColor =RoyalPurple,
                disabledContentColor =RoyalPurple
            ),
            onClick = {
                onClick.invoke()
            }
        ) {
            Text(
                modifier = Modifier.padding(6.dp),
                text = text,
                color = Color.White
            )
        }
    }
}


@Preview
@Composable
private fun preciew() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        ButtonBorderAnmation(
            modifier = Modifier,
            "LOGIN",
            false,
            false,
            {}
        )
    }
}
