package com.msa.eshop.data.repository

import com.msa.eshop.data.Model.ReportHistoryCustomerResponse
import com.msa.eshop.data.Model.SimulateResultModel
import com.msa.eshop.data.Model.request.ReportHistoryCustomerModelRequest
import com.msa.eshop.data.Model.request.SimulateModelRequest
import com.msa.eshop.data.local.dao.UserDao
import com.msa.eshop.data.local.entity.UserModelEntity
import com.msa.eshop.data.remote.api.ApiService
import com.msa.eshop.data.remote.utills.MakeSafeApiCall
import com.msa.eshop.data.remote.utills.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OrderStatusReportRepository @Inject constructor(
    private val apiService: ApiService,
    private val apiManager: MakeSafeApiCall,
    private val userDao: UserDao
) {

    val getUser : Flow<UserModelEntity> = userDao.getUserLogin()
    suspend fun reportHistoryOrder(
        reportHistoryOrder:ReportHistoryCustomerModelRequest
    ): Flow<Resource<ReportHistoryCustomerResponse?>> {
        return apiManager.makeSafeApiCall {
            withContext(Dispatchers.IO){
                apiService.reportHistoryCustomer(reportHistoryOrder)
            }
        }as Flow<Resource<ReportHistoryCustomerResponse?>>
    }



}