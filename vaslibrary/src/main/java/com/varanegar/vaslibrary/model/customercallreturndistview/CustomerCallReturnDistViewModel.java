package com.varanegar.vaslibrary.model.customercallreturndistview;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * Created by A.Torabi on 3/2/2019.
 */
@Table
public class CustomerCallReturnDistViewModel extends BaseModel {
    @Column
    public UUID ProductUniqueId;
    @Column
    public boolean IsFreeItem;
    @Column
    public boolean IsPromoLine;
    @Column
    public Currency RequestUnitPrice;
    @Column
    public Currency TotalRequestAmount; // request amount for IsPromoLine = false
    @Column
    public Currency TotalRequestAdd1Amount;
    @Column
    public Currency TotalRequestAdd2Amount;
    @Column
    public Currency TotalRequestDiscount;
    @Column
    public Currency TotalRequestTax;
    @Column
    public Currency TotalRequestCharge;
    @Column
    public Currency TotalRequestNetAmount; // price from promotion if IsPromoLine is true
    @Column
    public int SortId;
    @Column
    public int IndexInfo;
    @Column
    public BigDecimal Weight;
    @Column
    public String ReferenceId;
    @Column
    public int ReferenceNo;
    @Column
    public Date ReferenceDate;
    @Column
    public String CurrentType;
    @Column
    public BigDecimal CurrentQty;
    @Column
    public BigDecimal RequestBulkQty;
    @Column
    public UUID RequestBulkUnitId;
    @Column
    public UUID InvoiceId;
    @Column
    public Integer SaleNo;
    @Column
    public UUID ReturnReasonId;
    @Column
    public UUID ReturnProductTypeId;
    @Column
    public UUID CustomerUniqueId;
    @Column
    public String ProductName;
    @Column
    public String ProductCode;
    @Column
    public UUID ProductId;
    @Column
    public String Qty;
    @Column
    public String ConvertFactor;
    @Column
    public String ProductUnitId;
    @Column
    public String UnitName;
    @Column
    public BigDecimal TotalReturnQty;
    @Column
    public UUID StockId;
}
