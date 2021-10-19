package com.varanegar.vaslibrary.webapi.reviewreport;

import android.content.Context;

import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Torabi on 5/16/2018.
 */

public class ReviewReportApi extends BaseApi implements IReviewReportApi {
    public ReviewReportApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<OrderReviewReportViewModel>> order(String dealerId, String startDate, String endDate) {
        return getRetrofitBuilder(TokenType.UserToken).build().create(IReviewReportApi.class).order(dealerId, startDate, endDate);
    }

    @Override
    public Call<List<SellReviewReportViewModel>> sell(String dealerId, String startDate, String endDate) {
        return getRetrofitBuilder(TokenType.UserToken).build().create(IReviewReportApi.class).sell(dealerId, startDate, endDate);
    }

    @Override
    public Call<List<SellReturnReviewReportViewModel>> sellReturn(String dealerId, String startDate, String endDate) {
        return getRetrofitBuilder(TokenType.UserToken).build().create(IReviewReportApi.class).sellReturn(dealerId, startDate, endDate);
    }

    @Override
    public Call<List<ProductReviewReportViewModel>> product(String dealerId, String startDate, String endDate) {
        return getRetrofitBuilder(TokenType.UserToken).build().create(IReviewReportApi.class).product(dealerId, startDate, endDate);
    }

    @Override
    public Call<List<ProductGroupReviewReportViewModel>> productGroup(String dealerId, String startDate, String endDate) {
        return getRetrofitBuilder(TokenType.UserToken).build().create(IReviewReportApi.class).productGroup(dealerId, startDate, endDate);
    }

    public Call<List<TargetReviewReportViewModel>> target(String dealerId) {
        return getRetrofitBuilder(TokenType.UserToken).build().create(IReviewReportApi.class).target(dealerId);
    }
}
