package com.varanegar.vaslibrary.webapi.product;

import com.varanegar.vaslibrary.model.product.ProductModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 6/14/2017.
 */

public interface IProductApi {
    @GET("api/v2/ngt/product/sync/loaddata")
    Call<List<ProductModel>> getAll(@Query("Date") String dateAfter, @Query("dealerId") String dealerId , @Query("SubSystemTypeUniqueId") String subSystemTypeUniqueId);
    @GET("api/v2/ngt/ProductTemplate/PersonnelProductData")
    Call<List<PersonnelProductTemplateViewModel>> getPersonnelProductData(@Query("PersonnelId") String dealerId, @Query("SubSystemTypeUniqueId") String subSystemTypeUniqueId);
}
