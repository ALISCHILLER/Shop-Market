package com.varanegar.vaslibrary.model.product;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by atp on 8/10/2016.
 */
@Table
public class ProductTaxInfoModel extends BaseModel {

    @Column
    public UUID ProductId;
    @Column
    public int ProductRef;
    @Column
    public BigDecimal TaxRate;
    @Column
    public BigDecimal ChargeRate;
}
