package com.varanegar.vaslibrary.ui.fragment.order;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 11/19/2018.
 */
@Table
public class GoodsCustQuotaSummaryModel extends BaseModel {
    @Column
    public int GoodsCustID;
    @Column
    public String StartDate;
    @Column
    public String EndDate;
    @Column
    public String ItemRef;
    @Column
    public int RowOrder;
    @Column
    public int RuleNo;
    @Column
    public int ApplyInGroup;
    @Column
    public int GoodsRef;
    @Column
    public int GoodsCtgrRef;
    @Column
    public int MainTypeRef;
    @Column
    public int SubTypeRef;
    @Column
    public int CustRef;
    @Column
    public int CustCtgrRef;
    @Column
    public int CustActRef;
    @Column
    public int StateRef;
    @Column
    public int CountyRef;
    @Column
    public int AreaRef;
    @Column
    public int SaleOfficeRef;
    @Column
    public String UnitUniqueId;
    @Column
    public int GoodsGroupRef;
    @Column
    public int ManufacturerRef;
    @Column
    public int CustLevelRef;
    @Column
    public int ReqQty;
    @Column
    public int MINQty;
    @Column
    public int MAXQty;
    @Column
    public String OrderDate;
    @Column
    public String ProductCode;
    @Column
    public String ProductName;
    @Column
    public String ProductUniqueId;
}
