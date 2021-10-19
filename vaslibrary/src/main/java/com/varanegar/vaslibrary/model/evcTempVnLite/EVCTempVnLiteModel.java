package com.varanegar.vaslibrary.model.evcTempVnLite;

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
public class EVCTempVnLiteModel extends BaseModel {

    @Column
    public String DiscountId;
    @Column
    public int DiscountGroup;
    @Column
    public int Priority;
    @Column
    public BigDecimal ReqQty;
    @Column
    public Currency ReqAmount;
    @Column
    public BigDecimal MinQty;
    @Column
    public BigDecimal MaxQty;
    @Column
    public Currency MinAmount;
    @Column
    public Currency MaxAmount;
    @Column
    public BigDecimal ReqWeight;
    @Column
    public BigDecimal MinWeight;
    @Column
    public BigDecimal MaxWeight;
    @Column
    public UUID ProducUniqueId;
    @Column
    public UUID MainProductId;
    @Column
    public int ReqRowCount;
    @Column
    public int MinRowCount;
    @Column
    public int MaxRowCount;
    @Column
    public int PrizeRefId;
    @Column
    public String EVCId;

}

