package com.varanegar.vaslibrary.webapi.custextrafield;

import android.content.Context;

import com.varanegar.vaslibrary.model.custextrafield.CustExtraFieldModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Jafarzadeh on 3/4/2019.
 */

public class CustExtraFieldApi extends BaseApi implements ICustExtraFieldApi {
    public CustExtraFieldApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<CustExtraFieldModel>> getCustExtraField(String Date) {
        ICustExtraFieldApi api = getRetrofitBuilder(TokenType.UserToken).build().create(ICustExtraFieldApi.class);
        return api.getCustExtraField(Date);
    }
}
