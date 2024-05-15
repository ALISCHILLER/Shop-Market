package com.msa.eshop.ui.screen.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msa.eshop.data.Model.GeneralStateModel
import com.msa.eshop.data.local.entity.OrderEntity
import com.msa.eshop.data.local.entity.ProductGroupEntity
import com.msa.eshop.data.local.entity.ProductModelEntity
import com.msa.eshop.data.local.entity.UserModelEntity
import com.msa.eshop.data.remote.utills.Resource
import com.msa.eshop.data.repository.HomeRepository
import com.msa.eshop.ui.navigation.NavManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val navManager: NavManager,
    private val homeRepository: HomeRepository
) : ViewModel() {


    private val _state: MutableStateFlow<GeneralStateModel> = MutableStateFlow(GeneralStateModel())
    val state: StateFlow<GeneralStateModel> = _state

    private val _allProduct =
        MutableStateFlow<List<ProductModelEntity>>(emptyList())
    val allProduct: StateFlow<List<ProductModelEntity>> = _allProduct




    private val _allProductGroup =
        MutableStateFlow<List<ProductGroupEntity>>(emptyList())
    val allProductGroup: StateFlow<List<ProductGroupEntity>> = _allProductGroup


    private val _allOrder =
        MutableStateFlow<List<OrderEntity>>(emptyList())
    val allOrder: StateFlow<List<OrderEntity>> = _allOrder


    private val _user = MutableStateFlow<UserModelEntity?>(null)
    val user: StateFlow<UserModelEntity?> = _user

    fun productCheck(){
        val productCount = homeRepository.getProductCount()
        if (productCount==0) {
            ProductRequest()
            ProductGroupRequest()
        }else{
            getAllOrder()
            getAllProductGroup()
        }
    }


    fun ProductRequest() {
        viewModelScope.launch {
            homeRepository.productRequest().onEach { response ->
                Timber.d(response.data.toString())
                when (response.status) {
                    Resource.Status.SUCCESS -> {
                        Timber.tag("HomeViewModel").d("getToken SUCCESS: ${response.data}  ")
                        val responseData = response.data
                        responseData?.let { data ->
                            if (!data.hasError) {
                                homeRepository.insertProduct(data.data)
                                getAllProduct()
                            } else
                                updateStateError(data.message)
                        }
                    }

                    Resource.Status.LOADING -> {
                        Timber.tag("HomeViewModel").e("ProductRequest LOADING: ")
                        updateStateLoading()
                    }

                    Resource.Status.ERROR -> {
                        Timber.tag("HomeViewModel").e("ProductRequest ERROR: ${response.error}")
                        updateStateError(response.error?.message)
                    }

                }
            }.collect()
        }
    }

    private fun getAllProduct() {
        viewModelScope.launch {
            homeRepository.getAllProduct.collect {
                updateStateLoading(false)
                _allProduct.value = it
            }
        }
    }


    fun ProductGroupRequest() {
        viewModelScope.launch {

            homeRepository.productGroupRequest().onEach { response ->
                Timber.d(response.data.toString())
                when (response.status) {
                    Resource.Status.SUCCESS -> {
                        Timber.tag("HomeViewModel").d("getToken SUCCESS: ${response.data}  ")
                        val responseData = response.data
                        responseData?.let { data ->
                            if (!data.hasError) {
                                homeRepository.insertProductGroup(data.data)
                                delay(1000)
                                getAllProductGroup()
                            } else
                                updateStateError(data.message)
                        }
                    }

                    Resource.Status.LOADING -> {
                        Timber.tag("HomeViewModel").e("ProductRequest LOADING: ")
                        updateStateLoading()
                    }

                    Resource.Status.ERROR -> {
                        Timber.tag("HomeViewModel").e("ProductRequest ERROR: ${response.error}")
                        updateStateError(response.error?.message)

                    }

                }
            }.collect()
        }
    }

    private fun getAllProductGroup() {
        viewModelScope.launch {
            homeRepository.getAllProductGroup.collect {
                Log.e("TAG", "getAllProductGroup: $it")
                updateStateLoading(false)
                _allProductGroup.value = it
            }
        }
    }


    fun getProduct(productGroup: ProductGroupEntity) {
        viewModelScope.launch {
            if (productGroup.productCategoryCode != 0)
                homeRepository.getProduct(productGroup.productCategoryCode).collect {
                    _allProduct.value = it
                }
            else
                homeRepository.getAllProduct.collect {
                    updateStateLoading(false)
                    _allProduct.value = it
                }
        }
    }



    private fun getAllOrder() {
        viewModelScope.launch {
            homeRepository.getAllOrder.collect {
                _allOrder.value = it
            }
        }
    }

    private fun updateStateLoading() {
        _state.value = _state.value.copy(isLoading = true, error = null)
    }

    private fun updateStateLoading(isLoading: Boolean) {
        _state.value = _state.value.copy(isLoading = isLoading, error = null)
    }

    private fun updateStateError(errorMessage: String?) {
        _state.value = _state.value.copy(isLoading = false, error = errorMessage)
    }

    fun searchProduct(search: String) {
        viewModelScope.launch {
            homeRepository.searchProduct(search).collect {
                _allProduct.value = it
            }
        }
    }


    fun calculateTotalPriceAndHandleOrder(
        value1: Int,
        value2: Int,
        productModelEntity: ProductModelEntity
    ): Float {
        val totalValue = calculateTotalValue(value1, value2, productModelEntity)
        updateOrderInDatabase(productModelEntity, totalValue, value1, value2)
        return calculateSalePrice(totalValue, productModelEntity)
    }


    private fun updateOrderInDatabase(
        productModelEntity: ProductModelEntity,
        totalValue: Int,
        value1: Int,
        value2: Int
    ) {
        viewModelScope.launch {
            if (totalValue > 0) {
                insertOrder(productModelEntity, totalValue, value1, value2)
            } else {
                homeRepository.deleteOrder(productModelEntity.id)
            }
        }
    }

    private suspend fun insertOrder(
        productModelEntity: ProductModelEntity,
        totalValue: Int,
        value1: Int,
        value2: Int
    ) {
        homeRepository.insertOrder(
            createOrderEntity(productModelEntity, totalValue, value1, value2)
        )
    }


}


