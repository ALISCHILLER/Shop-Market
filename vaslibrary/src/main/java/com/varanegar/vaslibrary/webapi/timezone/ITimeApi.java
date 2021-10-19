package com.varanegar.vaslibrary.webapi.timezone;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ITimeApi {
    @GET("api/v2/ngt/TimeZone")
    Call<TimeViewModel> getTimeZone();
}
