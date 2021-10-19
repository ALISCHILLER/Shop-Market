package com.varanegar.vaslibrary.webapi.customer;

import android.content.Context;

import com.varanegar.vaslibrary.model.customerCardex.CustomerCardexModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Jafarzadeh on 9/11/2017.
 */

public class CustomerCardexApi extends BaseApi implements ICustomerCardexApi {
    public CustomerCardexApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<CustomerCardexModel>> getAll(String subSystemTypeUniqueId, String tourId, String dateAfter, String DeviceSettingNo, String customerId, String startDate, String endDate) {
        ICustomerCardexApi api = getRetrofitBuilder(TokenType.UserToken).build().create(ICustomerCardexApi.class);
        return api.getAll(subSystemTypeUniqueId, tourId, dateAfter, DeviceSettingNo, customerId, startDate, endDate);
    }
}
