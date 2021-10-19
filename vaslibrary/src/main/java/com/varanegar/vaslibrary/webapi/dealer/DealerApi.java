package com.varanegar.vaslibrary.webapi.dealer;

import android.content.Context;

import com.varanegar.vaslibrary.model.dealer.DealerModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Jafarzadeh on 9/15/2018.
 */

public class DealerApi extends BaseApi implements IDealerApi {
    public DealerApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<DealerModel>> getDealers() {
        IDealerApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IDealerApi.class);
        return api.getDealers();
    }
}
