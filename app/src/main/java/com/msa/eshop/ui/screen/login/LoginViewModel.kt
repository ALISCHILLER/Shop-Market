package com.msa.eshop.ui.screen.login

import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings.Global.putString
import android.system.Os.remove
import android.util.Log
import androidx.core.content.edit
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
import com.msa.eshop.utils.CompanionValues

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
    private val loginRepository: LoginRepository
): ViewModel(){

    private val _state: MutableStateFlow<GeneralStateModel> = MutableStateFlow(GeneralStateModel())
    val state: StateFlow<GeneralStateModel> = _state

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    fun clearState() {
        _state.value = GeneralStateModel()
    }
    fun getToken(
        username:String,
        password:String
    ){
        if (username.isBlank() || password.isBlank()) {
            updateStateError(context.getString(R.string.username_and_password_are_required))
            return
        }
        viewModelScope.launch {
            loginRepository.loginToken(
                TokenRequest(username,password)
            ).onEach { response ->
                Timber.d(response.data.toString())
                when (response.status) {
                    Resource.Status.SUCCESS-> {
                        Timber.tag("LoginViewModel").d("getToken SUCCESS: ${response.data}  " )
                        val loginResponse = response.data
                        loginResponse?.let { data ->
                            if (!data.hasError){
                                saveUserNameAndPassword(data.data,username,password)
                            }else
                                updateStateError(response.error?.message)
                        }
                    }
                    Resource.Status.LOADING-> {
                        updateStateLoading()
                    }
                    Resource.Status.ERROR -> {
                        val responseData = response.error
                        responseData?.let { error ->
                            Timber.tag("LoginViewModel").e("getToken ERROR: ${error.message}" )
                            updateStateError(response.error?.message)
                        }
                    }

                }
            }.collect()
        }
    }

    fun getUserData(){
        viewModelScope.launch {
            delay(1000)
            loginRepository.getUserData().onEach { response ->
                Timber.d(response.data.toString())
                when (response.status) {
                    Resource.Status.SUCCESS-> {
                        Timber.tag("LoginViewModel").d("getUserData SUCCESS: ${response.data}" )
                        val responseData = response.data
                        responseData?.let {data->
                            if (!data.hasError){
                                loginRepository.insertUser(data.data.get(0))
                                navigateToHome()
                            }else
                                updateStateError(response.error?.message)
                        }
                    }
                    Resource.Status.LOADING-> {
                        updateStateLoading()
                    }

                    Resource.Status.ERROR -> {
                        val responseData = response.error
                        responseData?.let { error ->
                            Timber.tag("LoginViewModel").e("getToken ERROR: ${error.message}" )
                            updateStateError(response.error?.message)
                        }
                    }

                }
            }.collect()
        }
    }
    private fun updateStateLoading() {
        _state.update { it.copy(isLoading = true) }
    }

    private fun updateStateLoading(isLoading: Boolean) {
        _state.value = _state.value.copy(isLoading = isLoading, error = null)
    }

    private fun updateStateError(errorMessage: String?) {
        _state.update { it.copy(isLoading = false, error = errorMessage) }
    }
    fun getSavedUsername(): String {
        return sharedPreferences.getString(CompanionValues.USERNAME, "") ?: ""
    }

    fun getSavedPassword(): String {
        return sharedPreferences.getString(CompanionValues.PASSWORD, "") ?: ""
    }
    private suspend fun saveUserNameAndPassword(
        token: String?,
        username:String,
        password:String
    ) {
        viewModelScope.launch {
            // حذف توکن قبلی
            sharedPreferences.edit {
                remove(CompanionValues.TOKEN)
            }
            // ذخیره کردن توکن جدید
            sharedPreferences.edit {
                putString(CompanionValues.TOKEN, token)
                putString(CompanionValues.USERNAME,username)
                putString(CompanionValues.PASSWORD,password)
            }
            getUserData()
        }
    }


    fun navigateToHome() {
        navManager.navigate(
            NavInfo(id = Route.HomeScreen.route,
                navOption = NavOptions.Builder().setPopUpTo(Route.LoginScreen.route,
                    inclusive = true).build())
        )
    }

}

