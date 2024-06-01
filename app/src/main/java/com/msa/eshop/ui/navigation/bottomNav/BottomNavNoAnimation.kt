@file:OptIn(ExperimentalAnimationApi::class)

package com.msa.eshop.ui.navigation.bottomNav

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import com.msa.eshop.R
import com.msa.eshop.ui.navigation.Route
import com.msa.eshop.ui.theme.Typography
import com.msa.eshop.ui.theme.barcolor
import com.msa.eshop.ui.theme.barcolorDark


@Preview
@Composable
fun BottomNavNoAnimationPreview(
) {
    Scaffold(

        bottomBar = {
            BottomNavNoAnimation(onClick = {})
        }
    ) {
        it
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .background(Color.Red)


        ) {

        }
    }

}

@ExperimentalAnimationApi
@Composable
fun BottomNavNoAnimation(
    onClick: (String) -> Unit
) {
    val screens = listOf(
        Screen(
            "صفحه اصلی",
            Icons.Filled.Home,
            Icons.Outlined.Home,
            Route.HomeScreen.route
        ),
        Screen(
            "گزارش خرید",
            ImageVector.vectorResource(R.drawable.ic_invoice),
            ImageVector.vectorResource(R.drawable.ic_invoice),
            Route.HomeScreen.route
        ),
        Screen(
            "سبد",
            ImageVector.vectorResource(R.drawable.ic_basket),
            ImageVector.vectorResource(R.drawable.ic_basket),
            Route.BasketScreen.route
        ),
        Screen(
            "پروفایل",
            ImageVector.vectorResource(R.drawable.ic_profile),
            ImageVector.vectorResource(R.drawable.ic_profile),
            Route.BasketScreen.route
        )

    )
    var selectedScreen by remember { mutableStateOf(0) }
    Box(
        Modifier
            .padding(horizontal = 8.dp, vertical = 5.dp)
            .shadow(5.dp, shape = RoundedCornerShape(10.dp))
            .background(color = colors.surface)
            .height(64.dp)
            .fillMaxWidth()
    ) {
        Row(
            Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            for (screen in screens) {
                val isSelected = screen == screens[selectedScreen]
                val animatedWeight by animateFloatAsState(targetValue = if (isSelected) 1.5f else 1f)
                Box(
                    modifier = Modifier
                        .weight(animatedWeight),
                    contentAlignment = Alignment.Center,
                ) {
                    val interactionSource = remember { MutableInteractionSource() }
                    BottomNavItem(
                        modifier = Modifier.clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            selectedScreen = screens.indexOf(screen)
                            onClick(
                                screen.route
                            )
                        },
                        item = screen,
                        isSelected = isSelected
                    )
                }
            }
        }
    }
}


@ExperimentalAnimationApi
@Composable
private fun BottomNavItem(
    modifier: Modifier = Modifier,
    item: Screen,
    isSelected: Boolean,
) {
    val animatedHeight by animateDpAsState(targetValue = if (isSelected) 36.dp else 26.dp)
    val animatedElevation by animateDpAsState(targetValue = if (isSelected) 15.dp else 0.dp)
    val animatedAlpha by animateFloatAsState(targetValue = if (isSelected) 1f else .5f)
    val animatedIconSize by animateDpAsState(
        targetValue = if (isSelected) 26.dp else 20.dp,
        animationSpec = spring(
            stiffness = Spring.StiffnessLow,
            dampingRatio = Spring.DampingRatioMediumBouncy
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .height(animatedHeight)  // <-------
                .shadow(
                    elevation = animatedElevation,  // <-------
                    shape = RoundedCornerShape(20.dp)
                )
                .background(
                    color = if (isSelected) Color.Red else colors.surface,
                    shape = RoundedCornerShape(20.dp)
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            FlipIcon(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(2.dp)
                    .fillMaxHeight()
                    .alpha(animatedAlpha)  // <-------
                    .size(animatedIconSize),  // <-------
                isActive = isSelected,
                activeIcon = item.activeIcon,
                inactiveIcon = item.inactiveIcon,
                ""
            )

            AnimatedVisibility(visible = isSelected) {
                Text(
                    text = item.title,
                    modifier = Modifier.padding(start = 2.dp, end = 2.dp),
                    maxLines = 1,
                    color = Color.White,
                    style = Typography.labelSmall
                )
            }
        }
    }
}


@Composable
fun FlipIcon(
    modifier: Modifier = Modifier,
    isActive: Boolean,
    activeIcon: ImageVector,
    inactiveIcon: ImageVector,
    contentDescription: String,
) {
    val animationRotation by animateFloatAsState(
        targetValue = if (isActive) 180f else 0f,
        animationSpec = spring(
            stiffness = Spring.StiffnessLow,
            dampingRatio = Spring.DampingRatioMediumBouncy
        )
    )
    Box(
        modifier = modifier
            .graphicsLayer { rotationY = animationRotation },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            rememberVectorPainter(image = if (animationRotation > 90f) activeIcon else inactiveIcon),
            contentDescription = contentDescription,
            tint = if (isActive) Color.White else barcolorDark
        )
    }
}

data class Screen(
    val title: String,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector,
    val route: String,

    ) {
}