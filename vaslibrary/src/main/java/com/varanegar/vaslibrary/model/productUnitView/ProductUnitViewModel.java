package com.varanegar.vaslibrary.model.productUnitView;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import com.varanegar.framework.validation.annotations.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by s.foroughi on 01/03/2017.
 */
@Table
public class ProductUnitViewModel  extends BaseModel {
    @Column
    @NotNull
    public UUID ProductId;
    @Column
    public int BackOfficeId;
    @Column
    public String ProductName;
    @Column
    public String ProductCode;
    @Column
    public UUID UnitId;
    @Column
    public int Decimal;
    @Column
    public String UnitName;
    @Column
    public double ConvertFactor;
    @Column
    public boolean IsForSale;
    @Column
    public boolean IsForReturn;
    @Column
    public boolean IsDefault;
    @Column
    public boolean IsReturnDefault;
    @Column
    public int UnitRef;
}
