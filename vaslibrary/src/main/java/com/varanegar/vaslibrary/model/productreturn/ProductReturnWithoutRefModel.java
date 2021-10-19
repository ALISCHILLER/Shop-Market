package com.varanegar.vaslibrary.model.productreturn;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by atp on 4/16/2017.
 */
@Table
public class ProductReturnWithoutRefModel extends BaseModel {
    @Column
    public String ProductName;
    @Column
    public String ProductCode;
    @Column
    public UUID ProductGroupId;
    @Column
    public UUID CustomerUniqueId;
    @Column
    public String CustomerName;
    @Column
    public BigDecimal TotalQty;
    @Column
    public Currency TotalRequestAmount;
    @Column
    public String UnitName;
    @Column
    public String Qty;
}
