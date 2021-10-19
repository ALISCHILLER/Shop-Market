package com.varanegar.vaslibrary.webapi.rs;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IRecommenderSystemApi {
    @GET("api/v2/ngt/setting/extra")
    Call<RecSysConfig> getConfigs();

    @GET("api/recommended/product")
    Call<List<RecommendedProductModel>> getRecommendedProducts(@Query("customerUniqueId") UUID customerId) throws RecSysException;
}
