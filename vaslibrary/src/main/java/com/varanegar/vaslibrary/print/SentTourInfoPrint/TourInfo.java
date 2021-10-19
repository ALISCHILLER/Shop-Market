package com.varanegar.vaslibrary.print.SentTourInfoPrint;

import com.varanegar.java.util.Currency;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Created by g.aliakbar on 17/04/2018.
 */

public class TourInfo implements Serializable  {
    public int DayCustomersCount;
    public int DayVisitedCount;
    public int DayOrderedCount;
    public Currency DayOrderSum;
    public int DayLackOfVisitCount;
    public int DayLackOfOrderCount;
    public double DayVisitRatio;
    public int TotalCustomersCount;
    public int TotalVisitedCount;
    public int TotalOrderedCount;
    public Currency TotalOrderSum;
    public int TotalLackOfVisitCount;
    public int TotalLackOfOrderCount;
    public double TotalVisitRatio;
    public String VisitTime;
    public String TourTime;
    public boolean Spd;
    public UUID TourId;
    public int TourNo;
    public UUID PersonnelId;
    public Date Time;

    // dist
    public int LackOfDeliveriesCount;
    public int DeliveriesCount;
    public int ReturnsCount;
    public int PartialDeliveriesCount;
    public String DistNo;
}
