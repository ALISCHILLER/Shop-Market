package com.varanegar.vaslibrary.ui.report.review;

import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.ui.report.review.adapter.CustomerCallOrderLineViewModel;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 7/10/2018.
 */

public class CustomerCallOrderViewModel {
    public UUID CustomerUniqueId;
    public String CustomerCode;
    public String CustomerName;
    public String Address;
    public String StoreName;
    public UUID SubSystemTypeUniqueId;
    public String BackOfficeSaleVoucherNo;
    public String BackOfficeOrderNoCollection;
    public UUID DistributionDeliveryStatusUniqueId;
    public String DistributionDeliveryStatusName;
    public UUID ReturnReasonUniqueId;
    public String ReturnReasonName;
    public Date DeliveryDate;
    public String DeliveryPDate;
    public UUID OrderTypeUniqueId;
    public String OrderTypeName;
    public String BackOfficeOrderNo;
    public UUID OrderPaymentTypeUniqueId;
    public String OrderPaymentTypeName;
    public String Comment;
    public String LocalPaperNo;
    public Currency RoundOrderAmount;
    public String CallDate;
    public String SalePDate;
    public String StockName;
    public UUID InvoicePaymentTypeUniqueId;
    public String InvoiceStartPTime;
    public String InvoiceEndPTime;
    public String InvoicePaymentTypeName;
    public String BackOfficeInvoiceNo;
    public int PrintCount;
    public Currency InvoiceRoundAmount;
    public Currency InvoiceOtherRoundDiscount;
    public Currency InvoiceRoundTax;
    public Currency InvoiceRoundCharge;
    public Currency InvoiceRoundDis1;
    public Currency InvoiceRoundDis2;
    public Currency InvoiceRoundDis3;
    public Currency InvoiceRoundDiscount;
    public Currency InvoiceRoundAddAmount;
    public Currency InvoiceRoundAdd1;
    public Currency InvoiceRoundAdd2;
    public Currency InvoiceRoundNetAmount;
    public boolean IsCanceled;
    public List<CustomerCallOrderLineViewModel> OrderLines;
}
