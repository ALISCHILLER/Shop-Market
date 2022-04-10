package com.varanegar.supervisor.webapi.model_old;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;
import com.varanegar.supervisor.webapi.model_old.CustomerCallOrderViewModel;
import com.varanegar.supervisor.webapi.model_old.CustomerCallReturnViewModel;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 7/10/2018.
 */
@Table
public class CustomerCallViewModel extends BaseModel {
    public String Description;
    @Column
    public String OrderTypeName;
    public UUID OrderTypeUniqueId;
    public String OrderDescription;
    public String ReturnDescription;
    public String DistributerName;
    @Column
    public Date StartTime;
    @Column
    public Date EndTime;
    public UUID TourUniqueId;
    @Column
    public int TourNo;
    public UUID VisitStatusUniqueId;
    public String VisitStatusName;
    public UUID NoSaleReasonUniqueId;
    public String NoSaleReasonName;
    public UUID CallStatusUniqueId;
    @Column
    public String CallStatusName;
    public String CallPDate;
    public Date CallDate;
    public String SalePDate;
    public UUID CustomerUniqueId;
    @Column
    public String CustomerName;
    @Column
    public String CustomerCode;
    @Column
    public String StoreName;
    public UUID DealerUniqueId;
    @Column
    public String DealerName;
    @Column
    public String Address;
    @Column
    public String StartPTime;
    @Column
    public String EndPTime;
    public UUID StockUniqueId;
    @Column
    public String StockName;
    public double Longitude;
    public double Latitude;
    public Date ReceiveDate;
    public String ReceivePDate;
    public String BackOfficeOrderNoCollection;
    @Column
    public String BackOfficeReturnOrderNoCollection;
    @Column
    public String LocalPaperNoCollection;
    public String DistributionNo;
    @Column
    public long VisitDuration;
    @Column
    public String VisitDurationStr;
    @Column
    public String OrderPaymentTypeName;
    @Column
    public Currency TotalRequestAmount;
    @Column
    public String Phone;

    public List<CustomerCallOrderViewModel> CustomerCallOrders;
    public List<CustomerCallReturnViewModel> CustomerCallReturns;

    public boolean isReadonly() {
        return !CallStatusUniqueId.equals(UUID.fromString("9068C65D-CA53-44FC-9D31-6E655334BD3E")) && !CallStatusUniqueId.equals(UUID.fromString("1FCCAB88-8FFD-4FF6-89F8-A45AC69D6649"));
    }
}
