package com.varanegar.vaslibrary.webapi.discount;

import com.varanegar.vaslibrary.model.discountSDS.DiscountConditionModel;
import com.varanegar.vaslibrary.model.discountSDS.DiscountItemCountModel;
import com.varanegar.vaslibrary.model.discountSDS.DiscountSDSModel;
import com.varanegar.vaslibrary.model.discountSDS.DiscountVnLtModel;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 6/24/2017.
 */

public interface IDiscountApi
{
//    @GET("api/v2/ngt/discount/sync/loaddata")
//    Call<List<DiscountSDSModel>> getDiscountSDS(@Query("Date") String dateAfter);

    @GET("api/v2/ngt/discountvnlite/sync/loaddata")
    Call<List<DiscountVnLtModel>> getDiscountVnLite(@Query("Date") String dateAfter);

//    @GET("api/v2/ngt/discount/discountcondition")
//    Call<List<DiscountConditionModel>> getDiscountConditions(@Query("Date") String dateAfter);

//    @GET("api/v2/ngt/discount/discountitemcount")
//    Call<List<DiscountItemCountModel>> getDiscountItemCounts(@Query("Date") String dateAfter);
}
