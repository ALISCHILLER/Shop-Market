package com.varanegar.vaslibrary.model.evcTempReturnItemSummaryFinalSDS;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import com.varanegar.framework.validation.annotations.NotNull;

import java.math.BigDecimal;

/**
 * Created by s.foroughi on 09/01/2017.
 */
@Table
public class EVCTempReturnItemSummaryFinalSDSModel extends BaseModel{

    @Column
    public String EVCId;
    @Column
    public int RowOrder;
    @Column
    public int GoodsRef;
    @Column
    public BigDecimal UnitQty;
    @Column
    public int CPriceRef;
    @Column
    public int UnitRef;
    @Column
    public BigDecimal UnitCapasity;
    @Column
    public BigDecimal TotalQty;
    @Column
    public BigDecimal AmountNut;
    @Column
    public Currency Discount;
    @Column
    public Currency RemainDiscount;
    @Column
    public Currency Amount;
    @Column
    public BigDecimal PrizeType;
    @Column
    public Currency SupAmount;
    @Column
    public Currency AddAmount;
    @Column
    public Currency RemainAddAmount;
    @Column
    public Currency Add1;
    @Column
    public Currency Add2;
    @Column
    public Currency RemainAdd1;
    @Column
    public Currency RemainAdd2;
    @Column
    public Currency UserPrice;
    @Column
    public Currency CustPrice;
    @Column
    public String PriceId;
    @Column
    public Currency Charge;
    @Column
    public Currency Tax;
    @Column
    public Currency Dis1;
    @Column
    public Currency Dis2;
    @Column
    public Currency Dis3;
    @Column
    public Currency RemainDis1;
    @Column
    public Currency RemainDis2;
    @Column
    public Currency RemianDis3;

}
