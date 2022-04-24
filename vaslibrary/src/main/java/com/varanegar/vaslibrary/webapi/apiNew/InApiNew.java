package com.varanegar.vaslibrary.webapi.apiNew;

import com.varanegar.vaslibrary.ui.fragment.news_fragment.model.NewsData_Model;
import com.varanegar.vaslibrary.webapi.apiNew.modelNew.PinRequestViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface InApiNew {

    @POST("api/v2/ngt/customer/pinrequest")
    Call<String> sendPinCode(
            @Body PinRequestViewModel pinRequestViewModel
            );

    @GET("api/v2/ngt/newsletter/sync/loaddata")
    Call<List<NewsData_Model>>getNewsData();
}
