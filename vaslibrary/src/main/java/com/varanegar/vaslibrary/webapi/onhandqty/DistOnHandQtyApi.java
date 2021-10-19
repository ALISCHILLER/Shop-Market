package com.varanegar.vaslibrary.webapi.onhandqty;

import android.content.Context;

import com.varanegar.vaslibrary.model.onhandqty.OnHandQtyModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Torabi on 9/23/2018.
 */

public class DistOnHandQtyApi extends BaseApi implements IDistOnHandQtyApi {
    public DistOnHandQtyApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<OnHandQtyModel>> getAll(String tourId) {
        IDistOnHandQtyApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IDistOnHandQtyApi.class);
        return api.getAll(tourId);
    }
}
