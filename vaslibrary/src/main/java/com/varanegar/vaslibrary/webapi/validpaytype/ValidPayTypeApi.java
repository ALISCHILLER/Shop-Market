package com.varanegar.vaslibrary.webapi.validpaytype;

import android.content.Context;

import com.varanegar.vaslibrary.model.validpaytype.ValidPayTypeModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Jafarzadeh on 4/22/2018.
 */

public class ValidPayTypeApi extends BaseApi implements IValidPayTypeAPI {
    public ValidPayTypeApi(Context context) {
        super(context);
    }


    @Override
    public Call<List<ValidPayTypeModel>> getAll(String dateAfter) {
        IValidPayTypeAPI iValidPayTypeAPI = getRetrofitBuilder(TokenType.UserToken).build().create(IValidPayTypeAPI.class);
        return iValidPayTypeAPI.getAll(dateAfter);
    }
}
