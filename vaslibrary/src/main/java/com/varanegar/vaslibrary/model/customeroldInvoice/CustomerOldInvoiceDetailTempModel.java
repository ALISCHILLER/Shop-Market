package com.varanegar.vaslibrary.model.customeroldInvoice;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 8/15/2018.
 */
@Table
public class CustomerOldInvoiceDetailTempModel extends BaseModel {
    @Column
    public String SaleId;
    @Column
    @NotNull
    public UUID ProductId;
    @Column
    public int UnitCapasity;
    @Column
    public int UnitRef;
    @Column
    public BigDecimal UnitQty;
    @Column
    public BigDecimal TotalQty;
    @Column
    public String UnitName;
    @Column
    public Currency UnitPrice;
    @Column
    public String  PriceId;
    @Column
    public int CPriceRef;
    @Column
    public Currency Amount;
    @Column
    public Currency AmountNut;
    @Column
    public Currency Discount;
    @Column
    public int PrizeType;
    @Column
    public Currency SupAmount;
    @Column
    public Currency AddAmount;
    @Column
    public Currency CustPrice;
    @Column
    public Currency UserPrice;
    @Column
    public Currency Charge;
    @Column
    public Currency Tax;
    @Column
    public int RowOrder;
    @Column
    public int ProductCtgrId;
    @Column
    public int FreeReasonId;
    @Column
    public boolean IsDeleted;
}
