package com.varanegar.vaslibrary.webapi.apiNew;

import com.varanegar.vaslibrary.webapi.apiNew.modelNew.PinRequestViewModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface InApiNew {

    @POST("api/v2/ngt/customer/pinrequest")
    Call<String> sendPinCode(
            @Body PinRequestViewModel pinRequestViewModel
            );

}
