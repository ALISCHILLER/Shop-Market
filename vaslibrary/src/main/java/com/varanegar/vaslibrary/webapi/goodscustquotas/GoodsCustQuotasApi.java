package com.varanegar.vaslibrary.webapi.goodscustquotas;

import android.content.Context;

import com.varanegar.vaslibrary.model.goodscustquotas.GoodsCustQuotasModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Jafarzadeh on 11/19/2018.
 */

public class GoodsCustQuotasApi extends BaseApi implements IGoodsCustQuotasApi {
    public GoodsCustQuotasApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<GoodsCustQuotasModel>> getGoodsCustQuotas(String dealerId) {
        IGoodsCustQuotasApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IGoodsCustQuotasApi.class);
        return api.getGoodsCustQuotas(dealerId);
    }
}
