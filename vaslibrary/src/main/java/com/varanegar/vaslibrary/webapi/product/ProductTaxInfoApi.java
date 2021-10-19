package com.varanegar.vaslibrary.webapi.product;

import android.content.Context;

import com.varanegar.vaslibrary.model.product.ProductTaxInfoModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Jafarzadeh on 6/14/2017.
 */

public class ProductTaxInfoApi extends BaseApi implements IProductTaxInfoApi {
    public ProductTaxInfoApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<ProductTaxInfoModel>> getAll() {
        IProductTaxInfoApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IProductTaxInfoApi.class);
        return api.getAll();
    }

}
