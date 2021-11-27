package com.varanegar.vaslibrary.webapi.customer;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 9/16/2017.
 */

public class SyncZarGetNewCustomerViewModel {
    public UUID  customerUniqueId;
    public String PersonName;
    public String StoreName;
    public String Street;
    public String Street2;
    public String Street3;
    public String Street4;
    public String Street5;
    public String PostalCode;
    public String Tel;
    public String Mobile;
    public String EconomicCode;
    public String NationalCode;
    public String CityId;
    @SerializedName("TRANSPZONE")
    public String TRANSPZONE;
    @SerializedName("KTOKD")
    public String KTOKD;
    @SerializedName("BZIRK")
    public String BZIRK;
    @SerializedName("KDGRP")
    public String KDGRP;
    @SerializedName("KVGR1")
    public String KVGR1;
    @SerializedName("KVGR2")
    public String KVGR2;
    public UUID DealerId;
    public UUID PathId;
    public String PaymentTypeId;
    public String CustomerCode;
    public Double latitude;
    public Double longitude;
    public String StateId;
    @SerializedName("KUKLA")
    public String KUKLA;
}
