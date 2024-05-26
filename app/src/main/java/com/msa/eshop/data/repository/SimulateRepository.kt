package com.msa.eshop.data.repository

import com.msa.eshop.data.Model.ProductResponse
import com.msa.eshop.data.Model.SimulateResultModel
import com.msa.eshop.data.local.dao.OrderDao
import com.msa.eshop.data.local.dao.ProductDao
import com.msa.eshop.data.local.entity.OrderEntity
import com.msa.eshop.data.remote.api.ApiService
import com.msa.eshop.data.remote.utills.MakeSafeApiCall
import com.msa.eshop.data.remote.utills.Resource
import com.msa.eshop.data.request.SimulateModelRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SimulateRepository @Inject constructor(
    private val apiService: ApiService,
    private val apiManager: MakeSafeApiCall,
    private val orderDao: OrderDao,

) {
    val getAllOrder: Flow<List<OrderEntity>> = orderDao.getAll()
    suspend fun SimulateModelRequest(
        simulateModel : List<SimulateModelRequest>
    ): Flow<Resource<SimulateResultModel?>> {
        return apiManager.makeSafeApiCall {
            withContext(Dispatchers.IO){
                apiService.requestSimulate(simulateModel)
            }
        }as Flow<Resource<SimulateResultModel?>>
    }




}