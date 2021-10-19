package com.varanegar.vaslibrary.model.goodsnosale;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

/**
 * Created by A.Jafarzadeh on 3/4/2019.
 */
@Table
public class GoodsNosaleModel extends BaseModel {
    @Column
    public int BackOfficeId;
    @Column
    public int GoodsRef;
    @Column
    public int Status;
    @Column
    public String StartDate;
    @Column
    public String EndDate;
    @Column
    public int CustRef;
    @Column
    public int DCRef;
    @Column
    public int CustActRef;
    @Column
    public int CustCtgrRef;
    @Column
    public int CustLevelRef;
    @Column
    public int StateRef;
    @Column
    public int AreaRef;
    @Column
    public int CountyRef;
}
