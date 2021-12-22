package com.varanegar.vaslibrary.ui.report.report_new.invoice_balance.model;

import com.varanegar.framework.database.model.ModelProjection;

public class ProductInvoiveBalanceReportView extends ModelProjection<ProductInvoiveBalanceReportViewModel> {
    protected ProductInvoiveBalanceReportView(String name) {
        super(name);
    }

    public static ProductInvoiveBalanceReportView CustomerBackOfficeCode = new
            ProductInvoiveBalanceReportView
            ("ProductInvoiveBalanceReportView.CustomerBackOfficeCode");

    public static ProductInvoiveBalanceReportView CustomerName = new
            ProductInvoiveBalanceReportView("ProductInvoiveBalanceReportView.CustomerName");
    public static ProductInvoiveBalanceReportView InvoiceNumber = new
            ProductInvoiveBalanceReportView("ProductInvoiveBalanceReportView.InvoiceNumber");

    public static ProductInvoiveBalanceReportView InvoiceShmsiDate = new
            ProductInvoiveBalanceReportView("ProductInvoiveBalanceReportView.InvoiceShmsiDate");

    public static ProductInvoiveBalanceReportView InvoiceOverDue = new
            ProductInvoiveBalanceReportView("ProductInvoiveBalanceReportView.InvoiceOverDue");
    public static ProductInvoiveBalanceReportView InvoiceFinalPrice = new
            ProductInvoiveBalanceReportView("ProductInvoiveBalanceReportView.InvoiceFinalPrice");


    public static ProductInvoiveBalanceReportView PaidPose = new
            ProductInvoiveBalanceReportView("ProductInvoiveBalanceReportView.PaidPose");

    public static ProductInvoiveBalanceReportView PaidCash = new
            ProductInvoiveBalanceReportView("ProductInvoiveBalanceReportView.PaidCash");


    public static ProductInvoiveBalanceReportView PaidCheck = new
            ProductInvoiveBalanceReportView("ProductInvoiveBalanceReportView.PaidCheck");


    public static ProductInvoiveBalanceReportView IvoiceRemain = new
            ProductInvoiveBalanceReportView("ProductInvoiveBalanceReportView.IvoiceRemain");


    public static ProductInvoiveBalanceReportView UsancePaid = new
            ProductInvoiveBalanceReportView("ProductInvoiveBalanceReportView.UsancePaid");


}
