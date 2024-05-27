package com.msa.eshop.ui.screen.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msa.eshop.data.Model.BannerModel
import com.msa.eshop.data.Model.DiscountResultModel
import com.msa.eshop.data.Model.GeneralStateModel
import com.msa.eshop.data.local.entity.OrderEntity
import com.msa.eshop.data.local.entity.ProductGroupEntity
import com.msa.eshop.data.local.entity.ProductModelEntity
import com.msa.eshop.data.local.entity.UserModelEntity
import com.msa.eshop.data.repository.HomeRepository
import com.msa.eshop.ui.navigation.NavManager
import com.msa.eshop.utils.calculateSalePrice
import com.msa.eshop.utils.calculateTotalValue
import com.msa.eshop.utils.createOrderEntity
import com.msa.eshop.utils.makeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val _banner = MutableStateFlow<List<BannerModel>>(emptyList())
    val banner: StateFlow<List<BannerModel>> = _banner

    private val _discount = MutableStateFlow<List<DiscountResultModel>>(emptyList())
    val discount: StateFlow<List<DiscountResultModel>> = _discount

    fun productCheck() {
//        val productCount = homeRepository.getProductCount()
//        if (productCount == 0) {
//            productRequest()
//            productGroupRequest()
//        } else {
//            getAllOrder()
//            getAllProduct()
//            getAllProductGroup()
//        }

        productRequest()
        productGroupRequest()
        getAllOrder()
        Bannerrequest()
    }


    fun productRequest() {
        makeRequest(
            scope = viewModelScope,
            request = { homeRepository.productRequest() },
            onSuccess = { response ->
                viewModelScope.launch {
                    response?.data?.let {
                        homeRepository.insertProduct(it)
                        delay(1000)
                        getAllProduct()
                    }
                }

            },
            updateStateLoading = { isLoading ->
                if(isLoading)
                updateStateLoading(isLoading)
                                 },
            updateStateError = { errorMessage -> updateStateError(errorMessage) }
        )
    }


    fun productGroupRequest() {
        makeRequest(
            scope = viewModelScope,
            request = { homeRepository.productGroupRequest() },
            onSuccess = { response ->
                viewModelScope.launch {
                    response?.data?.let { homeRepository.insertProductGroup(it) }
                    delay(100)
                    getAllProductGroup()
                }


            },
            updateStateLoading = { isLoading ->
                if(isLoading)
                updateStateLoading(isLoading)
                                 },
            updateStateError = { errorMessage -> updateStateError(errorMessage) }
        )
    }


    fun Bannerrequest() {
        makeRequest(
            scope = viewModelScope,
            request = { homeRepository.requestBanner() },
            onSuccess = { response ->
                response?.data?.let {
                    Timber.tag("HomeViewModel").d("Bannerrequest SUCCESS: ${it}  ")
                    _banner.value = it
                }
            },
            updateStateLoading = { isLoading -> updateStateLoading(isLoading) },
            updateStateError = { errorMessage -> updateStateError(errorMessage) }
        )
    }

    fun discountRequest(productCode: String) {
        makeRequest(
            scope = viewModelScope,
            request = { homeRepository.requestDiscount(productCode) },
            onSuccess = { response ->
                response.data?.let {
                    Timber.tag("HomeViewModel").d("discountRequest SUCCESS: ${it}  ")
                    _discount.value = it
                }

            },
            updateStateLoading = { isLoading -> updateStateLoading(isLoading) },
            updateStateError = { errorMessage -> updateStateError(errorMessage) },
        )
    }


    private fun updateStateLoading(isLoading: Boolean) {
        _state.value = _state.value.copy(isLoading = isLoading, error = null)
    }

    private fun updateStateError(errorMessage: String?) {
        _state.value = _state.value.copy(isLoading = false, error = errorMessage)
    }


    fun getProduct(productGroup: ProductGroupEntity) {
        viewModelScope.launch {
            if (productGroup.productCategoryCode != 99)
                homeRepository.getProduct(productGroup.productCategoryCode).collect {
                    _allProduct.value = it
                }
            else
                getAllProduct()
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

    private fun getAllProduct() {
        viewModelScope.launch {
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
        val totalValue = calculateTotalValue(value1, value2, productModelEntity.convertFactor2)
        updateOrderInDatabase(productModelEntity, totalValue, value1, value2)
        return calculateSalePrice(totalValue, productModelEntity.price)
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


