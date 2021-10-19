package com.varanegar.vaslibrary.webapi.customer;

import com.varanegar.vaslibrary.model.customerCardex.CustomerCardexModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 9/11/2017.
 */

public interface ICustomerCardexApi
{
    @GET("api/v2/ngt/customer/customercardex/sync/loaddata")
    Call<List<CustomerCardexModel>> getAll(@Query("SubSystemTypeUniqueId") String subSystemTypeUniqueId , @Query("tourId") String tourId, @Query("Date") String dateAfter, @Query("DeviceSettingNo") String DeviceSettingNo,@Query("customerId") String customerId, @Query("startDate") String startDate, @Query("endDate") String endDate);
}
