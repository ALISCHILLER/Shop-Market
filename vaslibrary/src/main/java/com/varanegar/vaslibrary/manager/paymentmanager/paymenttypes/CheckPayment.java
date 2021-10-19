package com.varanegar.vaslibrary.manager.paymentmanager.paymenttypes;

import com.varanegar.java.util.Currency;

import java.util.Date;
import java.util.UUID;

/**
 * Created by A.Torabi on 12/9/2017.
 */

public class CheckPayment {
    public Date Date;
    public Currency Amount;
    public UUID CityId;
    public UUID BankId;
    public String CheckAccountNumber;
    public String CheckNumber;
    public Date ChqDate;
    public String BranchName;
    public String BranchCode;
    public String AccountName;
    public String SayadNumber;
}
