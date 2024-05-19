package com.msa.eshop.ui.screen.simulate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msa.eshop.data.Model.GeneralStateModel
import com.msa.eshop.data.Model.SimulateResultModel
import com.msa.eshop.data.repository.SimulateRepository
import com.msa.eshop.data.request.SimulateModelRequest
import com.msa.eshop.utils.makeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SimulateViewModel @Inject constructor(
    private val simulateRepository: SimulateRepository
): ViewModel(){


    private val _simulate =MutableStateFlow<List<SimulateResultModel>>(emptyList())
    val simulat :StateFlow<List<SimulateResultModel>> = _simulate
    private val _state: MutableStateFlow<GeneralStateModel> = MutableStateFlow(GeneralStateModel())
    val state: StateFlow<GeneralStateModel> = _state

    fun productRequest(simulateModel : List<SimulateModelRequest>) {
        makeRequest(
            scope = viewModelScope,
            request = {simulateRepository.SimulateModelRequest(simulateModel) },
            onSuccess = { response ->
                viewModelScope.launch {
                    response?.data?.let {

                    }
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
        _state.value = _state.value.copy(isLoading = false, error = errorMessage)
    }
}