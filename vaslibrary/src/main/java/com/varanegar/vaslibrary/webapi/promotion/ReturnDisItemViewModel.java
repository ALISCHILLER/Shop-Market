package com.varanegar.vaslibrary.webapi.promotion;

import com.google.gson.annotations.SerializedName;
import com.varanegar.java.util.Currency;

import java.math.BigDecimal;

public class ReturnDisItemViewModel {
    @SerializedName("refID")
    public String RefId;
    public int EvcRef;
    public int PrizeType;
    public int GoodsRef;
    public BigDecimal TotalQty;
    public int CPriceRef;
    public int IsPromoLine;
    public Currency UnitPrice;
    public Currency TotalRequestNetAmount;
    public Currency TotalRequestTax;
    public Currency TotalRequestCharge;
    public Currency TotalRequestDis1Amount;
    public Currency TotalRequestDis2Amount;
    public Currency TotalRequestDis3Amount;
    public Currency TotalRequestAdd1Amount;
    public Currency TotalRequestAdd2Amount;
    public Currency TotalRequestDisOtherAmount;
    public Currency TotalRequestAddOtherAmount;
}
