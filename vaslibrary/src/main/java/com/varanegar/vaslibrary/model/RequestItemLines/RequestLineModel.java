package com.varanegar.vaslibrary.model.RequestItemLines;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import com.varanegar.framework.validation.annotations.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by s.foroughi on 11/01/2017.
 */
@Table
public class RequestLineModel extends BaseModel{
    @NotNull
    @Column
    public UUID ProductId;
    @Column
    public Currency UnitPrice;
    @Column
    public int RowIndex;
    @Column
    public BigDecimal BulkQty;
    @Column
    public UUID BulkQtyUnitUniqueId;

}
