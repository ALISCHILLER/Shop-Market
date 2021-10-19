package com.varanegar.vaslibrary.model.evcTempGoodsPackageItemSDS;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import com.varanegar.framework.validation.annotations.NotNull;

import java.math.BigDecimal;

/**
 * Created by s.foroughi on 09/01/2017.
 */
@Table
public class EVCTempGoodsPackageItemSDSModel extends BaseModel {

    @Column
    public String DiscountId;
    @Column
    public int GoodsPackageRef;
    @Column
    public int BaseGoodsRef;
    @Column
    public String GoodsRef;
    @Column
    public BigDecimal UnitQty;
    @Column
    public int UnitRef;
    @Column
    public BigDecimal TotalQty;
    @Column
    public int ReplaceGoodsPackageItemRef;
    @Column
    public int PrizeCount;


}
