package com.varanegar.vaslibrary.webapi.distribution;

import android.content.Context;

import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import retrofit2.Call;

/**
 * Created by A.Jafarzadeh on 2/19/2018.
 */

public class DistributionApi extends BaseApi implements IDistributionApi {
    public DistributionApi(Context context) {
        super(context);
    }

    @Override
    public Call<DistributionTourViewModel> getDistribution(String id, boolean virtual) {
        IDistributionApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IDistributionApi.class);
        return api.getDistribution(id, virtual);
    }
}
