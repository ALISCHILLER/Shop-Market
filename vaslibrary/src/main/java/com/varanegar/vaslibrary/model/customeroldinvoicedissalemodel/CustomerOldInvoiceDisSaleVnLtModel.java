package com.varanegar.vaslibrary.model.customeroldinvoicedissalemodel;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

/**
 * Created by A.Razavi on 13/12/2018.
 */
@Table
public class CustomerOldInvoiceDisSaleVnLtModel extends BaseModel {
    @Column
    public Currency DiscountAmount;
    @Column
    public Currency AddAmount;
    @Column
    public Currency PrizeAmount;
    @Column
    public Currency PrizeQty;

    @Column
    public int SellDetailPromotionDetailId;
    @Column
    public int SellDetailId;
    @Column
    public int SellId;
    @Column
    public int PromotionDetailId;
    @Column
    public int PromotionId;
}
