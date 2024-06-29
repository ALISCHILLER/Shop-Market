package com.msa.eshop.ui.screen.login

import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import com.msa.eshop.utils.result.GeneralStateModel
import com.msa.eshop.data.repository.LoginRepository
import com.msa.eshop.data.Model.request.TokenRequest
import com.msa.eshop.ui.navigation.NavInfo
import com.msa.eshop.ui.navigation.NavManager
import com.msa.eshop.ui.navigation.Route
import com.msa.eshop.utils.BiometricTools
import com.msa.eshop.utils.CompanionValues
import com.msa.eshop.utils.result.makeRequest

import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val _biometric: MutableStateFlow<String> = MutableStateFlow("")
    val biometric: StateFlow<String> get() = _biometric

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    fun clearState() {
        _state.value = GeneralStateModel()
    }


    fun tokenRequest(
        username: String,
        password: String,
        rememberme:Boolean
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

                        saveUserNameAndPassword(it.data, username, password,rememberme)

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

    private fun saveUserNameAndPassword(
        token: String?,
        username: String,
        password: String,
        rememberme: Boolean,
    ) {
        viewModelScope.launch {
            sharedPreferences.edit().apply {
                if (rememberme) {
                    putString(CompanionValues.USERNAME, username)
                    putString(CompanionValues.PASSWORD, password)
                } else {
                    remove(CompanionValues.USERNAME)
                    remove(CompanionValues.PASSWORD)
                }
                putString(CompanionValues.TOKEN, token)
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

    fun biometricDialog(fragmentActivity: FragmentActivity) {
        viewModelScope.launch {
            val savedUsername = getSavedUsername()
            val savedPassword = getSavedPassword()

            val message = biometricTools.showBiometricDialog(
                fragmentActivity,
                onAuthenticationFailed = {},
                onAuthenticationError = {},
                onAuthenticationSucceeded = {
                    if(savedPassword.isNotEmpty()&& savedUsername.isNotEmpty())
                        tokenRequest(savedUsername,savedPassword,true)
                }
            )
        }
    }

}

