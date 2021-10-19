package com.varanegar.vaslibrary.webapi.goodsfixunit;

import android.content.Context;

import com.varanegar.vaslibrary.model.goodsfixunit.GoodsFixUnitModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;
import com.varanegar.vaslibrary.webapi.goodsnosale.IGoodsNosaleApi;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Jafarzadeh on 3/4/2019.
 */

public class GoodsFixUnitApi extends BaseApi implements IGoodsFixUnitApi {
    public GoodsFixUnitApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<GoodsFixUnitModel>> getGoodsFixUnit(String Date) {
        IGoodsFixUnitApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IGoodsFixUnitApi.class);
        return api.getGoodsFixUnit(Date);
    }
}
