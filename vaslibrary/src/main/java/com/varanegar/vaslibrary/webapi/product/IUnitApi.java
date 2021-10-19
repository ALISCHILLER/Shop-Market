package com.varanegar.vaslibrary.webapi.product;

import com.varanegar.vaslibrary.model.product.ProductModel;
import com.varanegar.vaslibrary.model.unit.UnitModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 6/26/2017.
 */

public interface IUnitApi
{
    @GET("api/v2/ngt/unit/sync/loaddata")
    Call<List<UnitModel>> getAll(@Query("Date") String dateAfter);
}
