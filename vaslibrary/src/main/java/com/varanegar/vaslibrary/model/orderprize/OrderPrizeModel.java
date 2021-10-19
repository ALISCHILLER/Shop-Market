package com.varanegar.vaslibrary.model.orderprize;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 12/23/2017.
 */
@Table
public class OrderPrizeModel extends BaseModel {
    @Column
    public UUID DiscountId;
    @Column
    public UUID ProductId;
    @Column
    public UUID CustomerId;
    @Column
    public BigDecimal TotalQty;
    @Column
    public int DisRef;
    @Column
    public UUID CallOrderId;
}
