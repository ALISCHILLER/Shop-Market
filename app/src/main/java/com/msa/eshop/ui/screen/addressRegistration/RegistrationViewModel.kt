package com.msa.eshop.ui.screen.addressRegistration

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import com.msa.eshop.ui.navigation.NavInfo
import com.msa.eshop.ui.navigation.NavManager
import com.msa.eshop.ui.navigation.Route
import com.msa.eshop.utils.map.location.PiLocationException
import com.msa.eshop.utils.map.location.PiLocationManager
import com.msa.eshop.utils.result.GeneralStateModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val navManager: NavManager,
    private val locationManager: PiLocationManager
):ViewModel(){

    private val _state: MutableStateFlow<GeneralStateModel> = MutableStateFlow(GeneralStateModel())
    val state: StateFlow<GeneralStateModel> = _state

    private val _location = MutableStateFlow<Location?>(null)
    val location = _location.asStateFlow()

    fun startLocationUpdates() {
        viewModelScope.launch {
            try {
                locationManager.locationUpdates(5000L).collect { location ->
                    _location.value = location
                    updateStateLoading(false)
                }
            } catch (e: PiLocationException) {
                locationManager.turnOnGPS()
                updateStateLoading(true)

                // Handle the exception, maybe update the UI to show an error message
                // You can use a StateFlow or LiveData to communicate this error to the UI
                // Example:
                // _locationError.value = e.message
            }
        }
    }

    private fun updateStateLoading(isLoading: Boolean) {
        _state.value = _state.value.copy(isLoading = isLoading, error = null)
    }

    private fun updateStateError(errorMessage: String?) {
        _state.update { it.copy(isLoading = false, error = errorMessage) }
    }
    private fun updateStateMessage(message: String?) {
        _state.update { it.copy(isLoading = false, error=null, message = message) }
    }

    fun navigateToLocationRegistration() {
        navManager.navigate(
            NavInfo(
                id = Route.LocationRegistrationScreen.route,
                navOption = NavOptions.Builder().setPopUpTo(
                    Route.AddressRegistrationScreen.route,
                    inclusive = false
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
                    inclusive = false
                ).build()
            )
        )
    }

}