package com.varanegar.vaslibrary.ui.report.report_new.customerNoSaleReport.model;

import com.varanegar.framework.database.model.ModelProjection;

public class CustomerModelView extends
        ModelProjection<CustomerNoSaleModel> {
    protected CustomerModelView(String name) {
        super(name);
    }
    public static CustomerModelView BackOfficeId = new
            CustomerModelView
            ("CustomerNoSale.BackOfficeId");

    public static CustomerModelView CustomerName = new
            CustomerModelView
            ("CustomerNoSale.CustomerName");

    public static CustomerModelView CustomerCode = new
            CustomerModelView
            ("CustomerNoSale.CustomerCode");

    public static CustomerModelView Phone = new
            CustomerModelView
            ("CustomerNoSale.Phone");

    public static CustomerModelView Address = new
            CustomerModelView
            ("CustomerNoSale.Address");


    public static CustomerModelView StoreName = new
            CustomerModelView
            ("CustomerNoSale.StoreName");

    public static CustomerModelView CustomerActivity = new
            CustomerModelView
            ("CustomerNoSale.CustomerActivity");

    public static CustomerModelView CustomerCategory = new
            CustomerModelView
            ("CustomerNoSale.CustomerCategory");
    public static CustomerModelView Mobile = new
            CustomerModelView
            ("CustomerNoSale.Mobile");

    public static CustomerModelView NationalCode = new
            CustomerModelView
            ("CustomerNoSale.NationalCode");
    public static CustomerModelView CustomerModelTbl = new
            CustomerModelView
            ("CustomerNoSale");
}
