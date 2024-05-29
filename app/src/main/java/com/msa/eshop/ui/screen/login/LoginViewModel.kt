package com.msa.eshop.ui.screen.login

import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings.Global.putString
import android.system.Os.remove
import android.util.Log
import androidx.core.content.edit
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import com.msa.eshop.R
import com.msa.eshop.data.Model.GeneralStateModel
import com.msa.eshop.data.remote.utills.Resource
import com.msa.eshop.data.repository.LoginRepository
import com.msa.eshop.data.request.TokenRequest
import com.msa.eshop.ui.navigation.NavInfo
import com.msa.eshop.ui.navigation.NavManager
import com.msa.eshop.ui.navigation.Route
import com.msa.eshop.utils.BiometricTools
import com.msa.eshop.utils.CompanionValues
import com.msa.eshop.utils.Convert_Number
import com.msa.eshop.utils.makeRequest

import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val navManager: NavManager,
    private val loginRepository: LoginRepository,
    private val biometricTools: BiometricTools
) : ViewModel() {

    private val _state: MutableStateFlow<GeneralStateModel> = MutableStateFlow(GeneralStateModel())
    val state: StateFlow<GeneralStateModel> = _state

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    fun clearState() {
        _state.value = GeneralStateModel()
    }


    fun tokenRequest(
      username: String,
       password: String
    ) {
        if (username.isNullOrEmpty() || password.isNullOrEmpty())
            updateStateError("لطفا نام کاربری و رمز عبور را وارد کنید")
        else
        makeRequest(
            scope = viewModelScope,
            request = {
                loginRepository.loginToken(
                    TokenRequest(username, password)
                )
            },
            onSuccess = { response ->
                response?.let {
                    Timber.tag("LoginViewModel").d("getToken SUCCESS: ${it.data}  ")
                    saveUserNameAndPassword(it.data, username, password)

                }
            },
            updateStateLoading = { isLoading -> updateStateLoading(isLoading) },
            updateStateError = { errorMessage -> updateStateError(errorMessage) }
        )
    }

    fun UserRequest() {
        makeRequest(
            scope = viewModelScope,
            request = { loginRepository.getUserData() },
            onSuccess = { response ->

                response?.let {
                    Timber.tag("LoginViewModel").d("UserRequest SUCCESS: ${it.data}  ")
                    it.data?.let { it1 -> loginRepository.insertUser(it1.get(0)) }
                    navigateToHome()
                }
            },
            updateStateLoading = { isLoading -> updateStateLoading(isLoading) },
            updateStateError = { errorMessage -> updateStateError(errorMessage) }
        )
    }


    private fun updateStateLoading(isLoading: Boolean) {
        _state.value = _state.value.copy(isLoading = isLoading, error = null)
    }

    private fun updateStateError(errorMessage: String?) {
        _state.update { it.copy(isLoading = false, error = errorMessage) }
    }

    private fun saveUserNameAndPassword(token: String?, username: String, password: String) {
        viewModelScope.launch {
            sharedPreferences.edit().apply {
                remove(CompanionValues.TOKEN)
                putString(CompanionValues.TOKEN, token)
                putString(CompanionValues.USERNAME, username)
                putString(CompanionValues.PASSWORD, password)
            }.apply()
            UserRequest()
        }
    }

    fun getSavedUsername(): String {
        return sharedPreferences.getString(CompanionValues.USERNAME, "") ?: ""
    }

    fun getSavedPassword(): String {
        return sharedPreferences.getString(CompanionValues.PASSWORD, "") ?: ""
    }


    fun navigateToHome() {
        navManager.navigate(
            NavInfo(
                id = Route.HomeScreen.route,
                navOption = NavOptions.Builder().setPopUpTo(
                    Route.LoginScreen.route,
                    inclusive = true
                ).build()
            )
        )
    }

    fun biometricDialog(fragmentActivity: FragmentActivity){
        viewModelScope.launch {
            val message = biometricTools.showBiometricDialog(
                fragmentActivity,
                {},
                {},
                {}
            )
        }
    }

}

