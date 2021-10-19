package com.varanegar.supervisor.webapi;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.Date;
import java.util.UUID;

/**
 * Created by A.Torabi on 7/3/2018.
 */
@Table
public class TourStatusSummaryViewModel extends BaseModel {
    @Column
    public String AgentName;
    @Column
    public String TourStatusName;
    @Column
    public UUID DriverUniqueId;
    @Column
    public String DriverName;
    @Column
    public UUID VehicleUniqueId;
    @Column
    public String VehicleName;
    @Column
    public int TourNo;
    @Column
    public Date StartTime;
    @Column
    public String StartPTime;
    @Column
    public Date EndTime;
    @Column
    public String EndPTime;
    @Column
    public String PathTitle;
    @Column
    public int CustomerCount;
    @Column
    public int VisitCount;
    @Column
    public int NoVisitCount;
    @Column
    public int OrderCount;
    @Column
    public int NoOrderCount;
    @Column
    public int InvoiceCount;
    @Column
    public int ReturnInvoiceRequestCount;
    @Column
    public int ReturnInvoiceCount;
    @Column
    public int UndetermindCount;
    @Column
    public Currency TotalInvoiceAmount;
    @Column
    public Currency TotalOrderAmount;
    @Column
    public long AvG_OutOfStoreTime;
    @Column
    public long InStoreTime;
    @Column
    public long AvG_InStoreTime;
    @Column
    public long OutOfStoreTime;
    @Column
    public long AvG_BetweenTwoStoresTime;
    @Column
    public double OrderLineAvg;
    @Column
    public double InvoiceLineAvg;
    @Column
    public int SaleVolume;
    @Column
    public String OutOfStoreTimeAvgStr;
    @Column
    public String InStoreTimeStr;
    @Column
    public String InStoreTimeAvgStr;
    @Column
    public String OutOfStoreTimeStr;
    @Column
    public double SuccessVisitAvg;
    @Column
    public double TotalSaleAvg;
    @Column
    public String BetweenTwoStoresTimeAvgStr;
    @Column
    public boolean HasPreSale;
    @Column
    public boolean HasHotSale;
    @Column
    public boolean HasDistribution;
    @Column
    public UUID TourStatusUniqueId;
    @Column
    public Date TourDate;
    @Column
    public String TourPDate;
    @Column
    public UUID AgentUniqueId;
    @Column
    public UUID VisitTemplatePathUniqueId;
    @Column
    public boolean PaymentApproved;
    @Column
    public boolean StockLevelApproved;
}
