package com.varanegar.vaslibrary.webapi.tour;

import com.varanegar.java.util.Currency;

import java.util.UUID;

/**
 * Created by A.Torabi on 7/29/2018.
 */

public class SyncGetCustomerCallPaymentDetailViewModel {
    public UUID UniqueId;
    public UUID InvoiceId;
    public UUID CallOrderId;
    public String SaleNo;
    public String SaleRef;
    public boolean IsOldInvoice;
    public UUID CustomerId;
    public double PaidAmount;
    public UUID PaymentId;
    public String SaleDate;
}
