package com.varanegar.vaslibrary.manager.locationmanager.viewmodel;

import com.varanegar.java.util.Currency;

import java.util.Date;
import java.util.UUID;

/**
 * Created by A.Torabi on 4/25/2018.
 */

public class SendTourEventViewModel {
    public int CustomersCount;
    public int VisitedCustomers;
    public int OrderedCustomers;
    public Currency SumOfOrdered;
    public int LackOfVisit;
    public int LackOfOrder;
    public String VisitToCustomer;
    public boolean Spd;
    public String TotalVisitTime;
    public String TotalTourTime;
    public UUID TourId;
    public int TourNo;
    public UUID PersonnelId;
    public Date Time;
    public String description;
}
