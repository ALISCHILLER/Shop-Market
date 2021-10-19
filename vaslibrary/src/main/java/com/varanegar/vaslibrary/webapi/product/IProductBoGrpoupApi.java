package com.varanegar.vaslibrary.webapi.product;

import com.varanegar.vaslibrary.model.priceHistory.PriceHistoryModel;
import com.varanegar.vaslibrary.model.productBoGroup.ProductBoGroupModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 6/13/2017.
 */

public interface IProductBoGrpoupApi
{
    @GET("api/v2/ngt/productbogroup/sync/loaddata")
    Call<List<ProductBoGroupModel>> getAll(@Query("Date") String dateAfter);
}
