package com.varanegar.vaslibrary.model.productreportview;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.math.BigDecimal;

/**
 * Created by A.Jafarzadeh on 12/8/2018.
 */
@Table
public class ProductReportViewModel extends BaseModel {
    @Column
    public String ProductCode;
    @Column
    public String ProductName;
    @Column
    public String Qty;
    @Column
    public BigDecimal TotalQty;
    @Column
    public String ConvertFactor;
    @Column
    public String UnitId;
    @Column
    public String UnitName;
    @Column
    public double TotalWeight;
}
