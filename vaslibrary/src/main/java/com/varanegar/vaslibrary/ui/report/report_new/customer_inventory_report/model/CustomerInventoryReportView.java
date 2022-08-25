package com.varanegar.vaslibrary.ui.report.report_new.customer_inventory_report.model;

import com.varanegar.framework.database.model.ModelProjection;
import com.varanegar.vaslibrary.ui.report.report_new.customer_purchase_history_report.model.PCustomerPurchaseHistoryViewModel;

public class CustomerInventoryReportView extends ModelProjection<PCustomerInventoryReportModel> {
    protected CustomerInventoryReportView(String name) {
        super(name);
    }


    public static CustomerInventoryReportView customerCode = new
            CustomerInventoryReportView
            ("PCustomerInventoryReportModel.customerCode");

    public static CustomerInventoryReportView customerName = new
            CustomerInventoryReportView
            ("PCustomerInventoryReportModel.customerName");

    public static CustomerInventoryReportView saleDate = new
            CustomerInventoryReportView
            ("PCustomerInventoryReportModel.saleDate");


    public static CustomerInventoryReportView productCode = new
            CustomerInventoryReportView
            ("PCustomerInventoryReportModel.productCode");

    public static CustomerInventoryReportView productName = new
            CustomerInventoryReportView
            ("PCustomerInventoryReportModel.productName");

    public static CustomerInventoryReportView tedadKochektarinVahed = new
            CustomerInventoryReportView
            ("PCustomerInventoryReportModel.tedadKochektarinVahed");

    public static CustomerInventoryReportView productionDate = new
            CustomerInventoryReportView
            ("PCustomerInventoryReportModel.productionDate");

    public static CustomerInventoryReportView inventoryCustomer = new
            CustomerInventoryReportView
            ("PCustomerInventoryReportModel.inventoryCustomer");

    public static CustomerInventoryReportView personnelName = new
            CustomerInventoryReportView
            ("PCustomerInventoryReportModel.personnelName");

    public static CustomerInventoryReportView pathTitle = new
            CustomerInventoryReportView
            ("PCustomerInventoryReportModel.pathTitle");

    public static CustomerInventoryReportView uniqueId = new
            CustomerInventoryReportView
            ("PCustomerInventoryReportModel.uniqueId");

    public static CustomerInventoryReportView delear = new
            CustomerInventoryReportView
            ("PCustomerInventoryReportModel.delear");

    public static CustomerInventoryReportView productGroupName = new
            CustomerInventoryReportView
            ("PCustomerInventoryReportModel.productGroupName");

    public static CustomerInventoryReportView productGroupNameID = new
            CustomerInventoryReportView
            ("PCustomerInventoryReportModel.productGroupNameID");
}
