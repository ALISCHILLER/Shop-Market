package com.varanegar.vaslibrary.webapi.returncontrol;

import java.math.BigDecimal;
import java.util.UUID;

public class ReturnControlDetailViewModel {
    public UUID ReturnId;
    public Integer DealerRef;
    public Integer CustRef;
    public Integer GoodsRef;
    public String SaleRef;
    public Integer HeathCode;
    public Integer RetCauseRef;
    public BigDecimal TotalQty;
    public BigDecimal UnitPrice;
    public UUID RetCauseId;
}
