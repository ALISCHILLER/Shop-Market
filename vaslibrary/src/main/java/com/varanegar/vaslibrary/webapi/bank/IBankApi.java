package com.varanegar.vaslibrary.webapi.bank;

import com.varanegar.vaslibrary.model.bank.BankModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 3/26/2018.
 */

public interface IBankApi {
    @GET("api/v2/ngt/bank/sync/loaddata")
    Call<List<BankModel>> getBanks(@Query("Date") String dateAfter);
}
