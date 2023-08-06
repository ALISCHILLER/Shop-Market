package com.varanegar.vaslibrary.webapi.onhandqty;

import com.varanegar.vaslibrary.model.productobatchnhandqtyviewmodek.ProductBatchOnHandViewModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 7/10/2018.
 */

public interface IBatchOnHandQtyApi {
    @GET("api/v2/ngt/product/BatchOnHandQty/sync/loaddata")
    Call<ProductBatchOnHandViewModel> getAll(@Query("dealerId") String dealerId
            , @Query("DeviceSettingNo") String deviceSettingNo,
                                             @Query("CustomerCode")String customerCode
    );
}
