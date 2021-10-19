package com.varanegar.vaslibrary.model.oldinvoicedetailview;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by atp on 4/10/2017.
 */
@Table
public class OldInvoiceDetailViewModel extends BaseModel {
    @Column
    public UUID SaleId;
    @Column
    public String SaleNo;
    @Column
    public UUID StockId;
    @Column
    public UUID ProductId;
    @Column
    public BigDecimal AddAmount;
    @Column
    public BigDecimal UnitQty;
    @Column
    public BigDecimal TotalQty;
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
    public  Currency TotalRequestAmount;
    @Column
    public UUID CustomerId;
    @Column
    public Currency TotalAmount;
    @Column
    public String SalePDate;
    @Column
    public boolean PrizeType;
    @Column
    public String Item;
}
