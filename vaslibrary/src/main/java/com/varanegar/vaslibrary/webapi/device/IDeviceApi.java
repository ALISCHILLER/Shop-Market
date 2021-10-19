package com.varanegar.vaslibrary.webapi.device;

import com.varanegar.vaslibrary.webapi.tracking.LicenseRequestBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IDeviceApi {
    @POST("api/device/companydev/app/CheckLicense")
    Call<CompanyDeviceAppResult> checkLicense(@Body LicenseRequestBody info);

    @POST("api/device/register")
    Call<Void> registerDeviceToken(@Body UserDeviceTokenViewModel tokenViewModel);
}
