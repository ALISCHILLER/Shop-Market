package com.varanegar.vaslibrary.model.customer;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.validation.annotations.NotEmpty;
import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

@Table
public class SupervisorFullCustomerModel extends BaseModel {

    @Column
    @NotEmpty
    public UUID customerUniqueId;
    @Column
    @NotEmpty
    public String CustomerCode;
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
    public String NationalCode;
    @Column
    public String Mobile;
    @Column
    public double Longitude;
    @Column
    public double Latitude;
    @Column
    public UUID dealerUniqueId;
    @Column
    public String DealerName;
//    @Column
//    public UUID VisitTemplatePathUniqueId ;
//    @Column
//    public String VisitTemplatePathTitle ;
    @Column
    public UUID customerLevelUniqueId;
    @Column
    public String CustomerLevel;
    @Column
    public String CustomerActivity;
    @Column
    public UUID customerActivityUniqueId;
    @Column
    public String CustomerCategory;
    @Column
    public UUID customerCategoryUniqueId;
    @Column
    public UUID CityId;
    @Column
    public UUID StateId;
    @Column
    public UUID CenterId;
}
