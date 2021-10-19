package com.varanegar.vaslibrary.model.oldinvoicedetailreportview;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 8/19/2017.
 */
@Table
public class OldInvoiceDetailReportViewModel extends BaseModel
{
    @Column
    public UUID ProductId;
    @Column
    public BigDecimal AddAmount;
    @Column
    public BigDecimal UnitQty;
    @Column
    public BigDecimal TotalQty;
    @Column
    public String UnitName;
    @Column
    public Currency UnitPrice;
    @Column
    public String ProductName;
    @Column
    public String ProductCode;
    @Column
    public UUID ProductGroupId;
    @Column
    public BigDecimal TotalReturnQty;
    @Column
    public UUID CustomerId;
    @Column
    public Currency TotalAmount;
    @Column
    public UUID SaleId;
    @Column
    public String SaleNo;
    @Column
    public String SalePDate;
}
