package com.varanegar.vaslibrary.model.productorderqtyhistoryview;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * Created by A.Torabi on 9/13/2017.
 */
@Table
public class ProductOrderQtyHistoryViewModel extends BaseModel{
    @Column
    public UUID CustomerId;
    @Column
    public UUID ProductId;
    @Column
    public Date SaleDate;
    @Column
    public BigDecimal TotalQty;
}
