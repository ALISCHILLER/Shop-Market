package com.varanegar.vaslibrary.webapi.visitday;
import com.varanegar.vaslibrary.model.visitday.VisitDayModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 6/27/2017.
 */

public interface IVisitDayApi
{
    @GET("api/v2/ngt/visittemplatepath/sync/loaddata")
    Call<List<VisitDayModel>> getAll(@Query("Date") String dateAfter, @Query("dealerId") String dealerId);
}
