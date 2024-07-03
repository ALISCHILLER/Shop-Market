package com.msa.eshop.ui.acticity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msa.eshop.data.local.entity.OrderEntity
import com.msa.eshop.data.repository.BasketRepository
import com.msa.eshop.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val main: MainRepository
) : ViewModel() {

    private val _allOrder =
        MutableStateFlow<List<OrderEntity>>(emptyList())
    val allOrder: StateFlow<List<OrderEntity>> = _allOrder


    fun getAllOrder() {
        viewModelScope.launch {
            main.getAllOrder.collect {
                _allOrder.value = it
            }
        }
    }

}