package com.varanegar.vaslibrary.model.contractprice;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.Date;

/**
 * Created by A.Torabi on 12/5/2017.
 * This model is created for Rastak
 */
@Table
public class ContractPriceVnLiteModel extends BaseModel {
    @Column
    public int BackOfficeId;
    @Column
    public int ProductRef;
    @Column
    public int CustomerRef;
    @Column
    public int CustomerGroupRef;
    @Column
    public String StartDate;
    @Column
    public String EndDate;
    @Column
    public double SellPrice;
    @Column
    public double UserPrice;
    @Column
    public String Comment;
    @Column
    public String ModifiedDate;
    @Column
    public int AppUserRef;
    @Column
    public int BatchRef;
    @Column
    public int PriceClassRef;
    @Column
    public int CustomerSubGroup1Ref;
    @Column
    public int CustomerSubGroup2Ref;
    @Column
    public int CenterRef;
    @Column
    public int TargetCenterRef;
    @Column
    public int BackOfficeNumberId;
}
