package com.msa.eshop.ui.screen.orderDetailsReport

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msa.eshop.data.Model.ReportCartDetailsResponse
import com.msa.eshop.data.Model.request.ReportHistoryCustomerModelRequest
import com.msa.eshop.data.Model.response.ReportCartDetailsModel
import com.msa.eshop.data.Model.response.SimulateModel
import com.msa.eshop.data.repository.ReportCartDetailsRepository
import com.msa.eshop.utils.result.GeneralStateModel
import com.msa.eshop.utils.result.makeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class OrderDetailsReportViewModel @Inject constructor(
    private val reportCartDetailsRepository: ReportCartDetailsRepository
):ViewModel(){




    private val _state: MutableStateFlow<GeneralStateModel> = MutableStateFlow(GeneralStateModel())
    val state: StateFlow<GeneralStateModel> = _state


    private val _reportCartDetails=MutableStateFlow<List<ReportCartDetailsModel>>(emptyList())
    val reportCartDetails :StateFlow<List<ReportCartDetailsModel>> = _reportCartDetails
     fun reportCartDetailsRequest(
        cartCode : Int
    ){
        makeRequest(
            scope = viewModelScope,
            request = {reportCartDetailsRepository.reportCartDetails(cartCode) },
            onSuccess = { response ->
                viewModelScope.launch {
                    response?.data?.let {
                        Timber.tag("OrderDetailsReportViewModel").d("reportCartDetails SUCCESS: ${it}  ")
                        _reportCartDetails.value=it
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