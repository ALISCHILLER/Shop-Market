package com.varanegar.vaslibrary.webapi.city;

import com.varanegar.vaslibrary.model.city.CityModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 3/25/2018.
 */

public interface ICityApi {
    @GET("api/v2/ngt/city/sync/loaddata")
    Call<List<CityModel>> getCities(@Query("Date") String dateAfter);
}
