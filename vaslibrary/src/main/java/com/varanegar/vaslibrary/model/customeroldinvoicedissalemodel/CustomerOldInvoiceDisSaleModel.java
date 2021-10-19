package com.varanegar.vaslibrary.model.customeroldinvoicedissalemodel;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

/**
 * Created by A.Jafarzadeh on 1/21/2018.
 */
@Table
public class CustomerOldInvoiceDisSaleModel extends BaseModel {
    @Column
    public int BackOfficeId;
    @Column
    public int HdrRef;
    @Column
    public int ItemRef;
    @Column
    public double RowNo;
    @Column
    public int ItemType;
    @Column
    public int DisRef;
    @Column
    public int DisGroup;
    @Column
    public Currency Discount;
    @Column
    public Currency AddAmount;
}
