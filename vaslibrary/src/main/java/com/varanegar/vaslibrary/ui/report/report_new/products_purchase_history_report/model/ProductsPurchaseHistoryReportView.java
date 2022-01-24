package com.varanegar.vaslibrary.ui.report.report_new.products_purchase_history_report.model;

import com.varanegar.framework.database.model.ModelProjection;


public class ProductsPurchaseHistoryReportView  extends
        ModelProjection<TProductsPurchaseHistoryReportModel> {

    protected ProductsPurchaseHistoryReportView(String name) {
        super(name);
    }

    public static ProductsPurchaseHistoryReportView ProductBackOfficeCode = new
            ProductsPurchaseHistoryReportView
            ("TProductsPurchaseHistoryReport.ProductBackOfficeCode");



    public static ProductsPurchaseHistoryReportView ProductName = new
            ProductsPurchaseHistoryReportView("TProductsPurchaseHistoryReport.ProductName");

    public static ProductsPurchaseHistoryReportView ProductCategoryCode = new
            ProductsPurchaseHistoryReportView
            ("TProductsPurchaseHistoryReport.ProductCategoryCode");


    public static ProductsPurchaseHistoryReportView ProductCategoryName = new
            ProductsPurchaseHistoryReportView
            ("TProductsPurchaseHistoryReport.ProductCategoryName");

    public static ProductsPurchaseHistoryReportView ProductNetWeight = new
            ProductsPurchaseHistoryReportView
            ("TProductsPurchaseHistoryReport.ProductNetWeight");

    public static ProductsPurchaseHistoryReportView ProductNetCount_CA = new
            ProductsPurchaseHistoryReportView
            ("TProductsPurchaseHistoryReport.ProductNetCount_CA");
    public static ProductsPurchaseHistoryReportView TProductsPurchaseHistoryReportTbl = new
            ProductsPurchaseHistoryReportView
            ("TProductsPurchaseHistoryReport");
}
