package com.varanegar.vaslibrary.webapi.product;

import android.content.Context;

import com.varanegar.vaslibrary.model.emphaticproduct.EmphaticProductModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Torabi on 7/10/2017.
 */

public class EmphaticProductApi extends BaseApi implements IEmphaticProductApi {
    public EmphaticProductApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<EmphaticProductModel>> getAll() {
        IEmphaticProductApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IEmphaticProductApi.class);
        return api.getAll();
    }
}
