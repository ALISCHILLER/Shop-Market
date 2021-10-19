package com.varanegar.vaslibrary.webapi.target;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 12/16/2017.
 */

public interface ITargetApi {
    @GET("api/v2/ngt/target/sync/loaddata")
    Call<List<TargetMasterViewModel>> getTarget(@Query("date") String date, @Query("dealerId") String dealerId);

    @GET("api/v2/ngt/target/loadreport")
    Call<List<TargetReportDetailViewModel>> getTargetDetail(@Query("dealerId") String dealerId, @Query("targetId") String targetId, @Query("customerId") String customerId);
}
