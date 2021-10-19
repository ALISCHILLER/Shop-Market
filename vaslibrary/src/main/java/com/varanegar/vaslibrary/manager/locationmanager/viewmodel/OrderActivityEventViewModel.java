package com.varanegar.vaslibrary.manager.locationmanager.viewmodel;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 8/19/2017.
 */
public class OrderActivityEventViewModel {
    public String CustomerName;
    public String CustomerCode;
    public boolean IsInVisitDayPath;
    public UUID CustomerId;
    public String StoreName;

    public String Phone;
    public String Address;

    public String WatingTime;
    public String StartTime;
    public String EndTime;
    public String PStartTime;
    public String PEndTime;
    public BigDecimal OrderQty;
    public BigDecimal OrderAmunt;
    public List<OrderLineActivityEventViewModel> OrderLine;
    public UUID UniqueId;
}
