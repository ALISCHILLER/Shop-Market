package com.varanegar.vaslibrary.model.RequestItemLines;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by A.Torabi on 3/25/2018.
 */
@Table
public class RequestLineViewModel extends BaseModel {
    @Column
    public String ProductCode;
    @Column
    public String ProductName;
    @Column
    public int RowIndex;
    @Column
    public BigDecimal BulkQty;
    @Column
    public UUID BulkQtyUnitUniqueId;
    @Column
    public UUID ProductGroupId;
    @Column
    public UUID RequestLineUniqueId;
    @Column
    public String Qty;
    @Column
    public String ProductUnitId;
    @Column
    public String UnitName;
    @Column
    public String ConvertFactor;
    @Column
    public BigDecimal TotalQty;
    @Column
    public Currency UnitPrice;
    @Column
    public Currency TotalPrice;

    @Override
    public String toString() {
        return ProductName + " (" + ProductCode + ") ";
    }
}
