package com.varanegar.vaslibrary.model.evcSkipDiscount;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import com.varanegar.framework.validation.annotations.NotNull;

/**
 * Created by s.foroughi on 09/01/2017.
 */
@Table
public class EVCSkipDiscountModel extends BaseModel {

    @Column
    public int EvcRef;
    @Column
    public int SaleRef;
    @Column
    public int DisRef;

}
