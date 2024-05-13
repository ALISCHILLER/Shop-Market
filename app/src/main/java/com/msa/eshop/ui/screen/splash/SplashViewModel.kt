package com.msa.eshop.ui.screen.splash

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import com.msa.eshop.ui.navigation.NavInfo
import com.msa.eshop.ui.navigation.NavManager
import com.msa.eshop.utils.CompanionValues
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val navManager: NavManager,
): ViewModel(){
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    fun splashCheck(){
        viewModelScope.launch {
            delay(2000)
            val token = sharedPreferences.getString(CompanionValues.TOKEN, "")
            if (token?.isNotEmpty() == true)
                navigateToHome()
            else
                navigateToLogin()
        }
    }


    fun navigateToHome() {
//        navManager.navigate(
//            NavInfo(id = Route.HomeScreen.route,
//                navOption = NavOptions.Builder().setPopUpTo(
//                    Route.SplashScreen.route,
//                    inclusive = true).build())
//        )
    }

    fun navigateToLogin() {
//        navManager.navigate(
//            NavInfo(id = Route.LoginScreen.route,
//                navOption = NavOptions.Builder().setPopUpTo(
//                    Route.SplashScreen.route,
//                    inclusive = true).build())
//        )
    }

}