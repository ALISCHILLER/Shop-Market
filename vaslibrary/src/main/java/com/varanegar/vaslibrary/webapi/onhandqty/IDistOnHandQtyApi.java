package com.varanegar.vaslibrary.webapi.onhandqty;

import com.varanegar.vaslibrary.model.onhandqty.OnHandQtyModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Torabi on 9/23/2018.
 */

public interface IDistOnHandQtyApi {
    @GET("api/v2/ngt/StockLevel/Dist/OnHandQty")
    Call<List<OnHandQtyModel>> getAll(@Query("tourId") String tourId);
}
