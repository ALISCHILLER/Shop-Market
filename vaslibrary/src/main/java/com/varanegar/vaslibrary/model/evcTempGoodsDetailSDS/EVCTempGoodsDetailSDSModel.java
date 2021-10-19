package com.varanegar.vaslibrary.model.evcTempGoodsDetailSDS;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import com.varanegar.framework.validation.annotations.NotNull;

import java.math.BigDecimal;

/**
 * Created by s.foroughi on 09/01/2017.
 */

@Table
public class EVCTempGoodsDetailSDSModel extends BaseModel{

    @Column
    public int GoodsGroupRef;
    @Column
    public int ManufacturerRef;
    @Column
    public int BrandRef;
    @Column
    public int CartonType;
    @Column
    public BigDecimal Weight;
    @Column
    public BigDecimal CartonPrizeQty;
    @Column
    public int GoodsCtgrRef;


}
