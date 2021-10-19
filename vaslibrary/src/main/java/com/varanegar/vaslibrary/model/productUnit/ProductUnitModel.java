package com.varanegar.vaslibrary.model.productUnit;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import com.varanegar.framework.validation.annotations.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by s.foroughi on 09/01/2017.
 */
@Table
public class ProductUnitModel extends BaseModel {

    @Column
    @NotNull
    public UUID ProductId;
    @Column
    @NotNull
    public UUID UnitId;
    @Column
    public BigDecimal ConvertFactor;
    @Column
    public boolean IsForSale;
    @Column
    public boolean IsReturnDefault;
    @Column
    public boolean IsForReturn;
    @Column
    public boolean IsDefault;
    @Column
    @NotNull
    public UUID UnitStatusId;
}
