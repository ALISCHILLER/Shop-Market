package com.msa.eshop.ui.screen.basket

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msa.eshop.data.local.entity.OrderEntity
import com.msa.eshop.data.local.entity.ProductModelEntity
import com.msa.eshop.data.repository.HomeRepository
import com.msa.eshop.ui.navigation.NavManager
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
    private val homeRepository: HomeRepository
):ViewModel(){

    init {
        getAllOrder()
        getAllProduct()
    }

    private val _allOrder =
        MutableStateFlow<List<OrderEntity>>(emptyList())
    val allOrder: StateFlow<List<OrderEntity>> = _allOrder

    private val _allProduct =
        MutableStateFlow<List<ProductModelEntity>>(emptyList())
    val allProduct: StateFlow<List<ProductModelEntity>> = _allProduct

    fun getAllOrder() {
        viewModelScope.launch {
            homeRepository.getAllOrder.collect {
                _allOrder.value = it
            }
        }
    }



    private fun getAllProduct() {
        viewModelScope.launch {
            homeRepository.getAllProduct.collect {
                _allProduct.value = it
            }
        }
    }
    fun deleteOrder(orderId: String){
        viewModelScope.launch {
            homeRepository.deleteOrder(orderId)
        }
    }

    fun calculateTotalPriceAndHandleOrder(
        value1: Int,
        value2: Int,
        productModelEntity: ProductModelEntity?
    ): Float {
        val totalValue = calculateTotalValue(value1, value2, productModelEntity)
        updateOrderInDatabase(productModelEntity, totalValue, value1, value2)
        return calculateSalePrice(totalValue, productModelEntity)
    }


    private fun updateOrderInDatabase(
        productModelEntity: ProductModelEntity?,
        totalValue: Int,
        value1: Int,
        value2: Int
    ) {
        viewModelScope.launch {
            if (totalValue > 0) {
                insertOrder(productModelEntity, totalValue, value1, value2)
            } else {
                productModelEntity?.let { homeRepository.deleteOrder(it.id) }
            }
        }
    }

    private suspend fun insertOrder(
        productModelEntity: ProductModelEntity?,
        totalValue: Int,
        value1: Int,
        value2: Int
    ) {
        productModelEntity?.let { createOrderEntity(it, totalValue, value1, value2) }?.let {
            homeRepository.insertOrder(
                it
            )
        }
    }

}