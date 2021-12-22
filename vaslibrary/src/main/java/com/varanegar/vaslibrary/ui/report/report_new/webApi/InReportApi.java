package com.varanegar.vaslibrary.ui.report.report_new.webApi;

import com.varanegar.vaslibrary.ui.report.report_new.model.ProductInvoiveBalanceReportViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IntInvoiceReportApi {

    @GET("api/v2/ngt/ReviewReport/InvoiceRemain")
    Call<List<ProductInvoiveBalanceReportViewModel>> product(@Query("DealersId") List<String> dealersId,
                                                             @Query("StartDate") String startDate,
                                                             @Query("EndDate") String endDate);

}
