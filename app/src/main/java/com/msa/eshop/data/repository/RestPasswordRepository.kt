package com.msa.eshop.data.repository

import com.msa.eshop.data.Model.ChangePasswordResponse
import com.msa.eshop.data.Model.ReportHistoryCustomerResponse
import com.msa.eshop.data.Model.request.ChangePasswordRequest
import com.msa.eshop.data.Model.request.ReportHistoryCustomerModelRequest
import com.msa.eshop.data.remote.api.ApiService
import com.msa.eshop.data.remote.utills.MakeSafeApiCall
import com.msa.eshop.data.remote.utills.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RestPasswordRepository @Inject constructor(
    private val apiService: ApiService,
    private val apiManager: MakeSafeApiCall,
) {
    suspend fun changepassword(
        changePasswordRequest: ChangePasswordRequest
    ): Flow<Resource<ChangePasswordResponse?>> {
        return apiManager.makeSafeApiCall {
            withContext(Dispatchers.IO){
                apiService.changepassword(changePasswordRequest)
            }
        }as Flow<Resource<ChangePasswordResponse?>>
    }

}