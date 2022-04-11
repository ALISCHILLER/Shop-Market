package com.varanegar.supervisor.webapi.model_new.datamanager;


import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.Date;
import java.util.UUID;


@Table
public class CustomerPinModel extends BaseModel {
    @Column
    public UUID CustomerUniqueId;
    @Column
    public String CustomerCode;
    @Column
    public String CustomerName;
    @Column
    public Date PinDate;
    @Column
    public String PinPDate;
    @Column
    public String DealerName;
    @Column
    public String Pin1;
    @Column
    public String Pin2;
    @Column
    public String Pin3;
    @Column
    public String Pin4;

}
