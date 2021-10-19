package com.varanegar.vaslibrary.model.tempreportpreview;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;
import com.varanegar.vaslibrary.webapi.BaseApi;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 7/17/2017.
 */
@Table
public class TempReportPreViewModel extends BaseModel
{
    @Column
    public UUID OrderLineUniqueId;
    @Column
    public int ProductCode;
    @Column
    public int ProductName;
    @Column
    public UUID ProductId;
    @Column
    public Currency UnitPrice;
    @Column
    public Currency TotalPrice;
    @Column
    public Currency Discont;
    @Column
    public Currency Tax;
    @Column
    public BigDecimal Qty;
    @Column
    public String ConvertFactor;
    @Column
    public String ProductUnitId;
    @Column
    public String UnitName;
    @Column
    public BigDecimal TotalQty;
}
