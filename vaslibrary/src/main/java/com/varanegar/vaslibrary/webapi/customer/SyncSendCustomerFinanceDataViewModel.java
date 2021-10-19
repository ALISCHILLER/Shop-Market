package com.varanegar.vaslibrary.webapi.customer;

import java.util.UUID;

/**
 * Created by A.Torabi on 6/3/2018.
 */

public class SyncSendCustomerFinanceDataViewModel {
    public UUID CustomerUniqueId;
    public double RemainDebit;
    public double RemainCredit;
    public double CustRemAmountForSaleOffice;
    public double CustRemAmountAll;
    public double CustomerRemain;
    public double InitCredit;
    public double InitDebit;
    public double OpenInvoicesCount;
    public double OpenInvoicesAmount;
    public double OpenChequeCount;
    public double OpenChequeAmount;
    public double ReturnChequeCount;
    public double ReturnChequeAmount;
    public String CustomerMessage;
    public String ErrorMessage;
}
