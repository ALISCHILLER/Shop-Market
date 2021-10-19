package com.varanegar.vaslibrary.ui.fragment.settlement;

import androidx.annotation.Nullable;

import com.varanegar.java.util.Currency;

import java.util.Date;

/**
 * Created by A.Torabi on 11/11/2018.
 */

public class TransactionData {
    public String Id;
    public String Amount;
    public Currency PaidAmount;
    public String InvoiceNo;
    public String TransactionNo;
    public TransactionStatus Status;
    public String ErrorString;
    public String Description;
    public Date CreateTime;
    public Date PaymentTime;

    public static TransactionData create(String amount, String id, @Nullable String invoiceNo, @Nullable String description) {
        TransactionData transactionData = new TransactionData();
        transactionData.Status = TransactionStatus.Created;
        transactionData.PaidAmount = Currency.ZERO;
        transactionData.Amount = amount;
        transactionData.Id = id;
        transactionData.InvoiceNo = invoiceNo;
        transactionData.Description = description;
        transactionData.CreateTime = new Date();
        return transactionData;
    }

    public void setPaymentFailure(String transactionNo, Currency paidAmount, String error) {
        this.TransactionNo = transactionNo;
        this.PaidAmount = paidAmount;
        this.Status = TransactionStatus.Failed;
        this.ErrorString = error;
    }

    public void setPaymentCancel(String transactionNo, String error) {
        this.TransactionNo = transactionNo;
        this.PaidAmount = Currency.ZERO;
        this.Status = TransactionStatus.Canceled;
        this.ErrorString = error;
    }
}
