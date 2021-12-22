package com.varanegar.vaslibrary.ui.report.report_new.customer_purchase_history_report.model;

import com.varanegar.framework.database.model.ModelProjection;
import com.varanegar.vaslibrary.ui.report.report_new.invoice_balance.model.ProductInvoiveBalanceReportView;

public class CustomerPurchaseHistoryView extends ModelProjection<PCustomerPurchaseHistoryViewModel> {
    protected CustomerPurchaseHistoryView(String name) {
        super(name);
    }

    public static CustomerPurchaseHistoryView CustomerName = new
            CustomerPurchaseHistoryView
            ("CustomerPurchaseHistoryView.CustomerName");

    public static CustomerPurchaseHistoryView InvoiceShamsiDate = new
            CustomerPurchaseHistoryView
            ("CustomerPurchaseHistoryView.InvoiceShamsiDate");


    public static CustomerPurchaseHistoryView InvoiceNumber = new
            CustomerPurchaseHistoryView
            ("CustomerPurchaseHistoryView.InvoiceNumber");
    public static CustomerPurchaseHistoryView ProductBackOfficeCode = new
            CustomerPurchaseHistoryView
            ("CustomerPurchaseHistoryView.ProductBackOfficeCode");

    public static CustomerPurchaseHistoryView ProductName = new
            CustomerPurchaseHistoryView
            ("CustomerPurchaseHistoryView.ProductName");

    public static CustomerPurchaseHistoryView Count_CA = new
            CustomerPurchaseHistoryView
            ("CustomerPurchaseHistoryView.Count_CA");

    public static CustomerPurchaseHistoryView Count_EA = new
            CustomerPurchaseHistoryView
            ("CustomerPurchaseHistoryView.Count_EA");

    public static CustomerPurchaseHistoryView Price = new
            CustomerPurchaseHistoryView
            ("CustomerPurchaseHistoryView.Price");

    public static CustomerPurchaseHistoryView Amount = new
            CustomerPurchaseHistoryView
            ("CustomerPurchaseHistoryView.Amount");
}
