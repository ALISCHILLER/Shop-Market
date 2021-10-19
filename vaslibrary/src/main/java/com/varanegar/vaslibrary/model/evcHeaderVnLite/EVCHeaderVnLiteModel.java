package com.varanegar.vaslibrary.model.evcHeaderVnLite;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import com.varanegar.framework.validation.annotations.NotNull;

import java.util.UUID;

/**
 * Created by s.foroughi on 11/01/2017.
 */
@Table
public class EVCHeaderVnLiteModel extends BaseModel {

    @Column
    public int RefId;
    @Column
    public Currency DiscountAmount;
    @Column
    public Currency AddAmount;
    @Column
    public int OrderType;
    @Column
    public int PayType;
    @Column
    public int StockDCRef;
    @Column
    public int DCRef;
    @Column
    public int DisType;
    @Column
    public int AccYear;
    @Column
    public int DCSaleOfficeRef;
    @Column
    public String CallId;
    @Column
    public String EVCId;
    @Column
    public Currency Tax;
    @Column
    public Currency Charge;
    @Column
    public UUID CustomerUniqueId;
    @Column
    public Currency Amount;
    @Column
    public Currency NetAmount;
    @Column
    public int EVCType;



}
