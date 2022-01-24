package com.varanegar.supervisor.customreport.orderstatus.dbModel;
import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

@Table
public class OrderStatusReportFlatdbModel extends BaseModel {

    @Column
    public int level;
    @Column
    public String date;
    @Column
    public Double orderWeight;
    @Column
    public Double pendingOrderWeight;
    @Column
    public Double inProgressOrderWeight;
    @Column
    public Double undeliverdOrderWeight;
    @Column
    public Double finalWeight;
    @Column
    public String dealerName;
    @Column
    public String dealerCode;
    @Column
    public Double deliverdOrderWeight;
    @Column
    public String customerName;
    @Column
    public String customerCode;


}
