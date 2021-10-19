package com.varanegar.vaslibrary.webapi.nosalereason;

import com.varanegar.vaslibrary.model.noSaleReason.NoSaleReasonModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 8/10/2017.
 */

public interface INoSaleReasonApi
{
    @GET("api/v2/ngt/nosalereason/sync/loaddata/all")
    Call<List<NoSaleReasonModel>> getAll(@Query("Date") String date);
}
