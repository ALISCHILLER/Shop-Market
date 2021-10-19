package com.varanegar.supervisor.webapi;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 7/10/2018.
 */
@Table
public class CustomerCallReturnLineViewModel extends BaseModel {
    @Column
    public String ProductCode;
    @Column
    public String ProductName;
    @Column
    public Currency UnitPrice;
    @Column
    public Currency TotalReturnAmount;
    @Column
    public Currency TotalReturnNetAmount;
    @Column
    public String StockName;
    @Column
    public String ReturnReasonName;
    @Column
    public String ReturnProductTypeName;
    @Column
    public String ReturnQty;
    @Column
    public String ReturnUnit;
}
