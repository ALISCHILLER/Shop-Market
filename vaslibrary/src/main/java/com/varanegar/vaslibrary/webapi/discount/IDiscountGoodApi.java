package com.varanegar.vaslibrary.webapi.discount;

import com.varanegar.vaslibrary.model.discoungood.DiscountGoodModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 3/3/2019.
 */

public interface IDiscountGoodApi {
    @GET("api/v2/ngt/discount/discountgood")
    Call<List<DiscountGoodModel>> getDiscountGood(@Query("Date") String Date);
}
