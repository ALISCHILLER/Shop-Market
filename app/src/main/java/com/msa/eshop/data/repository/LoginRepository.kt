package com.msa.eshop.data.repository


import com.msa.eshop.data.Model.TokenResponse
import com.msa.eshop.data.Model.UserResponse
import com.msa.eshop.data.local.dao.UserDao
import com.msa.eshop.data.local.entity.UserModelEntity
import com.msa.eshop.data.remote.api.ApiService
import com.msa.eshop.data.remote.utills.MakeSafeApiCall
import com.msa.eshop.data.remote.utills.Resource
import com.msa.eshop.data.request.TokenRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val apiService: ApiService,
    private val apiManager: MakeSafeApiCall,
    private val userDao: UserDao
) {

    suspend fun loginToken(
        tokenRequest: TokenRequest
    ): Flow<Resource<TokenResponse?>> {
        return apiManager.makeSafeApiCall {
            withContext(Dispatchers.IO) {
                apiService.getToken(
                    tokenRequest
                )
            }
        } as Flow<Resource<TokenResponse?>>
    }
    suspend fun getUserData(
    ): Flow<Resource<UserResponse?>> {
        return apiManager.makeSafeApiCall {
            withContext(Dispatchers.IO) {
                apiService.getUserData()

            }
        } as Flow<Resource<UserResponse?>>
    }


    fun insertUser(user: UserModelEntity) {
        userDao.deleteUserLogin()
        userDao.insertUserLogin(user)
    }
}