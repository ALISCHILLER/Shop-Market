package com.varanegar.vaslibrary.manager.c_shipToparty;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

@Table
public class CustomerShipToPartyModel extends BaseModel {
    @Column
    public UUID SoldToPartyUniqueId;
    @Column
    public String BackOfficeId;
    @Column
    @NotNull
    public String CustomerName;
    @Column
    public String Address;
    @Column
    public String Phone;
    @Column
    public String StoreName;
    @Column
    public String Mobile;
    @Column
    public double Longitude;
    @Column
    public double Latitude;
    @Column
    public String PostCode;
    @Column
    public String EconomicCode;
    @Column
    public String NationalCode;
    @Column
    public boolean IsActive;
    @Column
    public int IgnoreLocation;

    @Override
    public String toString() {
        return BackOfficeId+"-"+Address+"-"+CustomerName;
    }
}


