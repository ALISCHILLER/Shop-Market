package com.varanegar.vaslibrary.webapi.price;

import com.varanegar.vaslibrary.model.priceHistory.PriceHistoryModel;
import com.varanegar.vaslibrary.model.user.UserModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 6/12/2017.
 */

public interface IPriceHistoryApi
{
    @GET("api/v2/ngt/price/priceHistory")
    Call<List<PriceHistoryModel>> getAll(@Query("Date") String dateAfter);
}
