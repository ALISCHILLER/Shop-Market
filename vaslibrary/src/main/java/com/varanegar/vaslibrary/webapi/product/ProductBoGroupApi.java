package com.varanegar.vaslibrary.webapi.product;

import android.content.Context;

import com.varanegar.vaslibrary.model.productBoGroup.ProductBoGroupModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Path;

/**
 * Created by A.Jafarzadeh on 6/13/2017.
 */

public class ProductBoGroupApi extends BaseApi implements IProductBoGrpoupApi
{
    public ProductBoGroupApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<ProductBoGroupModel>> getAll(@Path("Date") String dateAfter)
    {
        IProductBoGrpoupApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IProductBoGrpoupApi.class);
        return api.getAll(dateAfter);
    }

}
