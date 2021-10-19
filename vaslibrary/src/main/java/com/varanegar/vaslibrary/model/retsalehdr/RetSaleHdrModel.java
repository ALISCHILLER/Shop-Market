package com.varanegar.vaslibrary.model.retsalehdr;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

/**
 * Created by A.Jafarzadeh on 3/3/2019.
 */
@Table
public class RetSaleHdrModel extends BaseModel {
    @Column
    public int BackOfficeId;
    @Column
    public int RetSaleNo;
    @Column
    public String RetSaleDate;
    @Column
    public String SaleDate;
    @Column
    public int SaleRef;
    @Column
    public int VocherFlag;
    @Column
    public int HealthCode;
    @Column
    public int RetTypeCode;
    @Column
    public int RetCauseRef;
    @Column
    public Double Dis1;
    @Column
    public Double Dis2;
    @Column
    public Double Dis3;
    @Column
    public Double Add1;
    @Column
    public Double Add2;
    @Column
    public int CancelFlag;
    @Column
    public Double TotalAmount;
    @Column
    public int UserRef;
    @Column
    public String ChangeDate;
    @Column
    public int BuyType;
    @Column
    public int AccYear;
    @Column
    public int DCRef;
    @Column
    public int StockDCRef;
    @Column
    public int CustRef;
    @Column
    public int DealerRef;
    @Column
    public int DCSaleOfficeRef;
    @Column
    public int RetOrderRef;
    @Column
    public int DistRef;
    @Column
    public int TSaleRef;
    @Column
    public String Comment;
    @Column
    public Double Tax;
    @Column
    public Double Charge;
    @Column
    public int SupervisorRef;
    @Column
    public Double OtherDiscount;
    @Column
    public Double OtherAddition;
}
