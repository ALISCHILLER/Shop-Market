package com.varanegar.vaslibrary.webapi.catalog;

import com.varanegar.vaslibrary.model.productgroupcatalog.ProductGroupCatalogModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Torabi on 8/6/2017.
 */

public interface IProductGroupCatalogApi {
    @GET("api/v2/ngt/catalog/sync/loaddata")
    Call<List<ProductGroupCatalogModel>> getCatalogs();
}
