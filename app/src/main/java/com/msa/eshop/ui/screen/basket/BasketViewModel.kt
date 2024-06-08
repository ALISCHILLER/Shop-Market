package com.msa.eshop.ui.screen.basket

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import com.msa.eshop.data.local.entity.OrderEntity
import com.msa.eshop.data.local.entity.ProductModelEntity
import com.msa.eshop.data.repository.BasketRepository
import com.msa.eshop.data.repository.HomeRepository
import com.msa.eshop.ui.navigation.NavInfo
import com.msa.eshop.ui.navigation.NavManager
import com.msa.eshop.ui.navigation.Route
import com.msa.eshop.utils.calculateSalePrice
import com.msa.eshop.utils.calculateTotalValue
import com.msa.eshop.utils.createOrderEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BasketViewModel @Inject constructor(
    private val navManager: NavManager,
    private val basketRepository: BasketRepository,
):ViewModel(){


    private val _allOrder =
        MutableStateFlow<List<OrderEntity>>(emptyList())
    val allOrder: StateFlow<List<OrderEntity>> = _allOrder
    init {
        getAllOrder()
    }





    fun getAllOrder() {
        viewModelScope.launch {
            basketRepository.getAllOrder.collect {
                _allOrder.value = it
            }
        }
    }


    fun deleteOrder(orderId: String){
        viewModelScope.launch {
            basketRepository.deleteOrder(orderId)
        }
    }

    fun calculateTotalPriceAndHandleOrder(
        value1: Int,
        value2: Int,
        orderEntity: OrderEntity,
    ): Float{
        val totalValue = calculateTotalValue(value1, value2, orderEntity.convertFactor2 ?: 0)
        updateOrderInDatabase(orderEntity, totalValue, value1, value2)
        return orderEntity.let { calculateSalePrice(totalValue, it.price) }
    }


    private fun updateOrderInDatabase(
        orderEntity: OrderEntity,
        totalValue: Int,
        value1: Int,
        value2: Int
    ) {
        viewModelScope.launch {
            if (totalValue > 0) {
                insertOrder(orderEntity, totalValue, value1, value2)
            } else {
                basketRepository.deleteOrder(orderEntity.id)
            }
        }
    }

    private suspend fun insertOrder(
        orderEntity: OrderEntity,
        totalValue: Int,
        value1: Int,
        value2: Int
    ) {
        orderEntity.numberOrder = totalValue
        orderEntity.numberOrder1 = value1
        orderEntity.numberOrder2 = value2
        basketRepository.insertOrder(orderEntity) // اضافه کردن خط ذخیره سازی
    }


    fun navigateToSimulate() {
        navManager.navigate(
            NavInfo(id = Route.SimulateScreen.route,
                navOption = NavOptions.Builder().setPopUpTo(
                    Route.BasketScreen.route,
                    inclusive = true).build())
        )
    }


}