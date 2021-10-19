package com.varanegar.vaslibrary.webapi.sysconfig;

import android.content.Context;

import com.google.gson.GsonBuilder;
import com.varanegar.framework.network.gson.VaranegarGsonBuilder;
import com.varanegar.vaslibrary.manager.sysconfigmanager.CenterSysConfigModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Query;

/**
 * Created by A.Torabi on 8/7/2017.
 */

public class SysConfigApi extends BaseApi implements ISysConfigApi {
    public SysConfigApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<SysConfigModel>> getAll(@Query("code") String code) {
        return getRetrofitBuilder(TokenType.UserToken).build().create(ISysConfigApi.class).getAll(code);
    }

    @Override
    public Call<List<CenterSysConfigModel>> getOwnerKeys() {
        GsonBuilder gsonBuilder = VaranegarGsonBuilder.build();
        Retrofit retrofit= new Retrofit.Builder()
                .baseUrl(getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                .build();
        return retrofit.create(ISysConfigApi.class).getOwnerKeys();
    }

    @Override
    public Call<List<SysConfigModel>> getLanguage() {
        GsonBuilder gsonBuilder = VaranegarGsonBuilder.build();
        Retrofit retrofit= new Retrofit.Builder()
                .baseUrl(getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                .build();
        return retrofit.create(ISysConfigApi.class).getLanguage();
    }

    @Override
    public Call<List<SysConfigModel>> getSupervisorConfigs() {
        return getRetrofitBuilder(TokenType.UserToken).build().create(ISysConfigApi.class).getSupervisorConfigs();
    }
}
