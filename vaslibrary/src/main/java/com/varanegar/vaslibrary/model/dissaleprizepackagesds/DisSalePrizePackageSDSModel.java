package com.varanegar.vaslibrary.model.dissaleprizepackagesds;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

/**
 * Created by A.Jafarzadeh on 12/26/2017.
 */
@Table
public class DisSalePrizePackageSDSModel extends BaseModel {
    @Column
    public int BackOfficeId;
    @Column
    public int SaleRef;
    @Column
    public int DiscountRef;
    @Column
    public int MainGoodsPackageItemRef;
    @Column
    public int ReplaceGoodsPackageItemRef;
    @Column
    public int PrizeCount;
    @Column
    public int PrizeQty;
}
