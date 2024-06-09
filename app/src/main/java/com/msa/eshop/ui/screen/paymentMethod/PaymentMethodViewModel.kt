package com.msa.eshop.ui.screen.paymentMethod

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import com.msa.eshop.data.Model.request.InsertCartModelRequest
import com.msa.eshop.data.Model.request.SimulateModelRequest
import com.msa.eshop.data.local.entity.OrderAddressEntity
import com.msa.eshop.data.local.entity.OrderEntity
import com.msa.eshop.data.local.entity.PaymentMethodEntity
import com.msa.eshop.data.repository.PaymentMethodRepository
import com.msa.eshop.ui.navigation.NavInfo
import com.msa.eshop.ui.navigation.NavManager
import com.msa.eshop.ui.navigation.Route
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
class PaymentMethodViewModel @Inject constructor(
    private val paymentMethodRepository: PaymentMethodRepository,
    private val navManager: NavManager,
):ViewModel() {


    private val _state: MutableStateFlow<GeneralStateModel> = MutableStateFlow(GeneralStateModel())
    val state: StateFlow<GeneralStateModel> = _state


    private val _orderAddress =
        MutableStateFlow<List<OrderAddressEntity>>(emptyList())
    val orderAddress: StateFlow<List<OrderAddressEntity>> = _orderAddress


    private val _paymentMethod =
        MutableStateFlow<List<PaymentMethodEntity>>(emptyList())
    val paymentMethod: StateFlow<List<PaymentMethodEntity>> = _paymentMethod
     fun getOrderAddress() {
        viewModelScope.launch {
            paymentMethodRepository.getAllorderAddress.collect{
                Log.e("PaymentMethodViewModel", "getAllProductGroup: $it")
                _orderAddress.value=it
            }
        }
    }

     fun getAllPaymentMethod() {
        viewModelScope.launch {
            paymentMethodRepository.getAllPayment.collect{
                Log.e("PaymentMethodViewModel", "getAllPaymentMethod: $it")
                _paymentMethod.value=it
            }
        }
    }

    fun getOrderToSimulate(addressId:String){
        viewModelScope.launch {
            paymentMethodRepository.getAllOrder.collect {
                val simulateModelRequests: List<InsertCartModelRequest> = it.map { it.toSimulateModelRequest(addressId) }
                InsertCartRequest(simulateModelRequests)
            }
        }
    }

    fun OrderEntity.toSimulateModelRequest(addressId:String): InsertCartModelRequest {
        return InsertCartModelRequest(
            productCode = this.productCode,
            quantity = this.numberOrder,
            customerAddressId = addressId,
            paymentTermId ="16ccab60-279b-410a-90d1-b2673d5d1dd1" ,
        )
    }
    private fun InsertCartRequest(insertCartModelRequest: List<InsertCartModelRequest>) {
        makeRequest(
            scope = viewModelScope,
            request = { paymentMethodRepository.requestInsertCart(insertCartModelRequest) },
            onSuccess = { response ->
                viewModelScope.launch {
                    response?.data?.let {
                        Timber.tag("OrderAddressViewModel").d("InsertCartRequest SUCCESS: ${it}  ")
                        paymentMethodRepository.deleatOrder()
                        navigateToHome()
                        updateStateLoading(false)

                    }
                }

            },
            updateStateLoading = { isLoading -> updateStateLoading(isLoading) },
            updateStateError = { errorMessage -> updateStateError(errorMessage) }
        )
    }

    fun navigateToHome() {
        navManager.navigate(
            NavInfo(
                id = Route.HomeScreen.route,
                navOption = NavOptions.Builder().setPopUpTo(
                    Route.PaymentMethodScreen.route,
                    inclusive = true
                ).build()
            )
        )
    }

    private fun updateStateLoading(isLoading: Boolean) {
        _state.value = _state.value.copy(isLoading = isLoading, error = null)
    }

    private fun updateStateError(errorMessage: String?) {
        _state.update { it.copy(isLoading = false, error = errorMessage) }
    }

}