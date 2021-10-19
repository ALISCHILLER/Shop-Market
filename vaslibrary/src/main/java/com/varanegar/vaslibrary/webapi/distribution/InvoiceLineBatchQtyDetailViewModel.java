package com.varanegar.vaslibrary.webapi.distribution;

import java.math.BigDecimal;
import java.util.UUID;

public class InvoiceLineBatchQtyDetailViewModel {
    public UUID UniqueId;
    public UUID OrderLineUniqueId;
    public String BatchNo;
    public BigDecimal Qty;
    public String ExpDate;
    public int BatchRef;
}
