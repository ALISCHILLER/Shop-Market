package com.varanegar.vaslibrary.model.goodsPackageItem;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.math.BigDecimal;

/**
 * Created by s.foroughi on 09/01/2017.
 */
@Table
public class GoodsPackageItemModel extends BaseModel {

    @Column
    public int BackOfficeId;
    @Column
    public int GoodsPackageRef;
    @Column
    public int GoodsRef;
    @Column
    public BigDecimal UnitQty;
    @Column
    public int UnitRef;
    @Column
    public BigDecimal TotalQty;
    @Column
    public Integer ReplaceGoodsRef;
    @Column
    public BigDecimal PrizePriority;
    @Column
    public int DiscountRef;

}
