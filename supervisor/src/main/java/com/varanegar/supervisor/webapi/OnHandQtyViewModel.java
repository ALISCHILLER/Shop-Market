package com.varanegar.supervisor.webapi;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Property;
import com.varanegar.processor.annotations.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Table
public class OnHandQtyViewModel extends BaseModel {
    @Column
    public UUID ProductId;
    @Column
    public String ProductCode;
    @Column
    public String ProductName;
    @Column
    public String StockName;
    @Column
    public BigDecimal OnHandQty;
    @Column
    public BigDecimal ReservedQty;
    @Column
    public boolean IsBatch;
    @Column
    public UUID StockId;
    @Column
    public boolean HasAllocation;
    @Column
    public int StockDcRef;
    @Property
    public BigDecimal OnHandQtyAfterReserve;

    @Override
    public void setProperties() {
        super.setProperties();
        if (OnHandQty == null)
            OnHandQty = BigDecimal.ZERO;
        if (ReservedQty == null)
            ReservedQty = BigDecimal.ZERO;
        OnHandQtyAfterReserve = OnHandQty.subtract(ReservedQty);
    }
}
