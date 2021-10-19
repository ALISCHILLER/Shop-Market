package com.varanegar.vaslibrary.webapi.discount;

import android.content.Context;

import com.varanegar.vaslibrary.model.goodsPackage.GoodsPackageModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Jafarzadeh on 12/26/2017.
 */

public class GoodsPackageApi extends BaseApi implements IGoodsPackageApi {
    public GoodsPackageApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<GoodsPackageModel>> getAll() {
        IGoodsPackageApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IGoodsPackageApi.class);
        return api.getAll();
    }
}
