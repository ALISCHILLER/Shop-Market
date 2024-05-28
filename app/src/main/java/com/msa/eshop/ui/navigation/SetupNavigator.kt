@file:OptIn(ExperimentalAnimationApi::class)

package com.msa.eshop.ui.navigation

import android.os.Bundle
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.msa.eshop.MainActivity
import com.msa.eshop.ui.navigation.bottomNav.BottomNavNoAnimation
import com.msa.eshop.ui.navigation.bottomNav.BottomNavaghtion
import com.msa.eshop.ui.screen.basket.BasketScreen
import com.msa.eshop.ui.screen.home.HomeScreen
import com.msa.eshop.ui.screen.login.LoginScreen
import com.msa.eshop.ui.screen.simulate.SimulateScreen
import com.msa.eshop.ui.screen.splash.SplashScreen
import timber.log.Timber
import androidx.compose.animation.fadeOut
import kotlinx.coroutines.delay

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
fun MainActivity.SetupNavigator() {
    val navController = rememberNavController()
    val backStackState = navController.currentBackStackEntryAsState().value


    val navInfo by navManager.routeInfo.collectAsState()
    LaunchedEffect(key1 = navInfo) {
        navInfo.id?.let {
            if (it == Route.BACK.route) {
                // firebaseAnalytics.logEvent("Click_Back",null)

                navController.navigateUp()
                navManager.navigate(null)
                return@let
            }
            var bundle = Bundle()
            bundle.putString("link", it)
            // firebaseAnalytics.logEvent("Click_Navigate",bundle)

            navController.navigate(it, navOptions = navInfo.navOption)
            navManager.navigate(null)
        }

    }

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar(
                    navController.currentBackStackEntryAsState()
                        .value?.destination?.route
                )
            ) {
                BottomNavNoAnimation(onClick = {
                    navigateToTab(
                        navController = navController,
                        route = it
                    )
                })

            }

        }
    ) {

        Box(
            modifier = Modifier
                .padding(bottom = it.calculateBottomPadding())
        ) {

            Timber.tag("SetupNavigator").e("SetupNavigator SetupNavigator: ")
            val bottomPadding = it.calculateBottomPadding()
            NavHost(
                navController = navController,
                startDestination = Route.SplashScreen.route,
                ) {

                //Splash
                composable(
                    route = Route.SplashScreen.route,
                    exitTransition = {
                        slideOutVertically(
                            targetOffsetY = { -it },
                            animationSpec = tween(durationMillis = 2000) // مدت زمان انیمیشن افزایش یافته
                        )
                    }

                ) {
                    SplashScreen()

                }


                //Login
                composable(
                    route = Route.LoginScreen.route,
                    enterTransition = {
                        slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = tween(durationMillis = 2000)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    }
                ) {
                    LoginScreen()
                }
                //product
                composable(route = Route.HomeScreen.route) { HomeScreen() }

                //basket
                composable(route = Route.BasketScreen.route) { BasketScreen() }
                composable(route = Route.SimulateScreen.route) { SimulateScreen() }


                //profile


            }
        }


    }
}

// تابع برای تعیین اینکه آیا bottomBar باید نمایش داده شود یا خیر
fun shouldShowBottomBar(currentRoute: String?): Boolean {
    return currentRoute != Route.SplashScreen.route && currentRoute != Route.LoginScreen.route
}

private fun navigateToTab(navController: NavController, route: String) {
    navController.navigate(route) {
        navController.graph.startDestinationRoute?.let { screen_route ->
            popUpTo(screen_route) {
                saveState = true
            }
        }
        launchSingleTop = true
        restoreState = true
    }
}