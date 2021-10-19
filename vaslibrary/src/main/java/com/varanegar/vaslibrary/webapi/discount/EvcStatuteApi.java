package com.varanegar.vaslibrary.webapi.discount;

import android.content.Context;

import com.varanegar.vaslibrary.model.evcstatute.EvcStatuteTemplateModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Torabi on 11/20/2017.
 */

public class EvcStatuteApi extends BaseApi implements IEvcStatuteApi {
    public EvcStatuteApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<EvcStatuteTemplateModel>> get() {
        return getRetrofitBuilder(TokenType.UserToken).build().create(IEvcStatuteApi.class).get();
    }
}
