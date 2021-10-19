package com.varanegar.vaslibrary.webapi.customerbogroup;

import android.content.Context;

import com.varanegar.vaslibrary.model.customerbogroup.CustomerBoGroupModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;
import com.varanegar.vaslibrary.webapi.goodsfixunit.IGoodsFixUnitApi;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Jafarzadeh on 3/17/2019.
 */

public class CustomerBoGroupApi extends BaseApi implements ICustomerBoGroupApi {
    public CustomerBoGroupApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<CustomerBoGroupModel>> getAll(String Date) {
        ICustomerBoGroupApi api = getRetrofitBuilder(TokenType.UserToken).build().create(ICustomerBoGroupApi.class);
        return api.getAll(Date);
    }
}
