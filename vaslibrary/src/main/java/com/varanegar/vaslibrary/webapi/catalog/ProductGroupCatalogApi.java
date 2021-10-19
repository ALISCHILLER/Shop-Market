package com.varanegar.vaslibrary.webapi.catalog;

import android.content.Context;

import com.varanegar.vaslibrary.model.productgroupcatalog.ProductGroupCatalogModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Query;

/**
 * Created by A.Torabi on 8/6/2017.
 */

public class ProductGroupCatalogApi extends BaseApi implements IProductGroupCatalogApi {
    public ProductGroupCatalogApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<ProductGroupCatalogModel>> getCatalogs() {
        return getRetrofitBuilder(TokenType.UserToken).build().create(IProductGroupCatalogApi.class).getCatalogs();
    }
}
