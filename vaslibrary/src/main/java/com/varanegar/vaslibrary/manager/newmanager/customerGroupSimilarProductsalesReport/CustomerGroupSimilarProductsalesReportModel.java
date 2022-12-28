package com.varanegar.vaslibrary.manager.newmanager.customerGroupSimilarProductsalesReport;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

@Table
public class CustomerGroupSimilarProductsalesReportModel extends BaseModel {

    @Column
    public UUID customerUniqueId;
    @Column
    public String ProductName;
    @Column
    public String ProductCode;

}
