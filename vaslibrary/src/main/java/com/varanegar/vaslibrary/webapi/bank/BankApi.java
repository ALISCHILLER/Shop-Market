package com.varanegar.vaslibrary.webapi.bank;

import android.content.Context;

import com.varanegar.vaslibrary.model.bank.BankModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;
import com.varanegar.vaslibrary.webapi.city.ICityApi;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Jafarzadeh on 3/26/2018.
 */

public class BankApi extends BaseApi implements IBankApi {
    public BankApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<BankModel>> getBanks(String dateAfter) {
        IBankApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IBankApi.class);
        return api.getBanks(dateAfter);
    }
}
