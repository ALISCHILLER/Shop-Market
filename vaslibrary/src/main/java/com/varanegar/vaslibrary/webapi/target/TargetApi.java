package com.varanegar.vaslibrary.webapi.target;

import android.content.Context;

import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Jafarzadeh on 12/16/2017.
 */

public class TargetApi extends BaseApi implements ITargetApi {
    public TargetApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<TargetMasterViewModel>> getTarget(String date, String dealerId) {
        return getRetrofitBuilder(TokenType.UserToken).build().create(ITargetApi.class).getTarget(date,dealerId);
    }

    @Override
    public Call<List<TargetReportDetailViewModel>> getTargetDetail(String dealerId, String targetId, String customerId) {
        return getRetrofitBuilder(TokenType.UserToken).build().create(ITargetApi.class).getTargetDetail(dealerId,targetId,customerId);
    }
}
