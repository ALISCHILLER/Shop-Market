package com.varanegar.vaslibrary.model.retsaleitem;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

/**
 * Created by A.Jafarzadeh on 3/3/2019.
 */
@Table
public class RetSaleItemModel extends BaseModel {
    @Column
    public int BackOfficeId;
    @Column
    public int HdrRef;
    @Column
    public Double RowOrder;
    @Column
    public int RetCauseRef;
    @Column
    public int GoodsRef;
    @Column
    public int UnitRef;
    @Column
    public int UnitCapasity;
    @Column
    public Double UnitQty;
    @Column
    public Double TotalQty;
    @Column
    public Double UnitPrice;
    @Column
    public Double AmountNut;
    @Column
    public Double Discount;
    @Column
    public Double Amount;
    @Column
    public int AccYear;
    @Column
    public int PrizeType;
    @Column
    public int SaleRef;
    @Column
    public Double SupAmount;
    @Column
    public Double AddAmount;
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
    public Double Tax;
    @Column
    public Double Charge;
    @Column
    public int FreeReasonId;
    @Column
    public Double OtherDiscount;
    @Column
    public Double OtherAddition;
}
