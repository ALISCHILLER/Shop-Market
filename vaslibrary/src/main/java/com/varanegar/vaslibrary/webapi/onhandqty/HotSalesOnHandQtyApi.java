package com.varanegar.vaslibrary.webapi.onhandqty;

import android.content.Context;

import com.varanegar.vaslibrary.model.onhandqty.OnHandQtyModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Jafarzadeh on 3/26/2018.
 */

public class HotSalesOnHandQtyApi extends BaseApi implements IHotSalesOnHandQtyApi {
    public HotSalesOnHandQtyApi(Context context) {
        super(context);
    }


    @Override
    public Call<List<OnHandQtyModel>> getAll(String dealerId, String tourId) {
        IHotSalesOnHandQtyApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IHotSalesOnHandQtyApi.class);
        return api.getAll(dealerId, tourId);
    }

    @Override
    public Call<List<OnHandQtyModel>> renew(String dealerId, String tourId) {
        IHotSalesOnHandQtyApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IHotSalesOnHandQtyApi.class);
        return api.renew(dealerId, tourId);
    }
}
