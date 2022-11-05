package com.varanegar.vaslibrary.ui.report.report_new.customer_inventory_report.model;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

/**
 * Create by Mehrdad Latifi on 8/23/2022
 */

@Table
public class PCustomerInventoryReportModel extends BaseModel {
    @Column
    public String customerCode;
    @Column
    public String customerName;
    @Column
    public String saleDate;
    @Column
    public String productCode;
    @Column
    public String productName;
    @Column
    public String tedadKochektarinVahed ;
    @Column
    public String productionDate;
    @Column
    public String inventoryCustomer ;
    @Column
    public String personnelName ;
    @Column
    public String pathTitle ;
    @Column
    public String delear ;
    @Column
    public String productGroupName ;
    @Column
    public String productGroupNameID ;

}
