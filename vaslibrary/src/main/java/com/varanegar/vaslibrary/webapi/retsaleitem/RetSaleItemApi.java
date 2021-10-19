package com.varanegar.vaslibrary.webapi.retsaleitem;

import android.content.Context;

import com.varanegar.vaslibrary.model.retsaleitem.RetSaleItemModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;
import com.varanegar.vaslibrary.webapi.retsalehdr.IRetSaleHdrApi;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Jafarzadeh on 3/3/2019.
 */

public class RetSaleItemApi extends BaseApi implements IRetSaleItemApi {
    public RetSaleItemApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<RetSaleItemModel>> getRetSaleItem(String Date) {
        IRetSaleItemApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IRetSaleItemApi.class);
        return api.getRetSaleItem(Date);
    }
}
