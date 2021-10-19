package com.varanegar.vaslibrary.webapi.sysconfig;

import com.varanegar.vaslibrary.manager.sysconfigmanager.CenterSysConfigModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Torabi on 8/7/2017.
 */

public interface ISysConfigApi {
    @GET("api/v2/ngt/devicesetting/bycode")
    Call<List<SysConfigModel>> getAll(@Query("code") String code);

    @GET("api/v2/ngt/devicesetting/ownerkeys")
    Call<List<CenterSysConfigModel>> getOwnerKeys();

    @GET("api/v2/ngt/devicesetting/Language")
    Call<List<SysConfigModel>> getLanguage();

    @GET("api/v2/ngt/supervisorsetting?code=0")
    Call<List<SysConfigModel>> getSupervisorConfigs();
}
