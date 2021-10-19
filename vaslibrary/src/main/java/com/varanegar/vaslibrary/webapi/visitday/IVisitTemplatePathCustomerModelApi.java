package com.varanegar.vaslibrary.webapi.visitday;

import com.varanegar.vaslibrary.model.VisitTemplatePathCustomer.VisitTemplatePathCustomerModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 6/28/2017.
 */

public interface IVisitTemplatePathCustomerModelApi
{
    @GET("api/v2/ngt/visittemplatepathcustomer/sync/loaddata")
    Call<List<VisitTemplatePathCustomerModel>> getAll(@Query("Date") String dateAfter, @Query("dealerId") String dealerId, @Query("DeviceSettingNo") String DeviceSettingNo, @Query("customerId") String customerId);
}
