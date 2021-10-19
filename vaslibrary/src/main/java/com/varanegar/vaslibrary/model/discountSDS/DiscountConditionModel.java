package com.varanegar.vaslibrary.model.discountSDS;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 12/20/2017.
 */
@Table
public class DiscountConditionModel extends BaseModel {
    @Column
    public Integer DiscountRef;
    @Column
    public Integer DCRef;
    @Column
    public Integer CustCtgrRef;
    @Column
    public Integer CustActRef;
    @Column
    public Integer CustLevelRef;
    @Column
    public Integer PayType;
    @Column
    public Integer PaymentUsanceRef;
    @Column
    public Integer OrderType;
    @Column
    public Integer SaleOfficeRef;
    @Column
    public Integer CustGroupRef;
    @Column
    public Integer CustRef;
    @Column
    public String OrderNo;
    @Column
    public Integer StateRef;
    @Column
    public Integer AreaRef;
    @Column
    public Integer SaleZoneRef;
    @Column
    public Integer MainCustTypeRef;
    @Column
    public Integer SubCustTypeRef;

}
