package com.varanegar.vaslibrary.ui.report.report_new.webApi;

import android.content.Context;

import com.varanegar.vaslibrary.ui.report.report_new.customer_group_sales_summary.model.ProductCustomerGroupSalesSummaryViewModel;
import com.varanegar.vaslibrary.ui.report.report_new.customer_purchase_history_report.model.PCustomerPurchaseHistoryViewModel;
import com.varanegar.vaslibrary.ui.report.report_new.invoice_balance.model.ProductInvoiveBalanceReportViewModel;
import com.varanegar.vaslibrary.ui.report.report_new.products_purchase_history_report.model.TProductsPurchaseHistoryReportViewModel;
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
    public Call<List<ProductInvoiveBalanceReportViewModel>> product(List<String> dealerId, String startDate, String endDate) {
        return getRetrofitBuilder(TokenType.UserToken).build().create(InReportApi.class)
                .product(dealerId, startDate, endDate);
    }

    @Override
    public Call<List<ProductCustomerGroupSalesSummaryViewModel>> CustomerGroupSales(List<String> dealersId, String startDate, String endDate) {
        return  getRetrofitBuilder(TokenType.UserToken).build().create(InReportApi.class)
                .CustomerGroupSales(dealersId, startDate, endDate);
    }

    @Override
    public Call<List<PCustomerPurchaseHistoryViewModel>> CustomerPurchaseHistoryReport(List<String> dealersId, List<String> CustomersId, String startDate, String endDate) {
        return  getRetrofitBuilder(TokenType.UserToken).build().create(InReportApi.class)
                .CustomerPurchaseHistoryReport(dealersId,CustomersId, startDate, endDate);
    }

    @Override
    public Call<List<TProductsPurchaseHistoryReportViewModel>> ProductsPurchaseHistoryReport
            (List<String> dealersId, String startDate, String endDate) {

        return  getRetrofitBuilder(TokenType.UserToken).build().create(InReportApi.class)
                .ProductsPurchaseHistoryReport(dealersId, startDate, endDate);
    }

    @Override
    public Call<List<String>> CustomerNoSaleReport(List<String> dealersId, String startDate, String endDate, String productCategoriesId) {
        return  getRetrofitBuilder(TokenType.UserToken).build().create(InReportApi.class)
                .CustomerNoSaleReport(dealersId,startDate,endDate,productCategoriesId);
    }


}
