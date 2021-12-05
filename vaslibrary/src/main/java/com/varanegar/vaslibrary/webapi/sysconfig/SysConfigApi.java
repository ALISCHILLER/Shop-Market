package com.varanegar.vaslibrary.webapi.sysconfig;

import android.content.Context;
import android.content.pm.PackageManager;

import com.google.gson.GsonBuilder;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.network.gson.VaranegarGsonBuilder;
import com.varanegar.vaslibrary.manager.sysconfigmanager.CenterSysConfigModel;
import com.varanegar.vaslibrary.manager.sysconfigmanager.OwnerKeysWrapper;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.HeaderInterceptor;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
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
                .client(getClient())
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                .build();
        return retrofit.create(ISysConfigApi.class).getOwnerKeys();
    }

    @Override
    public Call<List<SysConfigModel>> getLanguage() {
        GsonBuilder gsonBuilder = VaranegarGsonBuilder.build();
        Retrofit retrofit= new Retrofit.Builder()
                .baseUrl(getBaseUrl())
                .client(getClient())
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                .build();
        return retrofit.create(ISysConfigApi.class).getLanguage();
    }

    @Override
    public Call<List<SysConfigModel>> getSupervisorConfigs() {
        return getRetrofitBuilder(TokenType.UserToken).build().create(ISysConfigApi.class).getSupervisorConfigs();
    }

    protected OkHttpClient getClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();


        OwnerKeysWrapper ownerKeys =new OwnerKeysWrapper();
        ownerKeys.subsystemtypeid = String.valueOf(VaranegarApplication.getInstance().getAppId());
        try {
            ownerKeys.Version=String.valueOf(getContext().getApplicationContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        ownerKeys.DataOwnerCenterKey = "";
        ownerKeys.DataOwnerKey = "";
        ownerKeys.OwnerKey="";

        builder.addInterceptor(new HeaderInterceptor(ownerKeys, null));
        return builder.build();
    }
}
