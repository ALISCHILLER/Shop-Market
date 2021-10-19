package com.varanegar.vaslibrary.model.evcItemStatutesSDS;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import com.varanegar.framework.validation.annotations.NotNull;

/**
 * Created by s.foroughi on 09/01/2017.
 */
@Table
public class EVCItemStatutesSDSModel extends BaseModel{
    @Column
    public String EVCItemRefId;
    @Column
    public int RowOrder;
    @Column
    public int DisRef;
    @Column
    public int DisGroup;
    @Column
    public Currency AddAmount;
    @Column
    public Currency SupAmount;
    @Column
    public Currency Discount;
    @Column
    public String EvcId;

}
