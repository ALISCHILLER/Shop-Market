package com.varanegar.vaslibrary.webapi.appversion;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Torabi on 2/10/2018.
 */

public interface IAppVersionApi {
    @GET("api/v2/ngt/downloadapk")
    Call<ResponseBody> downloadApk(@Query("FileName") String fileName);
    @GET("api/v2/ngt/latestversion")
    Call<String> getLatestVersion(@Query("SubSystemTypesId") String subSystemTypesId);
}
