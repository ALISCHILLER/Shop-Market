package com.varanegar.vaslibrary.webapi.returncontrol;

import java.math.BigDecimal;
import java.util.UUID;

public class ReturnControlOrderDetailViewModel {
    public UUID OrderId;
    public Integer PaymentUsanceRef;
    public Integer DealerRef;
    public Integer CustRef;
    public Integer DisType;
    public Integer OrderTypeRef;
    public Integer SaleOfficeRef;
    public String OrderDate;
    public Integer GoodsRef;
    public Integer FreeReasonRef;
    public BigDecimal TotalQty;
    public BigDecimal UnitPrice;
}
