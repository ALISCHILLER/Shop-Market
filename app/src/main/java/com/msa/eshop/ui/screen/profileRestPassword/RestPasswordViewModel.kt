package com.msa.eshop.ui.screen.profileRestPassword

import androidx.lifecycle.ViewModel
import com.msa.eshop.utils.result.GeneralStateModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class RestPasswordViewModel @Inject constructor(

):ViewModel(){

    private val _state: MutableStateFlow<GeneralStateModel> = MutableStateFlow(GeneralStateModel())
    val state: StateFlow<GeneralStateModel> = _state
    fun restPasswordRequest(
        password:String,
        newPassword: String,
        repeatNewPassword: String
    ) {
        if (newPassword.isNullOrEmpty() || repeatNewPassword.isNullOrEmpty() || newPassword != repeatNewPassword) {
            updateStateError("رمز عبور جدید و تکرار آن نباید خالی باشند و باید با هم مطابقت داشته باشند.")
        } else {
            // اگر هر دو فیلد پر شده باشند و مساوی باشند
            // انجام عملیات مربوطه
        }


    }


    private fun updateStateLoading(isLoading: Boolean) {
        _state.value = _state.value.copy(isLoading = isLoading, error = null)
    }

    private fun updateStateError(errorMessage: String?) {
        _state.update { it.copy(isLoading = false, error = errorMessage) }
    }
}