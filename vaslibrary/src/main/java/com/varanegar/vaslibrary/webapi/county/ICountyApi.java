package com.varanegar.vaslibrary.webapi.county;

import com.varanegar.vaslibrary.model.county.CountyModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 10/14/2018.
 */

public interface ICountyApi {
    @GET("api/v2/ngt/county/sync/loadData")
    Call<List<CountyModel>> getCounties(@Query("Date") String dateAfter);
}
