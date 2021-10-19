package com.varanegar.vaslibrary.webapi.returncontrol;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IReturnControlApi {
    @POST("api/v2/ngt/evc/returnControl")
    Call<String> returnControl(@Body ReturnControlHeaderViewModel preSaleEvcHeaderViewModel);
}