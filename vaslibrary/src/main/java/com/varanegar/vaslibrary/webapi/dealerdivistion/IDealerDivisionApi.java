package com.varanegar.vaslibrary.webapi.dealerdivistion;

import com.varanegar.vaslibrary.model.dealerdivision.DealerDivisionModel;
import retrofit2.Call;
import retrofit2.http.GET;

public interface IDealerDivisionApi {

    @GET("api/v2/ngt/personnel/dealer/GetDealerDivision")
    Call<DealerDivisionModel> get();

}
