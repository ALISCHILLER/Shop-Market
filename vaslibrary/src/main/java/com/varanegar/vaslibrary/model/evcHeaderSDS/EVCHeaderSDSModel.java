package com.varanegar.vaslibrary.model.evcHeaderSDS;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.Date;
import java.util.UUID;

/**
 * Created by s.foroughi on 11/01/2017.
 */
@Table
public class EVCHeaderSDSModel extends BaseModel {

    @Column
    public int RefId;
    @Column
    public UUID EVCId;

    @Column
    public int OrderType;
    @Column
    public int PayTypeRef;
    @Column
    public int OrderPayTypeRef;
    @Column
    public int StockDCRef;
    @Column
    public int DCRef;
    @Column
    public int DisType;
    @Column
    public int CustRef;
    @Column
    public int AccYear;
    @Column
    public int DCSaleOfficeRef;
    @Column
    public String CallId;
    @Column
    public Currency Tax;
    @Column
    public Currency Charge;
    @Column
    public Currency Amount;
    @Column
    public Currency NetAmount;
    @Column
    public int EVCType;
    @Column
    public int SaleOfficeRef;
    @Column
    public Date EvcDate;
    @Column
    public int DealerRef;
    @Column
    public int SupervisorRef;

    //TODO Asal remove
    @Column
    public Currency Dis1;
    @Column
    public Currency Dis2;
    @Column
    public Currency Dis3;
    @Column
    public Currency Add1;
    @Column
    public Currency Add2;

    @Column
    public String StockDCCode;
    @Column
    public String DealerCode;
    @Column
    public String SupervisorCode;
    @Column
    public String DCCode;

    @Column
    @NotNull
    public UUID CustomerUniqueId;


    public EVCHeaderSDSModel(){}


}
