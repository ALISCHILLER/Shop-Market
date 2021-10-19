package com.varanegar.vaslibrary.model.evcTempCustomerSDS;

import androidx.annotation.Nullable;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import com.varanegar.framework.validation.annotations.NotNull;

/**
 * Created by s.foroughi on 09/01/2017.
 */
@Table
public class EVCTempCustomerSDSModel extends BaseModel{

    @Column
    public int CustRef;
    @Column
    public int CustCtgrRef;
    @Column
    public int CustActRef;
    @Column
    public int CustLevelRef;
    @Column
    @Nullable
    public int AreaRef;
    @Column
    public int SalePathRef;
    @Column
    @Nullable
    public int ZoneId;
}
