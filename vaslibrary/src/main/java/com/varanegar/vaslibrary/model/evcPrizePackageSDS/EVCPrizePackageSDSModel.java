package com.varanegar.vaslibrary.model.evcPrizePackageSDS;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import com.varanegar.framework.validation.annotations.NotNull;

import java.math.BigDecimal;

/**
 * Created by s.foroughi on 09/01/2017.
 */

@Table
public class EVCPrizePackageSDSModel extends BaseModel {

    @Column
    public int EvcRef;
    @Column
    public int DiscountRef;
    @Column
    public int MainGoodsPackageItemRef;
    @Column
    public int ReplaceGoodsPackageItemRef;
    @Column
    public int PrizeCount;
    @Column
    public BigDecimal PrizeQty;
}
