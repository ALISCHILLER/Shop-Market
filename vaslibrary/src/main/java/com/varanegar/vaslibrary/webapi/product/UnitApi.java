package com.varanegar.vaslibrary.webapi.product;

import android.content.Context;

import com.varanegar.vaslibrary.model.unit.UnitModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 6/26/2017.
 */

public class UnitApi extends BaseApi implements IUnitApi
{
    public UnitApi(Context context)
    {
        super(context);
    }

    @Override
    public Call<List<UnitModel>> getAll(@Query("Date") String dateAfter)
    {
        IUnitApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IUnitApi.class);
        return api.getAll(dateAfter);
    }
}
