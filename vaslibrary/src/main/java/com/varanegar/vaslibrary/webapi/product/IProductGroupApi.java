package com.varanegar.vaslibrary.webapi.product;

import com.varanegar.vaslibrary.model.productBoGroup.ProductBoGroupModel;
import com.varanegar.vaslibrary.model.productGroup.ProductGroupModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 6/14/2017.
 */

public interface IProductGroupApi
{
    @GET("api/v2/ngt/productgroup/sync/loaddata")
    Call<List<ProductGroupModel>> getAll(@Query("Date") String dateAfter);
}
