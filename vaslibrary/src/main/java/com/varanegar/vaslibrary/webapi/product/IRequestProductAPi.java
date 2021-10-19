package com.varanegar.vaslibrary.webapi.product;

import com.varanegar.vaslibrary.webapi.tour.SyncGetRequestLineModel;
import com.varanegar.vaslibrary.webapi.tour.SyncGetTourViewModel;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 4/4/2018.
 */

public interface IRequestProductAPi {
    @POST("api/v2/ngt/requestitem/save")
    Call<ResponseBody> saveRequestData(@Body SyncGetTourViewModel syncGetTourViewModel, @Query("DeviceSettingCode") String DeviceSettingCode);
}
