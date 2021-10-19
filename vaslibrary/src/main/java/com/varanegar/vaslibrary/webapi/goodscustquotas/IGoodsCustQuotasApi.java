package com.varanegar.vaslibrary.webapi.goodscustquotas;

import com.varanegar.vaslibrary.model.goodscustquotas.GoodsCustQuotasModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 11/19/2018.
 */

public interface IGoodsCustQuotasApi {
    @GET("api/v2/ngt/product/customergoodsquota/sync/loaddata")
    Call<List<GoodsCustQuotasModel>> getGoodsCustQuotas(@Query("dealerId") String dealerId);
}
