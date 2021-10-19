package com.varanegar.vaslibrary.model.orderprizeview;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;
import com.varanegar.vaslibrary.model.orderprize.OrderPrizeModel;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 12/24/2017.
 */
@Table
public class OrderPrizeViewModel extends BaseModel {
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
    public String ProductName;
    @Column
    public String ProductCode;
    @Column
    public int GoodsRef;
    @Column
    public UUID CallOrderId;

    public OrderPrizeModel getOrderPrize() {
        OrderPrizeModel orderPrizeModel = new OrderPrizeModel();
        orderPrizeModel.UniqueId = UniqueId;
        orderPrizeModel.CustomerId = CustomerId;
        orderPrizeModel.DiscountId = DiscountId;
        orderPrizeModel.TotalQty = TotalQty;
        orderPrizeModel.DisRef = DisRef;
        orderPrizeModel.ProductId = ProductId;
        orderPrizeModel.CallOrderId = CallOrderId;
        return orderPrizeModel;
    }
}
