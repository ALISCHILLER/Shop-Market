package com.msa.eshop.data.repository

import com.msa.eshop.data.local.dao.OrderDao
import com.msa.eshop.data.local.entity.OrderEntity
import com.msa.eshop.data.remote.api.ApiService
import com.msa.eshop.data.remote.utills.MakeSafeApiCall
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BasketRepository @Inject constructor(
    private val apiService: ApiService,
    private val apiManager: MakeSafeApiCall,
    private val orderDao: OrderDao,

) {


    val getAllOrder: Flow<List<OrderEntity>> = orderDao.getAll()



    suspend fun insertOrder(orderEntity: OrderEntity){
        orderDao.insert(orderEntity)
    }

    suspend fun deleteOrder(orderId: String){
        orderDao.deleteOrder(orderId)
    }
}