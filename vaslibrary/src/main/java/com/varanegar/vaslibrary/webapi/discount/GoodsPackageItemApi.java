package com.varanegar.vaslibrary.webapi.discount;

import android.content.Context;

import com.varanegar.vaslibrary.model.goodsPackageItem.GoodsPackageItemModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Jafarzadeh on 12/26/2017.
 */

public class GoodsPackageItemApi extends BaseApi implements IGoodsPackageItemApi {
    public GoodsPackageItemApi(Context context) {
        super(context);
    }


    @Override
    public Call<List<GoodsPackageItemModel>> getAll() {
        IGoodsPackageItemApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IGoodsPackageItemApi.class);
        return api.getAll();
    }
}
