package com.varanegar.vaslibrary.webapi.regionapi;

import android.content.Context;

import com.varanegar.vaslibrary.model.RegionAreaPointModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Torabi on 4/29/2018.
 */

public class RegionAreaPointApi extends BaseApi implements IRegionAreaPointApi {

    public RegionAreaPointApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<RegionAreaPointModel>> getAll(String tourId) {
        IRegionAreaPointApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IRegionAreaPointApi.class);
        return api.getAll(tourId);
    }
}
