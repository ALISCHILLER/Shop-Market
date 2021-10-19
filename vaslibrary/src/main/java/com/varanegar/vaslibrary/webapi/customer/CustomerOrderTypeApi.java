package com.varanegar.vaslibrary.webapi.customer;

import android.content.Context;

import com.varanegar.vaslibrary.model.CustomerOrderType.CustomerOrderTypeModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 6/20/2017.
 */

public class CustomerOrderTypeApi extends BaseApi implements ICustomerOrderTypeApi
{

    public CustomerOrderTypeApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<CustomerOrderTypeModel>> getAll(@Query("Date") String dateAfter, @Query("devicesettingno") String devicesettingno, @Query("SubSystemTypeId") UUID appId) {
        ICustomerOrderTypeApi api = getRetrofitBuilder(TokenType.UserToken).build().create(ICustomerOrderTypeApi.class);
        return api.getAll(dateAfter , devicesettingno, appId);
    }
}
