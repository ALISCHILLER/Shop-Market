package com.varanegar.vaslibrary.webapi.disacc;

import com.varanegar.vaslibrary.model.disacc.DisAccModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 3/4/2019.
 */

public interface IDisAccApi {
    @GET("api/v2/ngt/discount/disacc")
    Call<List<DisAccModel>> getDisAcc();
}
