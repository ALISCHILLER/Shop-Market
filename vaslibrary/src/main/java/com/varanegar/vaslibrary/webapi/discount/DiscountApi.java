package com.varanegar.vaslibrary.webapi.discount;

import android.content.Context;

import com.varanegar.vaslibrary.model.discountSDS.DiscountConditionModel;
import com.varanegar.vaslibrary.model.discountSDS.DiscountItemCountModel;
import com.varanegar.vaslibrary.model.discountSDS.DiscountSDSModel;
import com.varanegar.vaslibrary.model.discountSDS.DiscountVnLtModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 6/24/2017.
 */

public class DiscountApi extends BaseApi implements IDiscountApi
{

    public DiscountApi(Context context)
    {
        super(context);
    }

//    @Override
//    public Call<List<DiscountSDSModel>> getDiscountSDS(@Query("Date") String dateAfter)
//    {
//        IDiscountApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IDiscountApi.class);
//        return api.getDiscountSDS(dateAfter);
//    }

    @Override
    public Call<List<DiscountVnLtModel>> getDiscountVnLite(@Query("Date") String dateAfter)
    {
        IDiscountApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IDiscountApi.class);
        return api.getDiscountVnLite(dateAfter);
    }
//    @Override
//    public Call<List<DiscountConditionModel>> getDiscountConditions(@Query("Date") String dateAfter) {
//        IDiscountApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IDiscountApi.class);
//        return api.getDiscountConditions(dateAfter);
//    }

//    @Override
//    public Call<List<DiscountItemCountModel>> getDiscountItemCounts(@Query("Date") String dateAfter) {
//        IDiscountApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IDiscountApi.class);
//        return api.getDiscountItemCounts(dateAfter);
//    }
}
