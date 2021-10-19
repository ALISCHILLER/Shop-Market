package com.varanegar.vaslibrary.webapi.onhandqty;

import android.content.Context;

import com.varanegar.vaslibrary.model.onhandqty.OnHandQtyModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Jafarzadeh on 7/10/2017.
 */

public class OnHandQtyAPI extends BaseApi implements IOnHandQtyAPI {
    public OnHandQtyAPI(Context context) {
        super(context);
    }

    @Override
    public Call<List<OnHandQtyModel>> getAll(String dateAfter, String dealerId) {
        IOnHandQtyAPI api = getRetrofitBuilder(TokenType.UserToken).build().create(IOnHandQtyAPI.class);
        return api.getAll(dateAfter, dealerId);
    }
}
