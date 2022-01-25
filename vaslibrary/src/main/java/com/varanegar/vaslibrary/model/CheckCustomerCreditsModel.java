package com.varanegar.vaslibrary.model;

import com.google.gson.annotations.SerializedName;
import com.varanegar.java.util.Currency;

import java.math.BigDecimal;

public class CheckCustomerCreditsModel {

    public  String customerBackOfficeCode;

    public Currency CustomerCreditLimit;

    public Currency customerUsedCredit;

    public Currency customerRemainCredit;
}
