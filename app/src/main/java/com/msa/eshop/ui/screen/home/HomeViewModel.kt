package com.msa.eshop.ui.screen.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msa.eshop.data.Model.GeneralStateModel
import com.msa.eshop.data.local.entity.ProductGroupEntity
import com.msa.eshop.data.local.entity.ProductModelEntity
import com.msa.eshop.data.remote.utills.Resource
import com.msa.eshop.data.repository.HomeRepository
import com.msa.eshop.ui.navigation.NavManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
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

    private val _allProductGroup =
        MutableStateFlow<List<ProductGroupEntity>>(emptyList())
    val allProductGroup: StateFlow<List<ProductGroupEntity>> = _allProductGroup

    private val _allPreoduct =
        MutableStateFlow<List<ProductModelEntity>>(emptyList())
    val allProduct: StateFlow<List<ProductModelEntity>> = _allPreoduct


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
            }
        }
    }

    private fun getAllProduct() {
        viewModelScope.launch {
            homeRepository.getAllProduct.collect {
                updateStateLoading(false)
                _allPreoduct.value = it
            }
        }
    }
    private fun getAllProductGroup() {
        viewModelScope.launch {
            homeRepository.getAllProductGroup.collect {
                Log.e("TAG", "getAllProductGroup: $it", )
                updateStateLoading(false)
                _allProductGroup.value = it
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
}


