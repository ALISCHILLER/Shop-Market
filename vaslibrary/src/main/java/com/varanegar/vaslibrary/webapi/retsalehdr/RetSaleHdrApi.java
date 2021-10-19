package com.varanegar.vaslibrary.webapi.retsalehdr;

import android.content.Context;

import com.varanegar.vaslibrary.model.retsalehdr.RetSaleHdrModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;
import com.varanegar.vaslibrary.webapi.discount.IDiscountGoodApi;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Jafarzadeh on 3/3/2019.
 */

public class RetSaleHdrApi extends BaseApi implements IRetSaleHdrApi {
    public RetSaleHdrApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<RetSaleHdrModel>> getRetSaleHdr(String Date) {
        IRetSaleHdrApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IRetSaleHdrApi.class);
        return api.getRetSaleHdr(Date);
    }
}
