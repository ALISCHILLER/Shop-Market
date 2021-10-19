package com.varanegar.vaslibrary.webapi.product;

import android.content.Context;

import com.varanegar.vaslibrary.model.productGroup.ProductGroupModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 6/14/2017.
 */

public class ProductGroupApi extends BaseApi implements IProductGroupApi {

    public ProductGroupApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<ProductGroupModel>> getAll(@Query("Date") String dateAfter) {
        IProductGroupApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IProductGroupApi.class);
        return api.getAll(dateAfter);
    }
}
