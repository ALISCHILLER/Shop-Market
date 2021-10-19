package com.varanegar.vaslibrary.webapi.returnreason;

import android.content.Context;

import com.varanegar.vaslibrary.model.returnReason.ReturnReasonModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 7/4/2017.
 */

public class ReturnReasonApi extends BaseApi implements IReturnReasonApi
{
    public ReturnReasonApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<ReturnReasonModel>> getAll(@Query("Date") String dateAfter, @Query("SubSystemTypeId") UUID appId) {
        IReturnReasonApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IReturnReasonApi.class);
        return api.getAll(dateAfter, appId);
    }
}
