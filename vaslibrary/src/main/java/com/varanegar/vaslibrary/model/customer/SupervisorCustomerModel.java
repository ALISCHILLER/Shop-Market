package com.varanegar.vaslibrary.model.customer;

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
    @Column
    public UUID CenterId;
    @Column
    public UUID CityId;
    @Column
    public UUID CustomerActivityId;
    @Column
    public UUID CustomerCategoryId;
    @Column
    public UUID CustomerLevelId;
    @Column
    public UUID AreaId;
    @Column
    public UUID StateId;
    @Column
    public Boolean isActive;
    @Column
    public Boolean isPenddingChange;
    @Column
    public String newStoreName;
    @Column
    public String newAddress;
    @Column
    public String newPhone;
    @Column
    public String newMobile;
    @Column
    public String newPostCode;
    @Column
    public String newCustomerLevelName;
    @Column
    public String newCustomerActivityName;
    @Column
    public String newCustomerCategoryName;
    @Column
    public String newNationalCode;
    @Column
    public String newEconomicCode;
    @Column
    public String newCityName;
    @Column
    public String PostCode;
}
