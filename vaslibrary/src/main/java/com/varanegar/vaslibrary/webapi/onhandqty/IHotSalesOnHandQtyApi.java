package com.varanegar.vaslibrary.webapi.onhandqty;

import com.varanegar.vaslibrary.model.onhandqty.OnHandQtyModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 3/26/2018.
 */

public interface IHotSalesOnHandQtyApi {
    @GET("api/v2/ngt/product/hotsaleonhandqty/sync/loaddata")
    Call<List<OnHandQtyModel>> getAll(@Query("dealerId") String dealerId, @Query("tourId") String tourId);
    @GET("api/v2/ngt/product/renewonhandqty/sync/loaddata")
    Call<List<OnHandQtyModel>> renew(@Query("dealerId") String dealerId, @Query("tourId") String tourId);
}
