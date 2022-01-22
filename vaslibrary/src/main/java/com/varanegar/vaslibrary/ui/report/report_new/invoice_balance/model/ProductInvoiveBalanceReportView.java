package com.varanegar.vaslibrary.ui.report.report_new.invoice_balance.model;

import com.varanegar.framework.database.model.ModelProjection;

public class ProductInvoiveBalanceReportView extends ModelProjection<ProductInvoiveBalanceReportViewModel> {
    protected ProductInvoiveBalanceReportView(String name) {
        super(name);
    }

    public static ProductInvoiveBalanceReportView CustomerBackOfficeCode = new
            ProductInvoiveBalanceReportView
            ("ProductInvoiveBalanceReport.CustomerBackOfficeCode");

    public static ProductInvoiveBalanceReportView CustomerName = new
            ProductInvoiveBalanceReportView("ProductInvoiveBalanceReport.CustomerName");
    public static ProductInvoiveBalanceReportView InvoiceNumber = new
            ProductInvoiveBalanceReportView("ProductInvoiveBalanceReport.InvoiceNumber");

    public static ProductInvoiveBalanceReportView InvoiceShmsiDate = new
            ProductInvoiveBalanceReportView("ProductInvoiveBalanceReport.InvoiceShmsiDate");

    public static ProductInvoiveBalanceReportView InvoiceOverDue = new
            ProductInvoiveBalanceReportView("ProductInvoiveBalanceReport.InvoiceOverDue");
    public static ProductInvoiveBalanceReportView InvoiceFinalPrice = new
            ProductInvoiveBalanceReportView("ProductInvoiveBalanceReport.InvoiceFinalPrice");


    public static ProductInvoiveBalanceReportView PaidPose = new
            ProductInvoiveBalanceReportView("ProductInvoiveBalanceReport.PaidPose");

    public static ProductInvoiveBalanceReportView PaidCash = new
            ProductInvoiveBalanceReportView("ProductInvoiveBalanceReport.PaidCash");


    public static ProductInvoiveBalanceReportView PaidCheck = new
            ProductInvoiveBalanceReportView("ProductInvoiveBalanceReport.PaidCheck");


    public static ProductInvoiveBalanceReportView IvoiceRemain = new
            ProductInvoiveBalanceReportView("ProductInvoiveBalanceReport.IvoiceRemain");


    public static ProductInvoiveBalanceReportView UsancePaid = new
            ProductInvoiveBalanceReportView("ProductInvoiveBalanceReport.UsancePaid");

    public static ProductInvoiveBalanceReportView ProductInvoiveBalanceReportTbl =
            new ProductInvoiveBalanceReportView("ProductInvoiveBalanceReport");

    public static ProductInvoiveBalanceReportView ProductInvoiveBalanceReportAll =
            new ProductInvoiveBalanceReportView("ProductInvoiveBalanceReport.*");
}
