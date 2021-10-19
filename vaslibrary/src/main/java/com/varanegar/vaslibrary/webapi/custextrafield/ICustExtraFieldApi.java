package com.varanegar.vaslibrary.webapi.custextrafield;

import com.varanegar.vaslibrary.model.custextrafield.CustExtraFieldModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 3/4/2019.
 */

public interface ICustExtraFieldApi {
    @GET("api/v2/ngt/discount/CustExtraField")
    Call<List<CustExtraFieldModel>> getCustExtraField(@Query("Date") String Date);
}
