package com.varanegar.vaslibrary.model.invoiceinfo;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by A.Torabi on 7/24/2018.
 */
@Table
public class InvoicePaymentInfoViewModel extends BaseModel {
    @Column
    public UUID InvoiceId;
    @Column
    public String InvoiceNo;
    @Column
    public boolean IsOldInvoice;
    @Column
    public Currency Amount;
    @Column
    public UUID CustomerId;
    @Column
    public String InvoiceDate;
    @Column
    public Currency RemAmount;
    @Column
    public Currency TotalRemAmount;
    @Column
    public Currency PaidAmount;
    @Column
    public Currency TotalPaidAmount;
    @Column
    public UUID PaymentId;
    @Column
    public UUID PaymentType;
    @Column
    public UUID OrderPaymentTypeUniqueId;
    @Column
    public String InvoiceRef;
}
