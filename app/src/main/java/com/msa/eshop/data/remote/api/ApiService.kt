package com.msa.eshop.data.remote.api

import com.msa.eshop.data.Model.ProductGroupModel
import com.msa.eshop.data.Model.ProductModel
import com.msa.eshop.data.Model.TokenModel
import com.msa.eshop.data.Model.UserModel
import com.msa.eshop.data.request.TokenRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {


    @POST("")
    suspend fun getToken(
        @Body tokenRequest: TokenRequest
    ): Response<TokenModel?>

    @GET("")
    suspend fun getUserData(): Response<UserModel?>

    @GET("")
    suspend fun getProductData(): Response<ProductModel?>

    @GET("")
    suspend fun getProductGroupData(): Response<ProductGroupModel?>
}