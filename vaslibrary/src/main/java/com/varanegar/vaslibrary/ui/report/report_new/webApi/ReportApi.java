package com.varanegar.vaslibrary.ui.report.report_new.webApi;

import android.content.Context;

import com.varanegar.vaslibrary.model.sendAnswersQustion.SyncGetTourModel;
import com.varanegar.vaslibrary.ui.report.report_new.customerNoSaleReport.model.CustomerNoSaleModel;
import com.varanegar.vaslibrary.ui.report.report_new.customer_group_sales_summary.model.ProductCustomerGroupSalesSummaryModel;
import com.varanegar.vaslibrary.ui.report.report_new.customer_purchase_history_report.model.PCustomerPurchaseHistoryViewModel;
import com.varanegar.vaslibrary.ui.report.report_new.invoice_balance.model.ProductInvoiveBalanceReportModel;
import com.varanegar.vaslibrary.ui.report.report_new.products_purchase_history_report.model.TProductsPurchaseHistoryReportModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;

/**
 * api گزارش ها
 */
public class ReportApi extends BaseApi implements InReportApi {
    public ReportApi(Context context) {
        super(context);
    }


    @Override
    public Call<List<ProductInvoiveBalanceReportModel>> product(List<String> dealerId, String startDate, String endDate) {
        return getRetrofitBuilder(TokenType.UserToken).build().create(InReportApi.class)
                .product(dealerId, startDate, endDate);
    }

    @Override
    public Call<List<ProductCustomerGroupSalesSummaryModel>> CustomerGroupSales(List<String> dealersId, String startDate, String endDate) {
        return  getRetrofitBuilder(TokenType.UserToken).build().create(InReportApi.class)
                .CustomerGroupSales(dealersId, startDate, endDate);
    }

    @Override
    public Call<List<PCustomerPurchaseHistoryViewModel>> CustomerPurchaseHistoryReport(List<String> dealersId, List<String> CustomersId, String startDate, String endDate) {
        return  getRetrofitBuilder(TokenType.UserToken).build().create(InReportApi.class)
                .CustomerPurchaseHistoryReport(dealersId,CustomersId, startDate, endDate);
    }

    @Override
    public Call<List<TProductsPurchaseHistoryReportModel>> ProductsPurchaseHistoryReport
            (List<String> dealersId, String startDate, String endDate) {

        return  getRetrofitBuilder(TokenType.UserToken).build().create(InReportApi.class)
                .ProductsPurchaseHistoryReport(dealersId, startDate, endDate);
    }

    @Override
    public Call<List<CustomerNoSaleModel>> CustomerNoSaleReport(List<String> dealersId, String startDate, String endDate, List<String> productCategoriesId) {
        return  getRetrofitBuilder(TokenType.UserToken).build().create(InReportApi.class)
                .CustomerNoSaleReport(dealersId,startDate,endDate,productCategoriesId);
    }

    @Override
    public Call<Void> savetourdata(SyncGetTourModel syncGetTourModel) {
        return  getRetrofitBuilder(TokenType.UserToken).build().create(InReportApi.class)
                .savetourdata(syncGetTourModel);
    }


}
