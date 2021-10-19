package com.varanegar.vaslibrary.model.evcTempSummaryFinalVnLite;

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
public class EVCTempSummaryFinalVnLiteModel extends BaseModel{

    @Column
    public UUID ProductId;
    @Column
    public String DisId;
    @Column
    public String EVCId;
    @Column
    public BigDecimal Qty;
    @Column
    public String DistId;
    @Column
    public Currency UnitPrice;
    @Column
    public String PriceId;
    @Column
    public BigDecimal ReduceOfQty;

}
