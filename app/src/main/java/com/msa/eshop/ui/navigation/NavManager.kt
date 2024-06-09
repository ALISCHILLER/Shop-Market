

package com.msa.eshop.ui.navigation

import android.os.Bundle
import androidx.navigation.NavOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavManager @Inject constructor() {

    private val _routeInfo = MutableStateFlow(NavInfo())
    val routeInfo: StateFlow<NavInfo> = _routeInfo

    fun navigate(routeInfo: NavInfo?) {
        //Clear previous error, when navigating

        _routeInfo.update { routeInfo ?: NavInfo() }
    }
}


data class NavInfo(
    val id:String?=null,
    val args: Bundle? = null, // اضافه کردن args برای پشتیبانی از Bundle
    val navOption: NavOptions? = null
)