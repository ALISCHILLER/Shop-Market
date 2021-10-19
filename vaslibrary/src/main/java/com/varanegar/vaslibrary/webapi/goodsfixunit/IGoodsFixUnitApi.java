package com.varanegar.vaslibrary.webapi.goodsfixunit;

import com.varanegar.vaslibrary.model.goodsfixunit.GoodsFixUnitModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 3/4/2019.
 */

public interface IGoodsFixUnitApi {
    @GET("api/v2/ngt/discount/goodsfixunit")
    Call<List<GoodsFixUnitModel>> getGoodsFixUnit(@Query("Date") String Date);
}
