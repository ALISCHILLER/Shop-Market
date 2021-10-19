package com.varanegar.vaslibrary.model.productorderqtyhistoryview;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by A.Torabi on 9/13/2017.
 */
@Table
public class CustomerProductOrderQtyHistoryModel extends BaseModel {
    @Column
    public UUID ProductId;
    @Column
    public UUID CustomerId;
    @Column
    public BigDecimal DangerQty;
    @Column
    public BigDecimal WarningQty;
}
