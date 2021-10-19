package com.varanegar.vaslibrary.webapi.disacc;

import android.content.Context;

import com.varanegar.vaslibrary.model.disacc.DisAccModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;
import com.varanegar.vaslibrary.webapi.discount.IDiscountGoodApi;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Jafarzadeh on 3/4/2019.
 */

public class DisAccApi extends BaseApi implements IDisAccApi {
    public DisAccApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<DisAccModel>> getDisAcc() {
        IDisAccApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IDisAccApi.class);
        return api.getDisAcc();
    }
}
