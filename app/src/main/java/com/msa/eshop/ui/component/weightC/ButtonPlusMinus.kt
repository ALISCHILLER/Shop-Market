@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class
)

package com.msa.eshop.ui.component.weightC

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Spring.StiffnessLow
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msa.eshop.ui.theme.*
import com.msa.eshop.ui.theme.Typography
import com.msa.eshop.ui.theme.barcolorlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.sign


private const val ICON_BUTTON_ALPHA_INITIAL = 0.3f
private const val CONTAINER_BACKGROUND_ALPHA_INITIAL = 0.6f
private const val CONTAINER_BACKGROUND_ALPHA_MAX = 0.7f
private const val CONTAINER_OFFSET_FACTOR = 0.1f
private const val DRAG_LIMIT_HORIZONTAL_DP = 72
private const val DRAG_LIMIT_VERTICAL_DP = 64
private const val START_DRAG_THRESHOLD_DP = 2
private const val DRAG_LIMIT_HORIZONTAL_THRESHOLD_FACTOR = 0.6f
private const val DRAG_LIMIT_VERTICAL_THRESHOLD_FACTOR = 0.9f
private const val DRAG_HORIZONTAL_ICON_HIGHLIGHT_LIMIT_DP = 36
private const val DRAG_VERTICAL_ICON_HIGHLIGHT_LIMIT_DP = 60
private const val DRAG_CLEAR_ICON_REVEAL_DP = 2
private const val COUNTER_DELAY_INITIAL_MS = 100L
private const val COUNTER_DELAY_FAST_MS = 100L

@Composable
fun CounterButton(
    value: String,
    onValueDecreaseClick: () -> Unit,
    onValueIncreaseClick: () -> Unit,
    onValue: (String) -> Unit,
    onValueClearClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .width(120.dp)
            .height(50.dp)
    ) {
        val thumbOffsetX = remember { Animatable(0f) }
        val thumbOffsetY = remember { Animatable(0f) }
        val verticalDragButtonRevealPx = DRAG_CLEAR_ICON_REVEAL_DP.dp.dpToPx()


        ButtonContainer(
            thumbOffsetX = thumbOffsetX.value,
            thumbOffsetY = thumbOffsetY.value,
            onValueDecreaseClick = { onValue(maxOf(value.toInt() - 1, 0).toString()) },
            onValueIncreaseClick = { onValue((value.toInt() + 1).toString()) },
            onValueClearClick = onValueClearClick,
            clearButtonVisible = thumbOffsetY.value >= verticalDragButtonRevealPx,
            modifier = Modifier
        )
        DraggableThumbButton(
            value = value,
            thumbOffsetX = thumbOffsetX,
            thumbOffsetY = thumbOffsetY,
            onClick = onValueIncreaseClick,
            onValueDecreaseClick = onValueDecreaseClick,
            onValueIncreaseClick = onValueIncreaseClick,
            onValue = { onValue(it) },
            onValueReset = onValueClearClick,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
@Composable
fun CounterButtonNew(
    value: String,
    onValueDecreaseClick: () -> Unit,
    onValueIncreaseClick: () -> Unit,
    onValue: (String) -> Unit,
    onValueClearClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .width(150.dp)
            .height(60.dp)
    ) {
        val thumbOffsetX = remember { Animatable(0f) }
        val thumbOffsetY = remember { Animatable(0f) }
        val verticalDragButtonRevealPx = DRAG_CLEAR_ICON_REVEAL_DP.dp.dpToPx()

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            Icon(
                imageVector = Icons.Outlined.Remove,
                contentDescription = "contentDescription",
                tint = if (isPressed) barcolorlow else barcolorlow,
                modifier = modifier
                    .padding(horizontal = 3.dp)
                    .size(30.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        onValue(maxOf(value.toInt() - 1, 0).toString())
                     }
                    .border(
                        border = BorderStroke(width = 1.dp, color = barcolorlow),
                        shape = RoundedCornerShape(8.dp)
                    )

            )
            OutlinedTextField(
                modifier = modifier
                    .padding(horizontal = 3.dp)
                    .weight(1.0f),
                value = value,
                onValueChange = { newValue ->
                    if (newValue.isNotEmpty())
                        onValue(newValue)
                    else
                        onValue("0")
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = Typography.labelSmall.copy(
                    textAlign = TextAlign.Center
                )
            )
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "contentDescription",
                tint = Color.White,
                modifier = modifier
                    .padding(horizontal = 3.dp)
                    .size(30.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        onValue((value.toInt() + 1).toString())
                    }
                    .background(RedMain)
            )



        }
    }
    

}



