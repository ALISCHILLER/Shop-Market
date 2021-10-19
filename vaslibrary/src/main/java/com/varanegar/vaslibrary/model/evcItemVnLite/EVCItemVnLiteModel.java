package com.varanegar.vaslibrary.model.evcItemVnLite;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import com.varanegar.framework.validation.annotations.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by s.foroughi on 09/01/2017.
 */

@Table
public class EVCItemVnLiteModel extends BaseModel {
    @Column
    public int RowOrder;
    @Column
    public UUID ProducUniqueId;
    @Column
    public BigDecimal UnitQty;
    @Column
    public BigDecimal UnitCapasity;
    @Column
    public BigDecimal TotalQty;
    @Column
    public Currency AmountNut;
    @Column
    public Currency Discount;
    @Column
    public Currency Amount;
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
    public BigDecimal ChargePercent;
    @Column
    public BigDecimal TaxPercent;
    @Column
    public String EVCRef;
    @Column
    public String CallId;
    @Column
    public BigDecimal TotalWeight;
    @Column
    public int UnitRef;
    @Column
    public int DisRef;
    @Column
    public Currency Tax;
    @Column
    public Currency Charge;
    @Column
    public String PriceId;
    @Column
    public BigDecimal PackQty;
    @Column
    public int BatchId;
    @Column
    public BigDecimal ReduceOfQty;
}
