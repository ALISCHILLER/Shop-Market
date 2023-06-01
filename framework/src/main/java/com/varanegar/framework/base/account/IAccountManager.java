package com.varanegar.framework.base.account;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by atp on 6/6/2017.
 */

public interface IAccountManager {
    @FormUrlEncoded
    @POST("oauth/token")
    Call<Token> getToken(
            @Field("username") String username,
            @Field("password") String password,
            @Field("grant_type") String grantType,
            @Field("scope") String scope,
            @Field("DeviceId") String deviceId,
            @Field("Token") String token,
            @Field("vpnUser") String vpnUser,
            @Field("SystemTypeId") String SystemTypeId
    );
}
