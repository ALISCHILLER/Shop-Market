package com.varanegar.vaslibrary.webapi.catalog;

import android.content.Context;

import com.varanegar.vaslibrary.model.catalog.CatalogModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by A.Torabi on 6/21/2017.
 */

public class CatalogApi extends BaseApi implements ICatalogApi {
//    ICatalogApi api;

    public CatalogApi(Context context) {
        super(context);
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(getBaseUrl())
//                .build();
//        api = retrofit.create(ICatalogApi.class);
    }

//    @Override
//    public Call<ResponseBody> downloadImage(@Path("type") String type, @Path("tokenId") String tokenId, @Path("id") String id) {
//        return api.downloadImage(type, tokenId, id);
//    }

    @Override
    public Call<List<CatalogModel>> getCatalogs(@Query("Date") String date) {
        ICatalogApi api = getRetrofitBuilder(TokenType.UserToken).build().create(ICatalogApi.class);
        return api.getCatalogs(date);
    }

//    @Override
//    public Call<List<ProductImage>> getPictureIds() {
//        ICatalogApi api = getRetrofitBuilder(TokenType.UserToken).build().create(ICatalogApi.class);
//        return api.getPictureIds();
//    }

}
