package com.varanegar.vaslibrary.model.evcTempSummaryVnLite;

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
public class EVCTempSummaryVnLiteModel extends BaseModel {

    @Column
    public String DisId;
    @Column
    public int DisGroup;
    @Column
    public int Priority;
    @Column
    public BigDecimal ReqQty;
    @Column
    public Currency ReqAmount;
    @Column
    public BigDecimal ReqWeight;
    @Column
    public UUID MainProductId;
    @Column
    public String EVCId;


}
