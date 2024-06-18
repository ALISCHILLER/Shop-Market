package com.msa.eshop.ui.screen.simulate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import com.msa.eshop.utils.result.GeneralStateModel
import com.msa.eshop.data.Model.response.SimulateModel
import com.msa.eshop.data.local.entity.OrderEntity
import com.msa.eshop.data.repository.SimulateRepository
import com.msa.eshop.data.Model.request.SimulateModelRequest
import com.msa.eshop.data.local.entity.PaymentMethodEntity
import com.msa.eshop.ui.navigation.NavInfo
import com.msa.eshop.ui.navigation.NavManager
import com.msa.eshop.ui.navigation.Route
import com.msa.eshop.utils.result.makeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class SimulateViewModel @Inject constructor(
    private val simulateRepository: SimulateRepository,
    private val navManager: NavManager,
): ViewModel(){

    init {
        getOrderToSimulate()
    }

    private val _state: MutableStateFlow<GeneralStateModel> = MutableStateFlow(GeneralStateModel())
    val state: StateFlow<GeneralStateModel> = _state

    private val _simulate =MutableStateFlow<List<SimulateModel>>(emptyList())
    val simulat :StateFlow<List<SimulateModel>> = _simulate

    private val _order =MutableStateFlow<List<OrderEntity>>(emptyList())
    val order :StateFlow<List<OrderEntity>> = _order

    fun getOrderToSimulate(){
        viewModelScope.launch {
            simulateRepository.getAllOrder.collect {
                val simulateModelRequests: List<SimulateModelRequest> = it.map { it.toSimulateModelRequest() }
                productRequest(simulateModelRequests)
            }
        }
    }

    fun productRequest(simulateModel : List<SimulateModelRequest>) {
        makeRequest(
            scope = viewModelScope,
            request = {simulateRepository.SimulateModelRequest(simulateModel) },
            onSuccess = { response ->
                viewModelScope.launch {
                    response?.data?.let {
                        Timber.tag("SimulateViewModel").d("productRequest SUCCESS: ${it}  ")
                        _simulate.value = it
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

    fun OrderEntity.toSimulateModelRequest(): SimulateModelRequest {
        return SimulateModelRequest(
            productCode = this.productCode,
            quantity = this.numberOrder
        )
    }


    fun savePayment(pyment: PaymentMethodEntity){
        simulateRepository.insertTopayment(pyment)
        navigateToOrderAddress()
    }
    fun navigateToOrderAddress() {
        navManager.navigate(
            NavInfo(
                id = Route.OrderAddressScreen.route,
                navOption = NavOptions.Builder().setPopUpTo(
                    Route.SimulateScreen.route,
                    inclusive = false
                ).build()
            )
        )
    }
}