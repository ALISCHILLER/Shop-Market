package com.varanegar.vaslibrary.model.DeliveryReportView;

import androidx.annotation.Nullable;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;


/**
 * Created by s.foroughi on 18/01/2017.
 */

@Table
public class DeliveryReportViewModel extends BaseModel{
    @Column
    public String CustomerId;
    @Column
    public String CustomerCode;
    @Column
    public String CustomerName;
    @Nullable
    @Column
    public String OrderUniqueId;
    @Column
    public Currency InvoiceNetAmount;
    @Column
    public Currency InvoiceReturnNetAmount;
    @Column
    public Currency TotalReturnNetAmount;
    @Column
    public Currency TotalOldInvoiceAmount;
    @Column
    public Currency TotalPayAbleAmount;
    @Column
    public Currency ReceiptAmount;
    @Column
    public Currency CashAmount;
    @Column
    public Currency ChequeAmount;
    @Column
    public Currency CardAmount;
    @Column
    public Currency SettlementDiscountAmount;
    @Column
    public Currency CreditAmount;
    @Nullable
    @Column
    public String DealerName;

 }
