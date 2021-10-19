package com.varanegar.vaslibrary.webapi.customerremainperLine;

import android.content.Context;

import com.varanegar.vaslibrary.model.customerremainperline.CustomerRemainPerLineModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;

/**
 * Created by A.Jafarzadeh on 8/20/2018.
 */

public class CustomerRemainPerLineApi extends BaseApi implements ICustomerRemainPerLineApi {
    public CustomerRemainPerLineApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<CustomerRemainPerLineModel>> getAll(String DealerId, String DeviceSettingNo, String customerId, int tourNo, UUID subSystemTypeId) {
        ICustomerRemainPerLineApi api = getRetrofitBuilder(TokenType.UserToken).build().create(ICustomerRemainPerLineApi.class);
        return api.getAll(DealerId, DeviceSettingNo, customerId,tourNo,subSystemTypeId);
    }
}
