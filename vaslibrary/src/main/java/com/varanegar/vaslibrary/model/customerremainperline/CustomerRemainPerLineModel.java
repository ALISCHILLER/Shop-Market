package com.varanegar.vaslibrary.model.customerremainperline;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 8/20/2018.
 */
@Table
public class CustomerRemainPerLineModel extends BaseModel {
    @Column
    public UUID CustomerId;
    @Column
    public int CustRef;
    @Column
    public int CountRetChq;
    @Column
    public Currency AmountRetChq;
    @Column
    public int CountOpenFact;
    @Column
    public Currency AmountOpenFact;
    @Column
    public Currency CustRemAmount;
    @Column
    public int CountChq;
    @Column
    public Currency AmountChq;
}
