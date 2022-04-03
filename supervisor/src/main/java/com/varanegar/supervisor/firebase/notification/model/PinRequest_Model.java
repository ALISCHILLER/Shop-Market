package com.varanegar.supervisor.firebase.notification.model;

import java.util.Date;
import java.util.UUID;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;




@Table
public class PinRequest_Model extends BaseModel {
    @Column
    public UUID customerId;//key = custoemr
    @Column
    public String pinType;//key = pin
    @Column
    public UUID dealerId;//key = user
    @Column
    public String customerName;//key = custoemrName
    @Column
    public String dealerName;//key = userName
    @Column
    public Date date;
    @Column
    public UUID customer_call_order;
}
