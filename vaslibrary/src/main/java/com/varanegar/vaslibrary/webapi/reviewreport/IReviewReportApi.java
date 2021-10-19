package com.varanegar.vaslibrary.webapi.reviewreport;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Torabi on 5/16/2018.
 */

public interface IReviewReportApi {
    @GET("api/v2/ngt/reviewreport/Order")
    Call<List<OrderReviewReportViewModel>> order(@Query("DealerId") String dealerId, @Query("StartDate") String startDate, @Query("EndDate") String endDate);

    @GET("api/v2/ngt/reviewreport/Sell")
    Call<List<SellReviewReportViewModel>> sell(@Query("DealerId") String dealerId, @Query("StartDate") String startDate, @Query("EndDate") String endDate);

    @GET("api/v2/ngt/reviewreport/SellReturn")
    Call<List<SellReturnReviewReportViewModel>> sellReturn(@Query("DealerId") String dealerId, @Query("StartDate") String startDate, @Query("EndDate") String endDate);

    @GET("api/v2/ngt/reviewreport/Product")
    Call<List<ProductReviewReportViewModel>> product(@Query("DealerId") String dealerId, @Query("StartDate") String startDate, @Query("EndDate") String endDate);

    @GET("api/v2/ngt/reviewreport/ProductGroup")
    Call<List<ProductGroupReviewReportViewModel>> productGroup(@Query("DealerId") String dealerId, @Query("StartDate") String startDate, @Query("EndDate") String endDate);

    @GET("api/v2/ngt/target/backoffice/loadreport")
    Call<List<TargetReviewReportViewModel>> target(@Query("DealerId") String dealerId);
}
