package com.varanegar.vaslibrary.model.evcTempSummarySDS;

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
public class EVCTempSummarySDSModel extends BaseModel {

    @Column
    public String DisId;
    @Column
    public int DisGroup;
    @Column
    public int DisType;
    @Column
    public int EVCItemRef;
    @Column
    public int RowOrder;
    @Column
    public int Priority;
    @Column
    public BigDecimal ReqQty;
    @Column
    public BigDecimal ReqRowCount;
    @Column
    public Currency ReqAmount;
    @Column
    public BigDecimal ReqWeight;
    @Column
    public String EVCId;


}
