package com.varanegar.vaslibrary.webapi.validpaytype;

import com.varanegar.vaslibrary.model.validpaytype.ValidPayTypeModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 4/22/2018.
 */

public interface IValidPayTypeAPI {
    @GET("api/v2/ngt/validpaytype/sync/loaddata")
    Call<List<ValidPayTypeModel>> getAll(@Query("date") String date);
}
