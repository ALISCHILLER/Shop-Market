package com.varanegar.vaslibrary.webapi.customerownertype;

import android.content.Context;

import com.varanegar.vaslibrary.model.customerownertype.CustomerOwnerTypeModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Jafarzadeh on 10/14/2018.
 */

public class CustomerOwnerTypeApi extends BaseApi implements ICustomerOwnerTypeApi {
    public CustomerOwnerTypeApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<CustomerOwnerTypeModel>> getCustomerOwnerTypes(String dateAfter) {
        ICustomerOwnerTypeApi api = getRetrofitBuilder(TokenType.UserToken).build().create(ICustomerOwnerTypeApi.class);
        return api.getCustomerOwnerTypes(dateAfter);
    }
}
