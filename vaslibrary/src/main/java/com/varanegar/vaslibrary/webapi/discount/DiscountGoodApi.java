package com.varanegar.vaslibrary.webapi.discount;

import android.content.Context;

import com.varanegar.vaslibrary.model.discoungood.DiscountGoodModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Jafarzadeh on 3/3/2019.
 */

public class DiscountGoodApi extends BaseApi implements IDiscountGoodApi {
    public DiscountGoodApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<DiscountGoodModel>> getDiscountGood(String Date) {
        IDiscountGoodApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IDiscountGoodApi.class);
        return api.getDiscountGood(Date);
    }
}
