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

    companion object {

        const val version = "v1"

    }
    @POST("/api/$version/User/loginUser")
    suspend fun getToken(
        @Body tokenRequest: TokenRequest
    ): Response<TokenModel?>

    @GET("/api/$version/User/CustomerProfile")
    suspend fun getUserData(): Response<UserModel?>

    @GET("/api/$version/Product/GetListKala")
    suspend fun getProductData(): Response<ProductModel?>

    @GET("/api/$version/Product/GetProductCategory")
    suspend fun getProductGroupData(): Response<ProductGroupModel?>
}