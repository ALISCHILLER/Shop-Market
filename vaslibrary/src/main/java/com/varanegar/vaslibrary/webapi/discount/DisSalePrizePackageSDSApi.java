package com.varanegar.vaslibrary.webapi.discount;

import android.content.Context;

import com.varanegar.vaslibrary.model.dissaleprizepackagesds.DisSalePrizePackageSDSModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Jafarzadeh on 12/26/2017.
 */

public class DisSalePrizePackageSDSApi extends BaseApi implements IDisSalePrizePackageSDSApi {
    public DisSalePrizePackageSDSApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<DisSalePrizePackageSDSModel>> getAll() {
        IDisSalePrizePackageSDSApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IDisSalePrizePackageSDSApi.class);
        return api.getAll();
    }
}
