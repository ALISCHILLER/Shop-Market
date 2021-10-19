package com.varanegar.vaslibrary.webapi.tracking;

import com.varanegar.vaslibrary.manager.locationmanager.BaseLocationViewModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by A.Torabi on 8/12/2017.
 */

public interface ITrackingApi {
    @POST("api/device/companydev/app/licbyimei")
    Call<LicenseResponse> getLicense(@Body LicenseRequestBody body);
    @POST("/api/device/companydev/app/save")
    Call<DeviceSaveResponse> requestLicense(@Body LicenseRequestBody body);
    @POST("api/dsd/tracking/svprsact")
    Call<Boolean> sendPoint(@Body TrackingRequestModel body);
}
