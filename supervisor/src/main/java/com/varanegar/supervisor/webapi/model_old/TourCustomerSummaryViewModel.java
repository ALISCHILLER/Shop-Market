package com.varanegar.supervisor.webapi.model_old;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by A.Torabi on 7/7/2018.
 */
@Table
public class TourCustomerSummaryViewModel extends BaseModel {
    @Column
    public UUID TourUniqueId;
    @Column
    public UUID CustomerCallUniqueId;
    @Column
    public String CustomerCode;
    @Column
    public String CustomerName;
    @Column
    public String Address;
    @Column
    public String StoreName;
    @Column
    public UUID CallStatusUniqueId;
    @Column
    public String CallStatusName;
    @Column
    public UUID VisitStatusUniqueId;
    @Column
    public String VisitStatusName;
    @Column
    public UUID NoSaleReasonUniqueId;
    @Column
    public String NoSaleReasonName;
    @Column
    public String DistributionNo;
    @Column
    public String DistributionPDate;
    @Column
    public boolean IsActive;
}
