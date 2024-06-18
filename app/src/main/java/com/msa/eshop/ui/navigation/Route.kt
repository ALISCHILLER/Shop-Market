package com.msa.eshop.ui.navigation

import androidx.navigation.NamedNavArgument


sealed class Route(
    val route: String,
    val arguments: List<NamedNavArgument> = emptyList()
) {

    object BACK : Route(route = "back")

    object SplashScreen : Route(route = "splashScreen")

    // Login
    object LoginScreen : Route(route = "loginScreen")
    object NewPasswordScreen : Route(route = "newPasswordScreen")
    object NationalCodeResetPassScreen : Route(route = "nationalCodeResetPassScreen")
    object OtpScreen : Route(route = "otpScreen")

    // Home Product
    object HomeScreen : Route(route = "homeScreen")
    object DetailsProductScreen : Route(route = "detailsProductScreen")

    // Basket
    object BasketScreen : Route(route = "BasketScreen")
    object SimulateScreen : Route(route = "simulateScreen")
    object OrderAddressScreen : Route(route = "OrderAddressScreen")
    object PaymentMethodScreen : Route(route = "paymentMethodScreen")

    // Profile Customer
    object ProfileScreen : Route(route = "profileScreen")
    object AddressRegistrationScreen : Route(route = "addressRegistrationScreen")
    object LocationRegistrationScreen : Route(route = "locationRegistrationScreen")

    // Report
    object OrderStatusReportScreen : Route(route = "orderStatusReportScreen")
    object OrderDetailsReportScreen : Route(route = "orderDetailsReportScreen")
}
