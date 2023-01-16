package com.varanegar.vaslibrary.manager.newmanager.customerGroupSimilarProductsalesReport;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

@Table
public class CustomerGroupSimilarProductsalesReportModel extends BaseModel {

    @Column
    public String customerCode;
    @Column
    public String ProductName;
    @Column
    public String ProductCode;

}
