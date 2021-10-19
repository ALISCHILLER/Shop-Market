package com.varanegar.vaslibrary.webapi.city;

import android.content.Context;

import com.varanegar.vaslibrary.model.city.CityModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;
import com.varanegar.vaslibrary.webapi.catalog.ICatalogApi;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Jafarzadeh on 3/25/2018.
 */

public class CityApi extends BaseApi implements ICityApi {

    public CityApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<CityModel>> getCities(String dateAfter) {
        ICityApi api = getRetrofitBuilder(TokenType.UserToken).build().create(ICityApi.class);
        return api.getCities(dateAfter);
    }
}
