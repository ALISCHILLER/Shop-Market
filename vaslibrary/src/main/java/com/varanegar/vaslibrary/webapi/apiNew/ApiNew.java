package com.varanegar.vaslibrary.webapi.apiNew;

import android.content.Context;

import com.varanegar.vaslibrary.ui.fragment.news_fragment.model.NewsData_Model;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;
import com.varanegar.vaslibrary.webapi.apiNew.modelNew.PinRequestViewModel;
import com.varanegar.vaslibrary.webapi.customer.ICustomerApi;

import java.util.List;

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

    @Override
    public Call<List<NewsData_Model>> getNewsData() {
        InApiNew api = getRetrofitBuilder(TokenType.UserToken)
                .build().create(InApiNew.class);
        return api.getNewsData();
    }
}
