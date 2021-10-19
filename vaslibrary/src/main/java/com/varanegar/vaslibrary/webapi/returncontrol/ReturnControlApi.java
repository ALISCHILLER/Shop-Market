package com.varanegar.vaslibrary.webapi.returncontrol;

import android.content.Context;

import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import retrofit2.Call;

public class ReturnControlApi extends BaseApi implements IReturnControlApi {
    public ReturnControlApi(Context context) {
        super(context);
    }


    @Override
    public Call<String> returnControl(ReturnControlHeaderViewModel preSaleEvcHeaderViewModel) {
        IReturnControlApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IReturnControlApi.class);
        return api.returnControl(preSaleEvcHeaderViewModel);
    }
}
