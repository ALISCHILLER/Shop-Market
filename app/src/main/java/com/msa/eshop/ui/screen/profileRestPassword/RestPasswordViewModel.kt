package com.msa.eshop.ui.screen.profileRestPassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msa.eshop.data.Model.request.ChangePasswordRequest
import com.msa.eshop.data.repository.RestPasswordRepository
import com.msa.eshop.utils.result.GeneralStateModel
import com.msa.eshop.utils.result.makeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RestPasswordViewModel @Inject constructor(
    private val restPasswordRepository: RestPasswordRepository
) : ViewModel() {

    private val _state: MutableStateFlow<GeneralStateModel> = MutableStateFlow(GeneralStateModel())
    val state: StateFlow<GeneralStateModel> = _state

    fun clearState() {
        _state.value = GeneralStateModel()
    }


    fun restPasswordRequest(
        password: String,
        newPassword: String,
        repeatNewPassword: String
    ) {
        if (newPassword.isNullOrEmpty() || repeatNewPassword.isNullOrEmpty() || newPassword != repeatNewPassword) {
            updateStateError("رمز عبور جدید و تکرار آن نباید خالی باشند و باید با هم مطابقت داشته باشند.")
        } else {
            makeRequest(
                scope = viewModelScope,
                request = {
                    restPasswordRepository.changepassword(
                        ChangePasswordRequest(
                            oldPassword = password,
                            newPassword = newPassword,
                        )
                    )
                },
                onSuccess = { response ->
                    viewModelScope.launch {
                        response?.data?.let {
                            Timber.tag("RestPasswordViewModel")
                                .d("restPasswordRequest SUCCESS: ${it}  ")
                            updateStateMessage(response.message)
                        }
                    }

                },
                updateStateLoading = { isLoading -> updateStateLoading(isLoading) },
                updateStateError = { errorMessage -> updateStateError(errorMessage) }
            )
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
}