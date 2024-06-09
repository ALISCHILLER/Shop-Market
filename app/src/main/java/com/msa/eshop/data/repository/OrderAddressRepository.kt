package com.msa.eshop.data.repository

import com.msa.eshop.data.Model.request.InsertCartModelRequest
import com.msa.eshop.data.Model.InsertCartModelResponse
import com.msa.eshop.data.Model.OrderAddressResultModel
import com.msa.eshop.data.local.dao.OrderAddressDao
import com.msa.eshop.data.local.dao.OrderDao
import com.msa.eshop.data.local.entity.OrderAddressEntity
import com.msa.eshop.data.local.entity.OrderEntity
import com.msa.eshop.data.remote.api.ApiService
import com.msa.eshop.data.remote.utills.MakeSafeApiCall
import com.msa.eshop.data.remote.utills.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OrderAddressRepository @Inject constructor(
    private val apiService: ApiService,
    private val apiManager: MakeSafeApiCall,
    private val  orderDao: OrderDao,
    private val  orderAddressDao: OrderAddressDao

    ) {
    val getAllOrder: Flow<List<OrderEntity>> = orderDao.getAll()
    suspend fun OrderAddressModelRequest(
    ): Flow<Resource<OrderAddressResultModel?>> {
        return apiManager.makeSafeApiCall {
            withContext(Dispatchers.IO){
                apiService.GetCustomerAddress()
            }
        }as Flow<Resource<OrderAddressResultModel?>>
    }

    suspend fun requestInsertCart(
        insertCart : List<InsertCartModelRequest>
    ): Flow<Resource<InsertCartModelResponse?>> {
        return apiManager.makeSafeApiCall {
            withContext(Dispatchers.IO){
                apiService.requestInsertCart(insertCart)
            }
        }as Flow<Resource<InsertCartModelResponse?>>
    }

    fun deleatOrder(){
        orderDao.delete()
    }

    fun insertOrderAddress(orderAddres: OrderAddressEntity){
        orderAddressDao.delete()
        orderAddressDao.insert(orderAddres)
    }
}