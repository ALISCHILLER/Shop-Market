package com.varanegar.vaslibrary.ui.report.report_new.products_purchase_history_report.model;

import com.varanegar.framework.database.model.ModelProjection;


public class ProductsPurchaseHistoryReportView  extends
        ModelProjection<TProductsPurchaseHistoryReportViewModel> {

    protected ProductsPurchaseHistoryReportView(String name) {
        super(name);
    }

    public static ProductsPurchaseHistoryReportView ProductBackOfficeCode = new
            ProductsPurchaseHistoryReportView
            ("ProductsPurchaseHistoryReportView.ProductBackOfficeCode");



    public static ProductsPurchaseHistoryReportView ProductName = new
            ProductsPurchaseHistoryReportView("ProductsPurchaseHistoryReportView.ProductName");

    public static ProductsPurchaseHistoryReportView ProductCategoryCode = new
            ProductsPurchaseHistoryReportView
            ("ProductsPurchaseHistoryReportView.ProductCategoryCode");


    public static ProductsPurchaseHistoryReportView ProductCategoryName = new
            ProductsPurchaseHistoryReportView
            ("ProductsPurchaseHistoryReportView.ProductCategoryName");

    public static ProductsPurchaseHistoryReportView ProductNetWeight = new
            ProductsPurchaseHistoryReportView
            ("ProductsPurchaseHistoryReportView.ProductNetWeight");

    public static ProductsPurchaseHistoryReportView ProductNetCount_CA = new
            ProductsPurchaseHistoryReportView
            ("ProductsPurchaseHistoryReportView.ProductNetCount_CA");
}
