package com.varanegar.vaslibrary.webapi.distribution;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 2/19/2018.
 */

public interface IDistributionApi {
    @GET("api/v2/ngt/tour/sync/distribution")
    Call<DistributionTourViewModel> getDistribution(@Query("id") String id, @Query("virtual") boolean virtual);
}
