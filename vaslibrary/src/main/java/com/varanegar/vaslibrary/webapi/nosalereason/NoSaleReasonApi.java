package com.varanegar.vaslibrary.webapi.nosalereason;

import android.content.Context;

import com.varanegar.vaslibrary.model.noSaleReason.NoSaleReasonModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 8/10/2017.
 */

public class NoSaleReasonApi extends BaseApi implements INoSaleReasonApi
{
    public NoSaleReasonApi(Context context)
    {
        super(context);
    }

    @Override
    public Call<List<NoSaleReasonModel>> getAll(String date) {
        INoSaleReasonApi api = getRetrofitBuilder(TokenType.UserToken).build().create(INoSaleReasonApi.class);
        return api.getAll(date);
    }
}
