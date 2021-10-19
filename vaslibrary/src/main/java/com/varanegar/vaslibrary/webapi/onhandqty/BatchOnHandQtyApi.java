package com.varanegar.vaslibrary.webapi.onhandqty;

import android.content.Context;

import com.varanegar.vaslibrary.model.productobatchnhandqtyviewmodek.ProductBatchOnHandViewModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Jafarzadeh on 7/10/2018.
 */

public class BatchOnHandQtyApi extends BaseApi implements IBatchOnHandQtyApi {
    public BatchOnHandQtyApi(Context context) {
        super(context);
    }

    @Override
    public Call<ProductBatchOnHandViewModel> getAll(String dealerId , String deviceSettingNo) {
        IBatchOnHandQtyApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IBatchOnHandQtyApi.class);
        return api.getAll(dealerId , deviceSettingNo);
    }
}
