package com.varanegar.vaslibrary.webapi.customerbogroup;

import com.varanegar.vaslibrary.model.customerbogroup.CustomerBoGroupModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 3/17/2019.
 */

public interface ICustomerBoGroupApi {
    @GET("api/v2/ngt/customer/CustomerBOGroups")
    Call<List<CustomerBoGroupModel>> getAll(@Query("Date") String Date);
}
