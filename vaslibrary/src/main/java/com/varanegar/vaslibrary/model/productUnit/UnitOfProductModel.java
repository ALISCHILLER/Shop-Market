package com.varanegar.vaslibrary.model.productUnit;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Table
public class UnitOfProductModel extends BaseModel {

    @Column
    public String UnitName;
    @Column
    public int BackOfficeId;
    @Column
    public UUID productUnitId;
    @Column
    public BigDecimal ConvertFactor;
    @Column
    public boolean IsDefault;
    @Column
    public boolean IsForSale;
    @Column
    public boolean IsReturnDefault;
    @Column
    public boolean IsForReturn;
    public int Count;
}
