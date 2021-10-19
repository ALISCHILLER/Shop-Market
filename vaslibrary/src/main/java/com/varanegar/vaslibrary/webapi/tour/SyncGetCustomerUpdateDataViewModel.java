package com.varanegar.vaslibrary.webapi.tour;

import com.varanegar.vaslibrary.model.dataforregister.DataForRegisterModel;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by A.Torabi on 10/2/2017.
 */

public class SyncGetCustomerUpdateDataViewModel {
    public UUID CustomerId;
    public String Phone;
    public String NationalCode;
    public String EconomicCode;
    public String CustomerName;
    public String StoreName;
    public String Address;
    public String Mobile;
    public UUID CityId;
    public UUID CountyId;
    public UUID StateId;
    public String OwnerTypeRef;
    public UUID CustomerActivityId;
    public String CustomerActivityRef;
    public UUID CustomerLevelId;
    public String CustomerLevelRef;
    public UUID CustomerCategoryId;
    public String CustomerCategoryRef;
    public String PostalCode;
    public int CityZone;
    // for zar makaron
    public ArrayList<DataForRegisterModel> ExtraFields;
}
