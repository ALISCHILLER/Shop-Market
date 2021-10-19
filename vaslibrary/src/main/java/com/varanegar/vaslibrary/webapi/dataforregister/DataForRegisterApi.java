package com.varanegar.vaslibrary.webapi.dataforregister;

import android.content.Context;

import com.varanegar.vaslibrary.model.dataforregister.DataForRegisterModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;

public class DataForRegisterApi extends BaseApi implements IDataForRegisterApi {
    public DataForRegisterApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<DataForRegisterModel>> getAll() {
        IDataForRegisterApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IDataForRegisterApi.class);
        return api.getAll();
    }
}
