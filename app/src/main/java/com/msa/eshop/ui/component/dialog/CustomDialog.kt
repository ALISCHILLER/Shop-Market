package com.msa.eshop.ui.component.dialog

import android.view.Window
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider

@ReadOnlyComposable
@Composable
fun getDialogWindow(): Window? = (LocalView.current.parent as? DialogWindowProvider)?.window

@Composable
fun CustomDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {

    var showAnimatedDialog by remember { mutableStateOf(false) }

    LaunchedEffect(showDialog) {
        if (showDialog) showAnimatedDialog = true
    }

    if (showAnimatedDialog) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            val dialogWindow = getDialogWindow()

            SideEffect {
                dialogWindow.let { window ->
                    window?.setDimAmount(0f)
                    window?.setWindowAnimations(-1)
                }
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                var animateIn by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) { animateIn = true }
                AnimatedVisibility(
                    visible = animateIn && showDialog,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Box(
                        modifier = Modifier
                            .pointerInput(Unit) { detectTapGestures { onDismissRequest() } }
                            .background(Color.Black.copy(alpha = .56f))
                            .fillMaxSize()
                    )
                }
                AnimatedVisibility(
                    visible = animateIn && showDialog,
                    enter = fadeIn(spring(stiffness = Spring.StiffnessHigh)) + scaleIn(
                        initialScale = .8f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ),
                    exit = slideOutVertically { it / 8 } + fadeOut() + scaleOut(targetScale = .95f)
                ) {
                    Box(
                        Modifier
                            .pointerInput(Unit) { detectTapGestures { } }
                            .shadow(8.dp, shape = RoundedCornerShape(16.dp))
                            .width(300.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                MaterialTheme.colorScheme.surface,
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        content()
                    }

                    DisposableEffect(Unit) {
                        onDispose {
                            showAnimatedDialog = false
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ErrorWarning(
    onDismissRequest: () -> Unit,
    title: String,
    message: String

) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(Modifier.background(MaterialTheme.colorScheme.surface)) {

            var graphicVisible by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) { graphicVisible = true }

            AnimatedVisibility(
                visible = graphicVisible,
                enter = expandVertically(
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                    expandFrom = Alignment.CenterVertically,
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xE9EE8888),
                                    Color(0xFFE4BD79),
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Warning,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Box(modifier = Modifier.height(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Box(modifier = Modifier.height(8.dp))
                Text(text = message)
            }
            Row(
                modifier = Modifier.height(IntrinsicSize.Min)
            ) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(
                            MaterialTheme.colorScheme.onSurface.copy(alpha = .08f),
                            shape = RoundedCornerShape(10.dp)
                        )
                )

                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onDismissRequest() }
                        .weight(1f)
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "تایید", fontWeight = FontWeight.Bold, color = Color(0xFFFF332C))
                }
            }
        }
    }
}