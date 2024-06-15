package com.msa.eshop.ui.screen.addressRegistration

import androidx.lifecycle.ViewModel
import androidx.navigation.NavOptions
import com.msa.eshop.ui.navigation.NavInfo
import com.msa.eshop.ui.navigation.NavManager
import com.msa.eshop.ui.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val navManager: NavManager,
):ViewModel(){


    fun navigateToLocationRegistration() {
        navManager.navigate(
            NavInfo(
                id = Route.LocationRegistrationScreen.route,
                navOption = NavOptions.Builder().setPopUpTo(
                    Route.AddressRegistrationScreen.route,
                    inclusive = true
                ).build()
            )
        )
    }

    fun navigateToAddressRegistration() {
        navManager.navigate(
            NavInfo(
                id = Route.AddressRegistrationScreen.route,
                navOption = NavOptions.Builder().setPopUpTo(
                    Route.LocationRegistrationScreen.route,
                    inclusive = true
                ).build()
            )
        )
    }

}