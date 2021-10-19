package com.varanegar.vaslibrary.webapi.product;

import android.content.Context;

import com.varanegar.vaslibrary.model.product.ProductModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Jafarzadeh on 6/14/2017.
 */

public class ProductApi extends BaseApi implements IProductApi {
    public ProductApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<ProductModel>> getAll(String dateAfter, String dealerId , String subSystemTypeUniqueId) {
        IProductApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IProductApi.class);
        return api.getAll(dateAfter, dealerId , subSystemTypeUniqueId);
    }

    @Override
    public Call<List<PersonnelProductTemplateViewModel>> getPersonnelProductData(String dealerId, String subSystemTypeUniqueId) {
        IProductApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IProductApi.class);
        return api.getPersonnelProductData(dealerId, subSystemTypeUniqueId);
    }
}
