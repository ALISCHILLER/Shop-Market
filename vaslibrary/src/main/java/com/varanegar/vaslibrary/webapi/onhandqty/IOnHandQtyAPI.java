package com.varanegar.vaslibrary.webapi.onhandqty;

import com.varanegar.vaslibrary.model.onhandqty.OnHandQtyModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 7/10/2017.
 */

public interface IOnHandQtyAPI {
    @GET("api/v2/ngt/product/onhandqty/sync/loaddata")
    Call<List<OnHandQtyModel>> getAll(@Query("Date") String dateAfter, @Query("dealerId") String dealerId);
}
