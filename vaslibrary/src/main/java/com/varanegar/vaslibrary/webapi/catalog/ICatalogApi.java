package com.varanegar.vaslibrary.webapi.catalog;

import com.varanegar.vaslibrary.model.catalog.CatalogModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Torabi on 6/21/2017.
 */

public interface ICatalogApi {

    @GET("api/v2/ngt/catalog/sync/loaddata/product")
    Call<List<CatalogModel>> getCatalogs(@Query("Date") String date);

}
