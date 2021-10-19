package com.varanegar.supervisor.status;

import com.varanegar.java.util.Currency;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderSummaryLineResultViewModel {
    public int Id;
    public int EvcRef;
    public int RowOrder;
    public int PrizeType;
    public int GoodsRef;
    public BigDecimal UnitQty;
    public BigDecimal TotalQty;
    public int UnitRef;
    public BigDecimal UnitCapasity;
    public Currency CustPrice;
    public Currency Amount;
    public Currency Discount;
    public Currency Tax;
    public Currency Charge;
    public Currency AmountNut;
    public Currency SupAmount;
    public Currency AddAmount;
    public Currency UserPrice;
    public int DisRef;
    public BigDecimal TaxPercent;
    public BigDecimal ChargePercent;
    public int PeriodicDiscountRef;
    public UUID FreeReasonId;
    public Currency EvcItemDis1;
    public Currency EvcItemDis2;
    public Currency EvcItemDis3;
    public Currency EvcItemDisOther;
    public Currency EvcItemAdd1;
    public Currency EvcItemAdd2;
    public Currency EvcItemAddOther;
    public String GoodName;
    public UUID GoodId;
}
