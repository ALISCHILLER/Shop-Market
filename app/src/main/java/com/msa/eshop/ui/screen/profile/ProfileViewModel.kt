package com.msa.eshop.ui.screen.profile

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.navigation.NavOptions
import com.msa.eshop.ui.navigation.NavInfo
import com.msa.eshop.ui.navigation.NavManager
import com.msa.eshop.ui.navigation.Route
import com.msa.eshop.utils.CompanionValues
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val navManager: NavManager,
):ViewModel(){
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    fun navigateToAddressRegistration() {
        navManager.navigate(
            NavInfo(
                id = Route.AddressRegistrationScreen.route,
                navOption = NavOptions.Builder().setPopUpTo(
                    Route.ProfileScreen.route,
                    inclusive = false,
                ).build()
            )
        )
    }

    fun navigateToLogin() {
        sharedPreferences.edit().apply {
            remove(CompanionValues.TOKEN)
            apply()
        }
        navManager.navigate(
            NavInfo(id = Route.LoginScreen.route,
                navOption = NavOptions.Builder().setPopUpTo(
                    Route.ProfileScreen.route,
                    inclusive = true,
                ).build())
        )
    }

}