package com.varanegar.vaslibrary.ui.report.review;

import com.varanegar.framework.database.model.BaseModel;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 7/10/2018.
 */

public class CustomerCallViewModel extends BaseModel {
    public String Description;
    public String OrderTypeName;
    public UUID OrderTypeUniqueId;
    public String OrderDescription;
    public String ReturnDescription;
    public String DistributerName;
    public Date StartTime;
    public Date EndTime;
    public UUID TourUniqueId;
    public int TourNo;
    public UUID VisitStatusUniqueId;
    public String VisitStatusName;
    public UUID NoSaleReasonUniqueId;
    public String NoSaleReasonName;
    public UUID CallStatusUniqueId;
    public String CallStatusName;
    public String CallPDate;
    public Date CallDate;
    public String SalePDate;
    public UUID CustomerUniqueId;
    public String CustomerName;
    public String CustomerCode;
    public String StoreName;
    public UUID DealerUniqueId;
    public String DealerName;
    public String Address;
    public String StartPTime;
    public String EndPTime;
    public UUID StockUniqueId;
    public String StockName;
    public double Longitude;
    public double Latitude;
    public Date ReceiveDate;
    public String ReceivePDate;
    public String BackOfficeOrderNoCollection;
    public String BackOfficeReturnOrderNoCollection;
    public String LocalPaperNoCollection;
    public String DistributionNo;
    public long VisitDuration;
    public String VisitDurationStr;
    public List<CustomerCallOrderViewModel> CustomerCallOrders;
    public List<CustomerCallReturnViewModel> CustomerCallReturns;
}
