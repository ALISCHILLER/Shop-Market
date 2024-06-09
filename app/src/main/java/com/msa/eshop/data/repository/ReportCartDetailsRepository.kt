package com.msa.eshop.data.repository

import com.msa.eshop.data.Model.ReportCartDetailsResponse
import com.msa.eshop.data.Model.ReportHistoryCustomerResponse
import com.msa.eshop.data.Model.request.ReportHistoryCustomerModelRequest
import com.msa.eshop.data.remote.api.ApiService
import com.msa.eshop.data.remote.utills.MakeSafeApiCall
import com.msa.eshop.data.remote.utills.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ReportCartDetailsRepository @Inject constructor(
    private val apiService: ApiService,
    private val apiManager: MakeSafeApiCall,
) {

    suspend fun reportCartDetails(
        cartCode : Int
    ): Flow<Resource<ReportCartDetailsResponse?>> {
        return apiManager.makeSafeApiCall {
            withContext(Dispatchers.IO){
                apiService.ReportCartDetails(cartCode)
            }
        }as Flow<Resource<ReportCartDetailsResponse?>>
    }

}