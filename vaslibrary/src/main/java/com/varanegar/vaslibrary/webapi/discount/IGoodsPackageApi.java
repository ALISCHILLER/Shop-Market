package com.varanegar.vaslibrary.webapi.discount;

import com.varanegar.vaslibrary.model.goodsPackage.GoodsPackageModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by A.Jafarzadeh on 12/26/2017.
 */

public interface IGoodsPackageApi {
    @GET("api/v2/ngt/goodspackage")
    Call<List<GoodsPackageModel>> getAll();
}
