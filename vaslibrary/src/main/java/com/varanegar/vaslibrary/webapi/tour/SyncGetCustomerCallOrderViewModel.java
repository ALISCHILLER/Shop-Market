package com.varanegar.vaslibrary.webapi.tour;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 8/1/2017.
 */

public class SyncGetCustomerCallOrderViewModel {
    public UUID DistributionDeliveryStatusUniqueId;
    public UUID UndeliveredReasonUniqueId;
    public UUID ReturnReasonUniqueId;
    public UUID UniqueId;
    public UUID SubSystemTypeUniqueId;
    public UUID OrderTypeUniqueId;
    public UUID OrderPaymentTypeUniqueId;
    public String Comment;
    public String LocalPaperNo;
    //    public double RoundOrderAmount;
    public Date CallDate;
    public Date DeliveryDate;
    public UUID InvoicePaymentTypeUniqueId;
    public String BackOfficeOrderNo;
    public int BackOfficeInvoiceNo;
    public UUID ShipToPartyUniqueId;
    public String ShipToPartyCode;
    //    public double RoundInvoiceAmount;
//    public double RoundInvoiceOtherDiscount;
//    public double RoundInvoiceTax;
//    public double RoundInvoiceCharge;
//    public double RoundInvoiceDis1;
//    public double RoundInvoiceDis2;
//    public double RoundInvoiceDis3;
//    public double RoundInvoiceAdd1;
//    public double RoundInvoiceAdd2;
    public Date InvoiceStartTime;
    public Date InvoiceEndTime;
    public int PrintCount;
    public String DistBackOfficeId;
    public int DisType;
    public Date SaleDate;
    public int BackOfficeOrderId;
    public UUID BackOfficeOrderTypeId;
    public double RoundOrderOtherDiscount;
    public double RoundOrderDis1;
    public double RoundOrderDis2;
    public double RoundOrderDis3;
    public double RoundOrderTax;
    public double RoundOrderCharge;
    public double RoundOrderAdd1;
    public double RoundOrderAdd2;
    public UUID PriceClassUniqueId;
    public UUID SellTypeStatusTypeUniqueId;
    public double OrderOtherRoundDiscount;
    public String InvoiceStartPTime;
    public List<SyncGetCustomerCallOrderLineViewModel> OrderLines = new ArrayList<>();
    public List<SyncGetCustomerCallOrderPrizeViewModel> OrderPrizes = new ArrayList<>();
    public List<SyncGetCustomerCallOrderLineViewModel> PromotionsPreview = new ArrayList<>();

}
