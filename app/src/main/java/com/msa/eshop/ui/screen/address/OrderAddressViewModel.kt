package com.msa.eshop.ui.screen.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import com.msa.eshop.utils.result.GeneralStateModel
import com.msa.eshop.data.Model.request.InsertCartModelRequest
import com.msa.eshop.data.Model.response.OrderAddressModel
import com.msa.eshop.data.local.entity.OrderEntity
import com.msa.eshop.data.repository.OrderAddressRepository
import com.msa.eshop.ui.navigation.NavInfo
import com.msa.eshop.ui.navigation.NavManager
import com.msa.eshop.ui.navigation.Route
import com.msa.eshop.utils.result.makeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class OrderAddressViewModel @Inject constructor(
    private val orderAddressRepository: OrderAddressRepository,
    private val navManager: NavManager,
):ViewModel(){


    private val _state: MutableStateFlow<GeneralStateModel> = MutableStateFlow(GeneralStateModel())
    val state: StateFlow<GeneralStateModel> = _state


    private val _orderAddress =
        MutableStateFlow<List<OrderAddressModel>>(emptyList())
    val orderAddress: StateFlow<List<OrderAddressModel>> = _orderAddress

    fun clearState() {
        _state.value = GeneralStateModel()
    }
    init {
        OrderAddressRequest()
    }
    fun OrderAddressRequest() {
        makeRequest(
            scope = viewModelScope,
            request = { orderAddressRepository.OrderAddressModelRequest() },
            onSuccess = { response ->

                response?.let {
                    Timber.tag("OrderAddressViewModel").d("OrderAddressRequest SUCCESS: ${it.data} ")
                    it.data?.let { it1 ->  _orderAddress.value = it1}
                    updateStateLoading(false)
                }
            },
            updateStateLoading = { isLoading -> updateStateLoading(isLoading) },
            updateStateError = { errorMessage -> updateStateError(errorMessage) }
        )
    }

    fun getOrderToInsertCart(address:String){
        if (address.isNullOrEmpty()) {
            Timber.tag("OrderAddressViewModel").d("لطفا آدرس را انتخاب کنید ")
            updateStateError("لطفا آدرس را انتخاب کنید")
        } else
        viewModelScope.launch {
            orderAddressRepository.getAllOrder.collect {
                val insertCartModelRequest: List<InsertCartModelRequest> = it.map { it.toInsertCartModelRequest(address) }
                InsertCartRequest(insertCartModelRequest)
            }
        }
    }

     private  fun InsertCartRequest(insertCartModelRequest: List<InsertCartModelRequest> ){
         makeRequest(
             scope = viewModelScope,
             request = {orderAddressRepository.requestInsertCart(insertCartModelRequest) },
             onSuccess = { response ->
                 viewModelScope.launch {
                     response?.data?.let {
                         Timber.tag("OrderAddressViewModel").d("InsertCartRequest SUCCESS: ${it}  ")
                         orderAddressRepository.deleatOrder()
                         updateStateLoading(false)
                         navigateToHome()

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
                    Route.OrderAddressScreen.route,
                    inclusive = true
                ).build()
            )
        )
    }


    fun OrderEntity.toInsertCartModelRequest(address:String): InsertCartModelRequest {
        return InsertCartModelRequest(
            productCode = this.productCode,
            quantity = this.numberOrder,
            customerAddressId = address
        )
    }

    private fun updateStateLoading(isLoading: Boolean) {
        _state.value = _state.value.copy(isLoading = isLoading, error = null)
    }

    private fun updateStateError(errorMessage: String?) {
        _state.update { it.copy(isLoading = false, error = errorMessage) }
    }
}