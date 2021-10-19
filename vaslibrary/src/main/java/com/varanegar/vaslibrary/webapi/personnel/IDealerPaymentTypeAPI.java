package com.varanegar.vaslibrary.webapi.personnel;


import com.varanegar.vaslibrary.model.dealerPaymentType.DealerPaymentTypeModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by g.aliakbar on 13/03/2018.
 */

public interface IDealerPaymentTypeAPI {

    @GET("api/v2/ngt/personnel/sync/dealerpaymenttype")
    Call<List<DealerPaymentTypeModel>> getDealerPaymentType(@Query("personnelId") String personnelId);
}
