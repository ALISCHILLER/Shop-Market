package com.varanegar.vaslibrary.webapi.retsaleitem;

import com.varanegar.vaslibrary.model.retsaleitem.RetSaleItemModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 3/3/2019.
 */

public interface IRetSaleItemApi {
    @GET("api/v2/ngt/discount/RetSaleItem")
    Call<List<RetSaleItemModel>> getRetSaleItem(@Query("Date") String Date);
}
