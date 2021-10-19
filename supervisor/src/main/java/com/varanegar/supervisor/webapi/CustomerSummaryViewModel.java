package com.varanegar.supervisor.webapi;

import com.varanegar.java.util.Currency;

import java.util.UUID;

public class CustomerSummaryViewModel {
    public String CustomerCode;
    public UUID CustomerUniqueId;
    public Currency RemainDebit;
    public Currency RemainCredit;
    public Currency CustRemAmountForSaleOffice;
    public Currency CustRemAmountAll;
    public Currency CustomerRemain;
    public Currency InitCredit;
    public Currency InitDebit;
    public int OpenInvoicesCount;
    public Currency OpenInvoicesAmount;
    public int OpenChequeCount;
    public Currency OpenChequeAmount;
    public int ReturnChequeCount;
    public Currency ReturnChequeAmount;
}
