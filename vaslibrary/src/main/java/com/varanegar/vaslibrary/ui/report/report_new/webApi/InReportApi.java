package com.varanegar.vaslibrary.ui.report.report_new.webApi;

import com.varanegar.vaslibrary.model.sendAnswersQustion.SyncGetTourModel;
import com.varanegar.vaslibrary.ui.report.report_new.customerNoSaleReport.model.CustomerNoSaleModel;
import com.varanegar.vaslibrary.ui.report.report_new.customer_group_sales_summary.model.ProductCustomerGroupSalesSummaryModel;
import com.varanegar.vaslibrary.ui.report.report_new.customer_inventory_report.model.InventoryRequest;
import com.varanegar.vaslibrary.ui.report.report_new.customer_inventory_report.model.PCustomerInventoryReportModel;
import com.varanegar.vaslibrary.ui.report.report_new.customer_purchase_history_report.model.PCustomerPurchaseHistoryViewModel;
import com.varanegar.vaslibrary.ui.report.report_new.invoice_balance.model.ProductInvoiveBalanceReportModel;
import com.varanegar.vaslibrary.ui.report.report_new.products_purchase_history_report.model.TProductsPurchaseHistoryReportModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface InReportApi {

    @POST("api/v2/ngt/customerinventoryreport")
    Call<List<PCustomerInventoryReportModel>> inventoryReport(
            @Body InventoryRequest request
            );


    @GET("api/v2/ngt/ReviewReport/InvoiceRemain")
    Call<List<ProductInvoiveBalanceReportModel>> product
            (@Query("DealersId") List<String> dealersId,
             @Query("StartDate") String startDate,
             @Query("EndDate") String endDate);

    @GET("api/v2/ngt/ReviewReport/CustomerGroupSaleReport")
    Call<List<ProductCustomerGroupSalesSummaryModel>>
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
    Call<List<TProductsPurchaseHistoryReportModel>> ProductsPurchaseHistoryReport
            (@Query("DealersId") List<String> dealersId,
             @Query("StartDate") String startDate,
             @Query("EndDate") String endDate);

    @GET("api/v2/ngt/ReviewReport/CustomerNoSaleReport")
    Call<List<CustomerNoSaleModel>> CustomerNoSaleReport
            (@Query("DealersId") List<String> dealersId,
             @Query("StartDate") String startDate,
             @Query("EndDate") String endDate,
             @Query("ProductCategoriesId") List<String> productCategoriesId);

    @POST("api/v2/ngt/tour/supervisor/savetourdata")
    Call<Void> savetourdata(@Body SyncGetTourModel syncGetTourModel);

}
