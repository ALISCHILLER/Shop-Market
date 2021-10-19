package com.varanegar.vaslibrary.model.PaymentReportView;

import androidx.annotation.Nullable;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import com.varanegar.framework.validation.annotations.NotNull;

import java.util.Date;
import java.util.UUID;

/**
 * Created by s.foroughi on 16/01/2017.
 */

@Table
public class PaymentReportViewModel extends BaseModel{

    @Column
    public String CustomerId;
    @Column
    public String CustomerCode;
    @Column
    public String CustomerName;
    @Column
    public UUID PaymentId;
    @Nullable
    @Column
    public UUID InvoiceId;
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
    @Column
    public String ChqNo;
    @Column
    public Date ChqDate;
    @Column
    public String BankName;
    @Column
    public String ChqBranchName;
    @Column
    public String ChqBranchCode;
    @Column
    public String ChqAccountNo;
    @Column
    public String ChqAccountName;
    @Column
    public String FollowNo;
    @Column
    public String CityName;
    @Column
    public Currency PaidAmount;
    @Nullable
    @Column
    public String DealerName;
    @Column
    public UUID PaymentType;
}
