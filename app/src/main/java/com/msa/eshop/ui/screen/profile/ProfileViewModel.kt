package com.msa.eshop.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.navigation.NavOptions
import com.msa.eshop.ui.navigation.NavInfo
import com.msa.eshop.ui.navigation.NavManager
import com.msa.eshop.ui.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val navManager: NavManager,
):ViewModel(){

    fun navigateToAddressRegistration() {
        navManager.navigate(
            NavInfo(
                id = Route.AddressRegistrationScreen.route,
                navOption = NavOptions.Builder().setPopUpTo(
                    Route.ProileScreen.route,
                    inclusive = true
                ).build()
            )
        )
    }

}