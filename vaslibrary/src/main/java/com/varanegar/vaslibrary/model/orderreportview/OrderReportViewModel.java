package com.varanegar.vaslibrary.model.orderreportview;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

/**
 * Created by A.Jafarzadeh on 8/10/2017.
 */
@Table
public class OrderReportViewModel extends BaseModel
{
    @Column
    public String ProductCode;

    @Column
    public String ProductName;

    @Column
    public String InvoiceBulkQty;
}
