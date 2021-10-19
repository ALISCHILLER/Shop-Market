package com.varanegar.vaslibrary.webapi.customerownertype;

import com.varanegar.vaslibrary.model.customerownertype.CustomerOwnerTypeModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 10/14/2018.
 */

public interface ICustomerOwnerTypeApi {
    @GET("api/v2/ngt/customerownertype/sync/loadData")
    Call<List<CustomerOwnerTypeModel>> getCustomerOwnerTypes(@Query("Date") String dateAfter);
}
