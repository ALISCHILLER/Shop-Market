package com.msa.eshop.data.remote.api


import com.msa.eshop.data.Model.BannerResponse
import com.msa.eshop.data.Model.DiscountResponse
import com.msa.eshop.data.Model.request.InsertCartModelRequest
import com.msa.eshop.data.Model.InsertCartModelResponse
import com.msa.eshop.data.Model.OrderAddressResultModel
import com.msa.eshop.data.Model.ProductGroupResponse
import com.msa.eshop.data.Model.ProductResponse
import com.msa.eshop.data.Model.ReportHistoryCustomerResponse
import com.msa.eshop.data.Model.SimulateResultModel
import com.msa.eshop.data.Model.TokenResponse
import com.msa.eshop.data.Model.UserResponse
import com.msa.eshop.data.Model.request.ReportHistoryCustomerModelRequest
import com.msa.eshop.data.Model.request.SimulateModelRequest
import com.msa.eshop.data.Model.request.TokenRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    companion object {

        const val version = "v1"

    }
    @POST("/api/$version/User/loginUser")
    suspend fun getToken(
        @Body tokenRequest: TokenRequest
    ): Response<TokenResponse?>

    @GET("/api/$version/User/CustomerProfile")
    suspend fun getUserData(): Response<UserResponse?>

    @GET("/api/$version/Product/GetListKala")
    suspend fun getProductData(): Response<ProductResponse?>

    @GET("/api/$version/Product/GetProductCategory")
    suspend fun getProductGroupData(): Response<ProductGroupResponse?>

    @GET("/api/$version/Banner/GetBanner")
    suspend fun getBanner():Response<BannerResponse?>


    @GET("/api/$version/Product/GetListDiscounts")
    suspend fun getListDiscounts(
        @Query("ProductID") prodouctCode : String
    ):Response<DiscountResponse?>

    @POST("/api/$version/Cart/GetCartSimulateRsult")
    suspend fun requestSimulate(
        @Body simulateModelRequest: List<SimulateModelRequest>
    ):Response<SimulateResultModel?>

    @GET("/api/$version/User/GetCustomerAddress")
    suspend fun GetCustomerAddress():Response<OrderAddressResultModel?>

    @POST("/api/$version/Cart/InsertCart")
    suspend fun requestInsertCart(
        @Body InsertCartModelRequest: List<InsertCartModelRequest>
    ):Response<InsertCartModelResponse?>



    @POST("/api/$version/Cart/ReportHistoryCustomer")
    suspend fun reportHistoryCustomer(
        @Body reportHistoryOrder:ReportHistoryCustomerModelRequest
    ):Response<ReportHistoryCustomerResponse?>

}