package com.varanegar.vaslibrary.manager.newmanager.customerXmounthsalereport;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;
import java.util.UUID;
@Table
public class CustomerXMounthSaleReportModel extends BaseModel {

    @Column
    public String customerCode;
    @Column
    public String ProductName;
    @Column
    public String ProductCode;

}