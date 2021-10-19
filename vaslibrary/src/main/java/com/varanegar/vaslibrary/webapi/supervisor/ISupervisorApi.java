package com.varanegar.vaslibrary.webapi.supervisor;

import com.varanegar.vaslibrary.model.TourStatusSummaryViewModel;
import com.varanegar.vaslibrary.ui.report.review.CustomerCallViewModel;
import com.varanegar.vaslibrary.ui.report.review.TourCustomerSummaryViewModel;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by A.Torabi on 6/7/2018.
 */

public interface ISupervisorApi {
    @GET("api/v2/ngt/tour")
    Call<List<TourStatusSummaryViewModel>> tour(@Query("TourViewType") int tourViewType,
                                                @Query("Show_ReadyToSend") boolean Show_ReadyToSend,
                                                @Query("Show_Sent") boolean Show_Sent,
                                                @Query("Show_InProgress") boolean Show_InProgress,
                                                @Query("Show_Received") boolean Show_Received,
                                                @Query("Show_Finished") boolean Show_Finished,
                                                @Query("Show_Canceled") boolean Show_Canceled,
                                                @Query("Show_Deactivated") boolean Show_Deactivated,
                                                @Query("From_Date") String from_date,
                                                @Query("To_Date") String to_date);

    @GET("api/v2/ngt/Customercall/summary/{TourId}")
    Call<List<TourCustomerSummaryViewModel>> tourCustomers(@Path("TourId") String tourId);

    @GET("api/v2/ngt/Tour/PreSale/DeactivateTour")
    Call<ResponseBody> deactivateTour(@Query("Id") String tourId);

    @GET("api/v2/ngt/Tour/PreSale/Replicate/{Id}")
    Call<ResponseBody> replicate(@Path("Id") String tourId);

    @GET("api/v2/ngt/Customercall/{Id}")
    Call<CustomerCallViewModel> customerCalls(@Path("Id") String tourId);

//    @POST("api/dsd/tracking/ldlstpnt")
//    Call<List<EventViewModel>> loadLastPoints(@Body LastPointsParam parameter);
//
//    @GET("api/v2/ngt/Supervisor/{supervisorId}/personels")
//    Call<List<VisitorModel>> getVisitors(@Path("supervisorId") String supervisorId);
//
//    @GET("api/v2/ngt/reviewreport/Order")
//    Call<List<OrderReviewReportViewModel>> order(@Query("DealerId") String dealerId, @Query("StartDate") String startDate, @Query("EndDate") String endDate);
//
//    @GET("api/v2/ngt/reviewreport/Sell")
//    Call<List<SellReviewReportViewModel>> sell(@Query("DealerId") String dealerId, @Query("StartDate") String startDate, @Query("EndDate") String endDate);
//
//    @GET("api/v2/ngt/reviewreport/SellReturn")
//    Call<List<SellReturnReviewReportViewModel>> sellReturn(@Query("DealerId") String dealerId, @Query("StartDate") String startDate, @Query("EndDate") String endDate);
//
//    @GET("api/v2/ngt/reviewreport/Product")
//    Call<List<ProductReviewReportViewModel>> product(@Query("DealerId") String dealerId, @Query("StartDate") String startDate, @Query("EndDate") String endDate);
//
//    @GET("api/v2/ngt/reviewreport/ProductGroup")
//    Call<List<ProductGroupReviewReportViewModel>> productGroup(@Query("DealerId") String dealerId, @Query("StartDate") String startDate, @Query("EndDate") String endDate);
}
