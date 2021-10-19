package com.varanegar.vaslibrary.webapi.ping;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by A.Torabi on 8/21/2017.
 */

public interface IPingApi {
    @GET("/api/v2/ngt/ping")
    Call<Void> ping();
}
