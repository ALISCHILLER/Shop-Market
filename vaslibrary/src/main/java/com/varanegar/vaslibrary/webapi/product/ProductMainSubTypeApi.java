package com.varanegar.vaslibrary.webapi.product;

import android.content.Context;

import com.varanegar.vaslibrary.model.productMainSubType.ProductMainSubTypeModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 6/20/2017.
 */

public class ProductMainSubTypeApi extends BaseApi implements IProductMainSubTypeApi
{

    public ProductMainSubTypeApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<ProductMainSubTypeModel>> getAll(@Query("Date") String dateAfter) {
        IProductMainSubTypeApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IProductMainSubTypeApi.class);
        return api.getAll(dateAfter);
    }
}
