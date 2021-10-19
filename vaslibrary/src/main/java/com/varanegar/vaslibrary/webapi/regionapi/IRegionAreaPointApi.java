package com.varanegar.vaslibrary.webapi.regionapi;

import com.varanegar.vaslibrary.model.RegionAreaPointModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Torabi on 4/29/2018.
 */

public interface IRegionAreaPointApi {
    @GET("api/v2/ngt/visittemplatepath/RegionAreaPoint")
    Call<List<RegionAreaPointModel>> getAll(@Query("TourId") String tourId);
}
