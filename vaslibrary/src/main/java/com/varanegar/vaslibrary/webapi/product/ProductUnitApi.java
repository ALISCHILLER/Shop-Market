package com.varanegar.vaslibrary.webapi.product;

import android.content.Context;

import com.varanegar.vaslibrary.model.productUnit.ProductUnitModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 6/26/2017.
 */

public class ProductUnitApi extends BaseApi implements IProductUnitApi
{
    public ProductUnitApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<ProductUnitModel>> getAll(@Query("Date") String dateAfter) {
        IProductUnitApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IProductUnitApi.class);
        return api.getAll(dateAfter);
    }
}
