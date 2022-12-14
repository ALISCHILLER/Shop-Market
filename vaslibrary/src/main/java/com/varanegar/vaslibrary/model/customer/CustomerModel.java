package com.varanegar.vaslibrary.model.customer;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.validation.annotations.NotEmpty;
import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;
import com.varanegar.vaslibrary.model.location.LocationModel;

import java.util.UUID;

/**
 * Created by atp on 12/9/2016.
 */
@Table
public class CustomerModel extends BaseModel {
    @Column
    public int BackOfficeId;
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
    public double Longitude;
    @Column
    public double Latitude;
    @Column
    public String NationalCode;
    @Column
    public boolean IsActive;
    @Column
    public UUID CountyId;
    @Column
    public UUID CityId;
    @Column
    public int CityRef;
    @Column
    public UUID StateId;
    @Column
    public int StateRef;
    @Column
    public UUID CustomerLevelId;
    @Column
    public UUID CustomerActivityId;
    @Column
    public UUID CustomerCategoryId;
    @Column
    public int CustomerLevelRef;
    @Column
    public int CustomerActivityRef;
    @Column
    public int CustomerCategoryRef;
    @Column
    public Currency RemainDebit;
    @Column
    public Currency RemainCredit;
    @Column
    public Currency CustRemAmountForSaleOffice;
    @Column
    public Currency CustRemAmountAll;
    @Column
    public Currency CustomerRemain;
    @Column
    public Currency InitCredit;
    @Column
    public Currency InitDebit;
    @Column
    public int OpenInvoiceCount;
    @Column
    public Currency OpenInvoiceAmount;
    @Column
    public int OpenChequeCount;
    @Column
    public Currency OpenChequeAmount;
    @Column
    public int ReturnChequeCount;
    @Column
    public Currency ReturnChequeAmount;
    @Column
    public boolean checkCredit;
    @Column
    public boolean checkDebit;
    @Column
    public int rowIndex;
    @Column
    public String Alarm;
    @Column
    public String EconomicCode;
    @Column
    public boolean IsNewCustomer;
    @Column
    public int SalePathRef;
    @Column
    public int SalePathNo;
    @Column
    public int SaleAreaRef;
    @Column
    public int SaleAreaNo;
    @Column
    public int SaleZoneRef;
    @Column
    public int SaleZoneNo;
    @Column
    public int DistPathRef;
    @Column
    public int DistPathNo;
    @Column
    public int DistAreaRef;
    @Column
    public int DistAreaNo;
    @Column
    public int DistZoneRef;
    @Column
    public int DistZoneNo;
    @Column
    public int CityCode;
    @Column
    public String CountyCode;
    @Column
    public int CountyRef;
    @Column
    public String CustCtgrCode;
    @Column
    public String CustActCode;
    @Column
    public String CustLevelCode;
    @Column
    public String CityArea;
    @Column
    public int OwnerTypeRef;
    @Column
    public String OwnerTypeCode;
    @Column
    public String StateCode;
    @Column
    public UUID CenterId;
    @Column
    public UUID ZoneId;
    @Column
    public UUID AreaId;
    @Column
    public String DcCode;
    @Column
    @SerializedName("dcRef")
    public Integer DCRef;
    @Column
    public String CustomerSubGroup2Id;
    @Column
    public String CustomerSubGroup1Id;
    @Column
    public int CountChq;
    @Column
    public Currency AmountChq;
    @Column
    public int ErrorType;
    @Column
    public String ErrorMessage;
    @Column
    public int CityZone;
    @Column
    public String CustomerPostalCode;
    @Column
    @SerializedName("dcName")
    public String DCName;
    @Column
    public String RealName; // we use it for default payment uniqueId in thirdParty!!!
    @Column
    public String Barcode;
    @Column
    public Integer PayableTypes;
    @Column
    public String CustomerMessage;
    @Column
    public int IgnoreLocation;
    @Column
    public Integer ParentCustomerId;
    @Column
    public int OPathId;
    @Column
    public String Description;
    @Column
    public String CodeNaghsh;
    @Column
    public boolean HasNationalCodeImage;
    @Column
    public String IsZarShopCustomer;
    @Column
    public int DegreeStar;
    @Nullable
    public LocationModel getLocation() {
        if (Latitude == 0 && Longitude == 0)
            return null;
        LocationModel locationModel = new LocationModel();
        locationModel.Latitude = Latitude;
        locationModel.Longitude = Longitude;
        locationModel.CustomerId = UniqueId;
        return locationModel;
    }
}
