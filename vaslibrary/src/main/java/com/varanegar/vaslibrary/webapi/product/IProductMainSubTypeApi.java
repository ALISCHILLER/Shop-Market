package com.varanegar.vaslibrary.webapi.product;
import com.varanegar.vaslibrary.model.productMainSubType.ProductMainSubTypeModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 6/20/2017.
 */

public interface IProductMainSubTypeApi
{
    @GET("api/v2/ngt/product/mainsubtype/sync/loaddata")
    Call<List<ProductMainSubTypeModel>> getAll(@Query("Date") String dateAfter);
}