@Composable
private fun ButtonContainer(
    thumbOffsetX: Float,
    thumbOffsetY: Float,
    onValueDecreaseClick: () -> Unit,
    onValueIncreaseClick: () -> Unit,
    onValueClearClick: () -> Unit,
    modifier: Modifier = Modifier,
    clearButtonVisible: Boolean = false,
) {
    // at which point the icon should be fully visible
    val horizontalHighlightLimitPx = DRAG_HORIZONTAL_ICON_HIGHLIGHT_LIMIT_DP.dp.dpToPx()
    val verticalHighlightLimitPx = DRAG_VERTICAL_ICON_HIGHLIGHT_LIMIT_DP.dp.dpToPx()

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
    ) {
        // decrease button
        IconControlButtondecrease(
            icon = Icons.Outlined.Remove,
            contentDescription = "Decrease count",
            onClick = onValueDecreaseClick,
            enabled = !clearButtonVisible,
            tintColor = Color.Black.copy(
                alpha = if (clearButtonVisible) {
                    0.0f
                } else if (thumbOffsetX < 0) {
                    (thumbOffsetX.absoluteValue / horizontalHighlightLimitPx).coerceIn(
                        ICON_BUTTON_ALPHA_INITIAL,
                        1f
                    )
                } else {
                    ICON_BUTTON_ALPHA_INITIAL
                }
            )
        )


        // increase button
        IconControlButton(
            icon = Icons.Outlined.Add,
            contentDescription = "Increase count",
            onClick = onValueIncreaseClick,
            enabled = !clearButtonVisible,
            tintColor = Color.Black.copy(
                alpha = if (clearButtonVisible) {
                    0.0f
                } else if (thumbOffsetX > 0) {
                    (thumbOffsetX.absoluteValue / horizontalHighlightLimitPx).coerceIn(
                        ICON_BUTTON_ALPHA_INITIAL,
                        1f
                    )
                } else {
                    ICON_BUTTON_ALPHA_INITIAL
                }
            )
        )
    }
}

@Composable
private fun IconControlButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tintColor: Color = Color.Black,
    clickTintColor: Color = Color.Red,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        tint = Color.White,
        modifier = modifier
            .size(30.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                onClick()
            }
            .background(RedMain)

    )

}

@Composable
private fun IconControlButtondecrease(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tintColor: Color = Color.Black,
    clickTintColor: Color = Color.Red,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        tint = if (isPressed) barcolorlow else barcolorlow,
        modifier = modifier
            .size(30.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                onClick()
            }
            .border(
                border = BorderStroke(width = 1.dp, color = barcolorlow),
                shape = RoundedCornerShape(8.dp)
            )
    )

}


@Composable
private fun DraggableThumbButton(
    value: String,
    thumbOffsetX: Animatable<Float, AnimationVector1D>,
    thumbOffsetY: Animatable<Float, AnimationVector1D>,
    onClick: () -> Unit,
    onValueDecreaseClick: () -> Unit,
    onValueIncreaseClick: () -> Unit,
    onValue: (String) -> Unit,
    onValueReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
//    Box(
//        contentAlignment = Alignment.Center,
//        modifier = modifier
//            .size(50.dp)
//    ) {
//        Text(
//            text = value,
//            color = Color.White,
//            textAlign = TextAlign.Center,
//            style = Typography.labelSmall
//        )
    OutlinedTextField(
        modifier = modifier
            .padding(horizontal = 3.dp)
            .size(80.dp),
        value = value,
        onValueChange = { newValue ->
            if (newValue.isNotEmpty())
                onValue(newValue)
            else
                onValue("0")
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = Typography.labelSmall.copy(
            textAlign = TextAlign.Center
        )
    )

//    }
}

@Composable
private fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }

private enum class DragDirection {
    NONE, HORIZONTAL, VERTICAL
}


@Preview
@Composable
private fun ButtonPlusMinus() {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.wrapContentSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var valueCounter by remember {
                mutableStateOf(0)
            }

            CounterButtonNew(
                value = valueCounter.toString(),
                onValueIncreaseClick = {
                    valueCounter += 1
                },
                onValueDecreaseClick = {
                    valueCounter = maxOf(valueCounter - 1, 0)
                },
                onValueClearClick = {
                    valueCounter = 0
                },
                onValue = { valueCounter = it.toInt() }
            )
        }
    }
}


@Composable
fun dialogAddNumber(modifier: Modifier = Modifier) {

}