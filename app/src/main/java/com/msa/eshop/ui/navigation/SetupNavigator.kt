@file:OptIn(ExperimentalAnimationApi::class, ExperimentalAnimationApi::class)

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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.msa.eshop.ui.screen.address.OrderAddressScreen
import com.msa.eshop.ui.screen.addressRegistration.AddressRegistrationScreen
import com.msa.eshop.ui.screen.addressRegistration.LocationRegistrationScreen
import com.msa.eshop.ui.screen.orderDetailsReport.OrderDetailsReportScreen
import com.msa.eshop.ui.screen.orderStatusReport.OrderStatusReportScreen
import com.msa.eshop.ui.screen.paymentMethod.PaymentMethodScreen
import com.msa.eshop.ui.screen.profile.ProfileScreen
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
fun MainActivity.SetupNavigator() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route


    val navInfo by navManager.routeInfo.collectAsState()
    LaunchedEffect(key1 = navInfo) {
        navInfo.id?.let {
            if (it == Route.BACK.route) {
                navController.popBackStack()
                navManager.navigate(null)
                return@let
            } else if (it == Route.LoginScreen.route) {

            }
            val bundle = Bundle()
            bundle.putString("link", it)
            navController.navigate(it, navOptions = navInfo.navOption)
            navManager.navigate(null)
        }
    }

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar(currentRoute)) {
                BottomNavNoAnimation(
                    currentRoute,
                    onClick = { navigateToTab(navController, it) }
                )
            }
        }
    ) {
        Box(modifier = Modifier.padding(bottom = it.calculateBottomPadding())) {
            NavHost(
                navController = navController,
                startDestination = Route.SplashScreen.route,
            ) {
                // Splash
                composable(
                    route = Route.SplashScreen.route,
                    exitTransition = {
                        slideOutVertically(
                            targetOffsetY = { -it },
                            animationSpec = tween(durationMillis = 2000)
                        )
                    }
                ) { SplashScreen() }

                // Login
                composable(
                    route = Route.LoginScreen.route,
                    enterTransition = {
                        slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = tween(durationMillis = 2000)
                        )
                    },
                    exitTransition = {
                        slideOutVertically(
                            targetOffsetY = { -it },
                            animationSpec = tween(durationMillis = 700)
                        )
                    }
                ) { LoginScreen() }

                // Home
                composable(route = Route.HomeScreen.route) { HomeScreen() }

                // Basket
                composable(route = Route.BasketScreen.route) { BasketScreen() }

                // Simulate
                composable(route = Route.SimulateScreen.route) { SimulateScreen() }

                // Order Address
                composable(route = Route.OrderAddressScreen.route) { OrderAddressScreen() }

                // Payment Method
                composable(route = Route.PaymentMethodScreen.route) { PaymentMethodScreen() }

                // Order Status Report
                composable(route = Route.OrderStatusReportScreen.route) { OrderStatusReportScreen() }

                // Order Details Report
                composable(
                    route = "${Route.OrderDetailsReportScreen.route}/{card}",
                    arguments = listOf(navArgument("card") { type = NavType.IntType })
                ) { backStackEntry ->
                    val card = backStackEntry.arguments?.getInt("card", 0)
                    OrderDetailsReportScreen(card = card)
                }

                // Profile
                composable(route = Route.ProfileScreen.route) { ProfileScreen() }
                composable(route = Route.AddressRegistrationScreen.route) { AddressRegistrationScreen() }
                composable(route = Route.LocationRegistrationScreen.route) { LocationRegistrationScreen() }
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
        navController.graph.startDestinationRoute?.let { screenRoute ->
            popUpTo(screenRoute) {
                inclusive = true
            }
        }
        launchSingleTop = true
        restoreState = true
    }
}


