package com.varanegar.vaslibrary.webapi.imageapi;

import android.content.Context;

import com.varanegar.vaslibrary.model.image.LogoModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;


import java.util.List;

import retrofit2.Call;

/**
 * Created by e.hashemzadeh on 6/11/2019.
 */

public class LogoApi extends BaseApi implements ILogoApi{
    public LogoApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<LogoModel>> logoInfo(String type) {
        ILogoApi api = getRetrofitBuilder(TokenType.UserToken).build().create(ILogoApi.class);
        return api.logoInfo(type);
    }
}
