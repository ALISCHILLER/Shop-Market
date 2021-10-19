package com.varanegar.vaslibrary.webapi.customer;

import com.varanegar.vaslibrary.model.CustomerOrderType.CustomerOrderTypeModel;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 6/20/2017.
 */

public interface ICustomerOrderTypeApi
{
    @GET("api/v2/ngt/devicesetting/ordertype")
    Call<List<CustomerOrderTypeModel>> getAll(@Query("Date") String dateAfter, @Query("devicesettingno") String devicesettingno, @Query("SubSystemTypeId") UUID appId);
}
