package com.varanegar.vaslibrary.webapi.dealer;

import com.varanegar.vaslibrary.model.dealer.DealerModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by A.Jafarzadeh on 9/15/2018.
 */

public interface IDealerApi {
    @GET("api/v2/ngt/personnel/Dealer/ForInvoiceReturn")
    Call<List<DealerModel>> getDealers();
}
