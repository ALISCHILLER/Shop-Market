package com.varanegar.supervisor.status;

import com.varanegar.java.util.Currency;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class OrderSummaryResultViewModel {
    public UUID CustId;
    public UUID OrderTypeId;
    public UUID SaleOfficeId;
    public Date OrderDate;
    public UUID DealerId;
    public UUID BuyTypeId;
    public UUID PaymentUsanceId;
    public Currency Amount;
    public Currency AddAmount;
    public Currency DisAmount;
    public Currency AmountNut;
    List<OrderSummaryLineResultViewModel> SaleEvcDetails;
}
