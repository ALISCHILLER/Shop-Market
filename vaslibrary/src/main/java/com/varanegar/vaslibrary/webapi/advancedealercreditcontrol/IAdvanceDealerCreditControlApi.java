package com.varanegar.vaslibrary.webapi.advancedealercreditcontrol;

import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by A.Jafarzadeh on 8/7/2018.
 */

public interface IAdvanceDealerCreditControlApi {
    @GET("api/v2/ngt/personnel/dealer/AdvancedCreditControl/{id}")
    Call<SysConfigModel> getAdvanceDealerCreditsControl(@Path("id") String id);
}
