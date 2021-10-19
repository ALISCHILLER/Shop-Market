package com.varanegar.vaslibrary.webapi.retsalehdr;

import com.varanegar.vaslibrary.model.retsalehdr.RetSaleHdrModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 3/3/2019.
 */

public interface IRetSaleHdrApi {
    @GET("api/v2/ngt/discount/RetSaleHdr")
    Call<List<RetSaleHdrModel>> getRetSaleHdr(@Query("Date") String Date);
}
