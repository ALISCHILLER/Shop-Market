package com.varanegar.vaslibrary.model.evcTempDiscountSDS;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import com.varanegar.framework.validation.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by s.foroughi on 09/01/2017.
 */

@Table
public class EVCTempDiscountSDSModel extends BaseModel {

    @Column
    public int Id;
    @Column
    public int DisGroup;
    @Column
    public int DisType;
    @Column
    public int Priority;
    @Column
    public int ApplyInGroup;
    @Column
    public BigDecimal QtyUnit;
    @Column
    public BigDecimal MinQty;
    @Column
    public BigDecimal MaxQty;
    @Column
    public Currency MinAmount;
    @Column
    public Currency MaxAmount;
    @Column
    public BigDecimal MinWeight;
    @Column
    public BigDecimal MaxWeight;
    @Column
    public int MinRowsCount;
    @Column
    public int MaxRowsCount;
    @Column
    public int OrderType;
    @Column
    public int GoodsRef;
    @Column
    public int IsActive;
    @Column
    public int PayType;
    @Column
    public int AreaRef;
    @Column
    public int StateRef;
    @Column
    public int DcRef;
    @Column
    public int CustActRef;
    @Column
    public int CustCtgrRef;
    @Column
    public int CustLevelRef;
    @Column
    public int CustRef;
    @Column
    public int SaleOfficeRef;
    @Column
    public int SaleZoneRef;
    @Column
    public int GoodsGroupRef;
    @Column
    public int GoodsCtgrRef;
    @Column
    public int ManufacturerRef;
    @Column
    public int OrderNo;
    @Column
    public int MainTypeRef;
    @Column
    public int SubTypeRef;
    @Column
    public int BrandRef;
    @Column
    public int PrizeRef;
    @Column
    public int PrizeStep;
    @Column
    public int PrizeStepType;
    @Column
    public Date StartDate;
    @Column
    public Date EndDate;
    @Column
    public int CalcMethod;

    }
