package com.varanegar.vaslibrary.model.totalproductsaleview;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by A.Torabi on 9/12/2017.
 */
@Table
public class CustomerTotalProductSaleModel extends BaseModel {
    @Column
    public int TotalQty;
    @Column
    public UUID ProductId;
    @Column
    public UUID CustomerId;
    @Column
    public int InvoiceCount;
}
