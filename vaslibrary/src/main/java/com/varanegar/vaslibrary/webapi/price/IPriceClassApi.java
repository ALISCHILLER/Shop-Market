package com.varanegar.vaslibrary.webapi.price;


import com.varanegar.vaslibrary.model.priceclass.PriceClassVnLiteModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by A.Jafarzadeh on 2/21/2018.
 */

public interface IPriceClassApi {
    @GET("api/v2/ngt/pricevnlite/sync/loaddata/priceClass")
    Call<List<PriceClassVnLiteModel>> getAll();
}
