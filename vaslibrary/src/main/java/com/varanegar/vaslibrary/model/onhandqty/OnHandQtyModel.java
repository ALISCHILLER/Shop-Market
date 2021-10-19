package com.varanegar.vaslibrary.model.onhandqty;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 7/10/2017.
 */
@Table
public class OnHandQtyModel extends BaseModel
{
    @Column
    public UUID ProductId;
    @Column
    public BigDecimal OnHandQty;
    @Column
    public BigDecimal RenewQty;
    @Column
    public UUID StockId;
    @Column
    public boolean IsBatch;
    @Column
    public boolean HasAllocation;
    @Column
    public int StockDcRef;
    @Column
    public BigDecimal ReservedQty;
}
