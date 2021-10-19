package com.varanegar.vaslibrary.webapi.product;

import com.varanegar.vaslibrary.model.product.ProductTaxInfoModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IProductTaxInfoApi {
    @GET("api/v2/ngt/product/producttaxinfo/sync/loaddata")
    Call<List<ProductTaxInfoModel>> getAll();
}
