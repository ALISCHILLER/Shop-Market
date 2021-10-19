package com.varanegar.vaslibrary.webapi.price;

import android.content.Context;

import com.varanegar.vaslibrary.model.priceclass.PriceClassVnLiteModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Jafarzadeh on 2/21/2018.
 */

public class PriceClassApi extends BaseApi implements IPriceClassApi {
    public PriceClassApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<PriceClassVnLiteModel>> getAll() {
        IPriceClassApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IPriceClassApi.class);
        return api.getAll();
    }
}
