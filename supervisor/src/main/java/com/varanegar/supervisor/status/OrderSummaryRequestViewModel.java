package com.varanegar.supervisor.status;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class OrderSummaryRequestViewModel {
    public UUID CustId;
    public UUID OrderTypeId;
    public UUID BuyTypeId;
    public UUID SaleOfficeId;
    public int DisType;
    public Date OrderDate;
    public UUID DealerId;
    public int PaymentUsanceId;
    public List<OrderSummaryRequestDetailViewModel> SupSaleEvcDetails;
    public String SalePDate;
    public int SaleOfficeRefSDS;
}
