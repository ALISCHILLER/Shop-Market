package com.varanegar.vaslibrary.webapi.freereason;

import android.content.Context;

import com.varanegar.vaslibrary.model.freeReason.FreeReasonModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Torabi on 7/26/2017.
 */

public class FreeReasonApi extends BaseApi implements IFreeReasonApi {
    public FreeReasonApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<FreeReasonModel>> getAll() {
        IFreeReasonApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IFreeReasonApi.class);
        return api.getAll();
    }
}
