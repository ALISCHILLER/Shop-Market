package com.varanegar.vaslibrary.model.returnreportview;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Property;
import com.varanegar.processor.annotations.Table;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 8/17/2017.
 */
@Table
public class ReturnReportViewModel extends BaseModel {
    @Column
    public UUID ProductId;
    @Column
    public String ProductCode;
    @Column
    public String ProductName;
    @Column
    public String ReturnReasonId;
    @Column
    public String ReturnProductTypeId;
    @Column
    public String ConvertFactor;
    @Column
    public String ProductUnitId;
    @Column
    public String Qty;
    @Column
    public String UnitName;
    @Column
    public BigDecimal TotalReturnQty;
    @Column
    public Currency RequestUnitPrice;
    @Column
    public Currency TotalRequestAmount;
    @Column
    public float TotalWeight;
}
