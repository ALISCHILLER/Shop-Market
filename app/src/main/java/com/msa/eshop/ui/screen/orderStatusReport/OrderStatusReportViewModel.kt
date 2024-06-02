package com.msa.eshop.ui.screen.orderStatusReport

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msa.eshop.data.Model.request.ReportHistoryCustomerModelRequest
import com.msa.eshop.data.Model.response.ReportHistoryCustomerModel
import com.msa.eshop.data.repository.OrderStatusReportRepository
import com.msa.eshop.utils.result.GeneralStateModel
import com.msa.eshop.utils.result.makeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class OrderStatusReportViewModel @Inject constructor(
    private val orderStatusReportRepository: OrderStatusReportRepository
):ViewModel(){


    private val _orderStatusReport  = MutableStateFlow<List<ReportHistoryCustomerModel>>(emptyList())

    val orderStatusReport :StateFlow<List<ReportHistoryCustomerModel>> = _orderStatusReport


    private val _state: MutableStateFlow<GeneralStateModel> = MutableStateFlow(GeneralStateModel())
    val state: StateFlow<GeneralStateModel> = _state


    init {
        getUser()
    }
    fun getUser(){
        viewModelScope.launch {
          orderStatusReportRepository.getUser.collect{
              val reportHistoryOrder = ReportHistoryCustomerModelRequest(
                  customerId = it.id,
                  fromDate = "",
                  endDate = "",

              )
              reportHistoryOrderRequest(reportHistoryOrder)
          }
        }
    }

    private fun reportHistoryOrderRequest(
        reportHistoryOrder: ReportHistoryCustomerModelRequest
    ){
        makeRequest(
            scope = viewModelScope,
            request = {orderStatusReportRepository.reportHistoryOrder(reportHistoryOrder) },
            onSuccess = { response ->
                viewModelScope.launch {
                    response?.data?.let {
                        Timber.tag("SimulateViewModel").d("reportHistoryOrderRequest SUCCESS: ${it}  ")
                        _orderStatusReport.value = it
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