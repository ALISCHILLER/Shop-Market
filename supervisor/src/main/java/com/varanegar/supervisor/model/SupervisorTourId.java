package com.varanegar.supervisor.model;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import java.util.UUID;

public class SupervisorTourId {

    public UUID uniqueId;
    @Nullable
    public String agentName;
    @Nullable
    public String agentMobail;

    public UUID tourStatusUniqueId;
    @Nullable
    public String tourStatusName;
    @Nullable
    public String driverName;
    @Nullable
    public String vehicleName;
    @Nullable
    public String stockName;
    @Nullable
    public UUID dayVisitPathId;
    public UUID agentId;
    public int tourNo;
    public boolean hasPreSale;
    public boolean hasHotSale;
    public String tourDate;
    @Nullable
    public String errMsg;
    public boolean isPhoneSale;
    @Nullable
    public ArrayList<String> pins;
    public String ZarNotificationToken ;
}
