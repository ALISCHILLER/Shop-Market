package com.varanegar.vaslibrary.webapi.apiNew;

import android.content.Context;

import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;
import com.varanegar.vaslibrary.webapi.apiNew.modelNew.PinRequestViewModel;
import com.varanegar.vaslibrary.webapi.customer.ICustomerApi;

import retrofit2.Call;

public class ApiNew extends BaseApi implements InApiNew {
    public ApiNew(Context context) {
        super(context);
    }

    @Override
    public Call<String> sendPinCode(PinRequestViewModel pinRequestViewModel) {
        InApiNew api = getRetrofitBuilder(TokenType.UserToken)
                .build().create(InApiNew.class);
        return api.sendPinCode(pinRequestViewModel);
    }
}
