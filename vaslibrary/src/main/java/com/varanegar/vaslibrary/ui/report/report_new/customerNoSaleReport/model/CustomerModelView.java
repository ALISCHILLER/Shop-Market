package com.varanegar.vaslibrary.ui.report.report_new.customerNoSaleReport.model;

import com.varanegar.framework.database.model.ModelProjection;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.ui.report.report_new.products_purchase_history_report.model.ProductsPurchaseHistoryReportView;
import com.varanegar.vaslibrary.ui.report.report_new.products_purchase_history_report.model.TProductsPurchaseHistoryReportViewModel;

public class CustomerModelView extends
        ModelProjection<CustomerModel> {
    protected CustomerModelView(String name) {
        super(name);
    }
    public static CustomerModelView BackOfficeId = new
            CustomerModelView
            ("CustomerModelView.BackOfficeId");

    public static CustomerModelView CustomerName = new
            CustomerModelView
            ("CustomerModelView.CustomerName");

    public static CustomerModelView CustomerCode = new
            CustomerModelView
            ("CustomerModelView.CustomerCode");

    public static CustomerModelView Phone = new
            CustomerModelView
            ("CustomerModelView.Phone");

    public static CustomerModelView StoreName = new
            CustomerModelView
            ("CustomerModelView.StoreName");

    public static CustomerModelView Mobile = new
            CustomerModelView
            ("CustomerModelView.Mobile");

    public static CustomerModelView NationalCode = new
            CustomerModelView
            ("CustomerModelView.NationalCode");
}
