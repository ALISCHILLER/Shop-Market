package com.varanegar.vaslibrary.webapi.goodsnosale;

import com.varanegar.vaslibrary.model.goodsnosale.GoodsNosaleModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 3/4/2019.
 */

public interface IGoodsNosaleApi {
    @GET("api/v2/ngt/discount/goodsnosale")
    Call<List<GoodsNosaleModel>> getGoodsNosale(@Query("Date") String Date);
}
