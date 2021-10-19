package com.varanegar.supervisor.model;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.validation.annotations.NotEmpty;
import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

@Table
public class SupervisorCustomerModel extends BaseModel {
    @Column
    @NotNull
    public String CustomerName;
    @Column
    @NotEmpty
    public String CustomerCode;
    @Column
    public String Address;
    @Column
    public String Phone;
    @Column
    public String StoreName;
    @Column
    public String Mobile;
    @Column
    public String NationalCode;
    @Column
    public String CustomerLevel;
    @Column
    public String CustomerActivity;
    @Column
    public String CustomerCategory;
    @Column
    public UUID DealerId;
    @Column
    public String DealerName;
    @Column
    public String PathTitle;
    @Column
    public double Latitude;
    @Column
    public double Longitude;
}
