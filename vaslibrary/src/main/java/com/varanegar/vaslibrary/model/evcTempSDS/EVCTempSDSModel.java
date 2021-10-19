package com.varanegar.vaslibrary.model.evcTempSDS;

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
public class EVCTempSDSModel extends BaseModel {

    @Column
    public String DiscountId;
    @Column
    public int DisGroup;
    @Column
    public int DisType;
    @Column
    public int  EVCItemRefId;
    @Column
    public int Priority;
    @Column
    public int RowOrder;
    @Column
    public BigDecimal ReqQty;
    @Column
    public Currency ReqAmount;
    @Column
    public int ReqRowCount;
    @Column
    public BigDecimal MinQty;
    @Column
    public BigDecimal MaxQty;
    @Column
    public int UnitCapasity;
    @Column
    public Currency MinAmount;
    @Column
    public Currency MaxAmount;
    @Column
    public String EVCId;
    @Column
    public int ApplyInGroup;
    @Column
    public BigDecimal ReqWeight;
    @Column
    public BigDecimal MinWeight;
    @Column
    public BigDecimal MaxWeight;
    @Column
    public int MinRowsCount;
    @Column
    public int MaxRowsCount;
    @Column
    public int DistItemRef;
    @Column
    public int PrizeStep;
    @Column
    public int PrizeStepType;



}
