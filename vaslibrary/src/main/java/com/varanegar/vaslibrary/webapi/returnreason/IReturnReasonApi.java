package com.varanegar.vaslibrary.webapi.returnreason;

import com.varanegar.vaslibrary.model.productUnit.ProductUnitModel;
import com.varanegar.vaslibrary.model.returnReason.ReturnReason;
import com.varanegar.vaslibrary.model.returnReason.ReturnReasonModel;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 7/4/2017.
 */

public interface IReturnReasonApi
{
    @GET("api/v2/ngt/returnreason/sync/loaddata")
    Call<List<ReturnReasonModel>> getAll(@Query("Date") String dateAfter, @Query("SubSystemTypeId") UUID appId);
}
