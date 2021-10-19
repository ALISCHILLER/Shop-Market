package com.varanegar.vaslibrary.webapi.price;

import android.content.Context;

import com.varanegar.vaslibrary.model.priceHistory.PriceHistoryModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Jafarzadeh on 6/12/2017.
 */

public class PriceHistoryApi extends BaseApi implements IPriceHistoryApi
{
    public PriceHistoryApi(Context context)
    {
        super(context);
    }

    @Override
    public Call<List<PriceHistoryModel>> getAll(String dateAfter) {
        IPriceHistoryApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IPriceHistoryApi.class);
        return api.getAll(dateAfter);
    }
}
