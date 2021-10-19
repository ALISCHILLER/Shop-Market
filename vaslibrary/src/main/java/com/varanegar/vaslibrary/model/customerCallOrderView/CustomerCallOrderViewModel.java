package com.varanegar.vaslibrary.model.customerCallOrderView;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;


import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by s.foroughi on 21/02/2017.
 */
@Table
public class CustomerCallOrderViewModel extends BaseModel{

    @Column
    public String ProductName;
    @Column
    public String BackOfficeId;
    @Column
    public String ProductCode;
    @Column
    @NotNull
    public UUID UnitId;
    @Column
    public BigDecimal ConvertFactor;
    @Column
    public String UnitName;
    @Column
    public int Decimal;
}
