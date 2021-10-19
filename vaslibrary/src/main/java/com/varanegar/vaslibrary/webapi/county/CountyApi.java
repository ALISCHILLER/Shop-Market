package com.varanegar.vaslibrary.webapi.county;

import android.content.Context;

import com.varanegar.vaslibrary.model.county.CountyModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Jafarzadeh on 10/14/2018.
 */

public class CountyApi extends BaseApi implements ICountyApi {
    public CountyApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<CountyModel>> getCounties(String dateAfter) {
        ICountyApi api = getRetrofitBuilder(TokenType.UserToken).build().create(ICountyApi.class);
        return api.getCounties(dateAfter);
    }
}
