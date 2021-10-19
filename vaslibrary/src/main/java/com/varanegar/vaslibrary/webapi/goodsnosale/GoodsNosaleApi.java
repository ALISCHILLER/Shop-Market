package com.varanegar.vaslibrary.webapi.goodsnosale;

import android.content.Context;

import com.varanegar.vaslibrary.model.goodsnosale.GoodsNosaleModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;
import com.varanegar.vaslibrary.webapi.discount.IDiscountGoodApi;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Jafarzadeh on 3/4/2019.
 */

public class GoodsNosaleApi extends BaseApi implements IGoodsNosaleApi {
    public GoodsNosaleApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<GoodsNosaleModel>> getGoodsNosale(String Date) {
        IGoodsNosaleApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IGoodsNosaleApi.class);
        return api.getGoodsNosale(Date);
    }
}
