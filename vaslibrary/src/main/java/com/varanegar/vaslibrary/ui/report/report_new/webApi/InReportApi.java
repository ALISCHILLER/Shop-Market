package com.varanegar.vaslibrary.ui.report.report_new.webApi;

import com.varanegar.vaslibrary.ui.report.report_new.customer_group_sales_summary.model.ProductCustomerGroupSalesSummaryViewModel;
import com.varanegar.vaslibrary.ui.report.report_new.customer_purchase_history_report.model.PCustomerPurchaseHistoryViewModel;
import com.varanegar.vaslibrary.ui.report.report_new.invoice_balance.model.ProductInvoiveBalanceReportViewModel;
import com.varanegar.vaslibrary.ui.report.report_new.products_purchase_history_report.model.TProductsPurchaseHistoryReportViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface InReportApi {

    @GET("api/v2/ngt/ReviewReport/InvoiceRemain")
    Call<List<ProductInvoiveBalanceReportViewModel>> product
            (@Query("DealersId") List<String> dealersId,
             @Query("StartDate") String startDate,
             @Query("EndDate") String endDate);

    @GET("api/v2/ngt/ReviewReport/CustomerGroupSaleReport")
    Call<List<ProductCustomerGroupSalesSummaryViewModel>>
    CustomerGroupSales(@Query("DealersId") List<String> dealersId,
                       @Query("StartDate") String startDate,
                       @Query("EndDate") String endDate);

    @GET("api/v2/ngt/ReviewReport/CustomerPurchaseHistoryReport")
    Call<List<PCustomerPurchaseHistoryViewModel>>
    CustomerPurchaseHistoryReport(@Query("DealersId") List<String> dealersId,
                                  @Query("CustomersId") List<String> CustomersId,
                                  @Query("StartDate") String startDate,
                                  @Query("EndDate") String endDate);

    @GET("api/v2/ngt/ReviewReport/ProductsPurchaseHistoryReport")
    Call<List<TProductsPurchaseHistoryReportViewModel>> ProductsPurchaseHistoryReport
            (@Query("DealersId") List<String> dealersId,
             @Query("StartDate") String startDate,
             @Query("EndDate") String endDate);

    @GET("api/v2/ngt/ReviewReport/CustomerNoSaleReport")
    Call<List<String>> CustomerNoSaleReport
            (@Query("DealersId") List<String> dealersId,
             @Query("StartDate") String startDate,
             @Query("EndDate") String endDate,
             @Query("ProductCategoriesId") List<String> productCategoriesId);

}
