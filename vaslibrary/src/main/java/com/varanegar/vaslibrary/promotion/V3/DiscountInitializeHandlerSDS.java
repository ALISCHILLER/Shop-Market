package com.varanegar.vaslibrary.promotion.V3;

import android.content.Context;

import com.varanegar.framework.base.VaranegarApplication;

/**
 * Created by A.Razavi on 11/14/2017.
 */

public class DiscountInitializeHandlerSDS extends DiscountInitializeHandler {

    int backofficeversion = 19;

    public DiscountInitializeHandlerSDS(Context context) {
        super(context);
    }

    @Override
    public String getInitQuery() {
        return "";
    }

    @Override
    public int getBackOffice() {
        return 1;
    }

    @Override
    public int getBackOfficeVersion() {
        return backofficeversion;
    }



    @Override
    public String getFillDiscountConditionQuery() {
        return "INSERT INTO DiscountConditionSDS4_19 \n" +
                "SELECT Id\n" +
                ",DiscountRef\n" +
                ",DCRef\n" +
                ",CustCtgrRef\n" +
                ",CustActRef\n" +
                ",CustLevelRef\n" +
                ",PayType\n" +
                ",PaymentUsanceRef\n" +
                ",OrderType\n" +
                ",SaleOfficeRef\n" +
                ",CustGroupRef\n" +
                ",CustRef\n" +
                ",OrderNo\n" +
                ",StateRef\n" +
                ",AreaRef\n" +
                ",SaleZoneRef\n" +
                ",MainCustTypeRef\n" +
                ",SubCustTypeRef\n" +
                "FROM DiscountCondition ";

    }

    @Override
    public String getFillDiscountItemQuery() {
        return "INSERT INTO DiscountDiscountItemCount " +
                "SELECT DisRef, GoodsRef\n" +
                " FROM DiscountItemCount";
    }

    @Override
    public String getFillDiscountDisSaleQuery() {
        return "INSERT INTO DiscountDisSaleSDS " +
                " SELECT UniqueId\n" +
                ",HdrRef\n" +
                ",ItemRef\n" +
                ",RowNo\n" +
                ",ItemType\n" +
                ",DisRef\n" +
                ",DisGroup\n " +
                ",0 as CPriceRef\n " +
                " FROM CustomerOldInvoiceDisSale ";
    }

    @Override
    public String getFillDiscountDisSaleVnLtQuery() {
        return "";
    }

    @Override
    public String getFillCPriceQuery() {
        return "INSERT INTO DiscountContractPriceSDS\n" +
                "SELECT BackOfficeId as ID,\n" + //
                "CPriceType,\n" +
                "Code,\n" +
                "GoodsRef,\n" +
                "UnitRef,\n" +
                "CustRef,\n" +
                "CustCtgrRef,\n" +
                "DCRef,\n" +
                "CustActRef,\n" +
                "MinQty,\n" +
                "MaxQty,\n" +
                "SalePrice,\n" +
                "StartDate,\n" +
                "EndDate,\n" +
                "BuyTypeRef,\n" +
                "UsanceDay,\n" +
                "StateRef,\n" +
                "CountyRef,\n" +
                "AreaRef,\n" +
                "Priority,\n" +
                "OrderTypeRef,\n" +
                "GoodsGroupRef,\n" +
                "MainTypeRef,\n" +
                "SubTypeRef,\n" +
                "CustLevelRef,\n" +
                "UserRef,\n" +
                "ModifiedDate,\n" +
                "DealerCtgrRef,\n" +
                "ModifiedDateBeforeSend,\n" +
                "UserRefBeforeSend,\n" +
                "BatchNoRef,\n" +
                "BatchNo,\n" +
                "UniqueId\n" +
                "FROM ContractPriceSDS";
    }

    @Override
    public String getFillPriceHistory() {
        return "INSERT INTO DiscountPriceHistory\n" +
                "SELECT BackOfficeId as ID,\n" +
                "GoodsRef,\n" +
                "DCRef,\n" +
                "SalePrice,\n" +
                "UserPrice,\n" +
                "GoodsCtgrRef,\n" +
                "StartDate,\n" +
                "EndDate,\n" +
                "IsActive,\n" +
                "UsanceDay,\n" +
                "CustRef,\n" +
                "CustActRef,\n" +
                "CustCtgrRef,\n" +
                "CustLevelRef,\n" +
                "StateRef,\n" +
                "AreaRef,\n" +
                "CountyRef,\n" +
                "UserRef,\n" +
                "ModifiedDate,\n" +
                "ModifiedDateBeforeSend,\n" +
                "UserRefBeforeSend,\n" +
                "UniqueId\n" +
                "FROM PriceHistory ";
    }

    @Override
    public String getFillCustomerQuery() {
        return "INSERT INTO DiscountCustomer (CustomerId,\n" +
                "CustomerCode,\n" +
                "CustomerName,\n" +
                "Address,\n" +
                "Phone,\n" +
                "StoreName,\n" +
                "Mobile,\n" +
                "Longitude,\n" +
                "Latitude,\n" +
                "Alarm,\n" +
                "EconomicCode,\n" +
                "NationalCode,\n" +
                "totalorder,\n" +
                "visitstatus,\n" +
                "visitstatusreason,\n" +
                "salesmanid,\n" +
                "pasajid,\n" +
                "visitlat,\n" +
                "visitlng,\n" +
                "gpssave,\n" +
                "nosalereasonid,\n" +
                "ErrorType,\n" +
                "ErrorMessage,\n" +
                "NoSend,\n" +
                "SendDataStatus,\n" +
                "IsInfoSend,\n" +
                "IsInfoEdit,\n" +
                "SortId,\n" +
                "CityId,\n" +
                "StateId,\n" +
                "CustomerLevelId,\n" +
                "CustomerActivityId,\n" +
                "CustomerCategoryId,\n" +
                "CenterId,\n" +
                "ZoneId,\n" +
                "AreaId,\n" +
                "HasCancelOrder,\n" +
                "IsPrinted,\n" +
                "ActionGUID,\n" +
                "ActionType,\n" +
                "IsNewCustomer,\n" +
                "DCRef,\n" +
                "CountyId,\n" +
                "CustomerSubGroup1Id,\n" +
                "CustomerSubGroup2Id,\n" +
                "UniqueId,\n" +
                "DcCode,\n" +
                "CustomerCategoryCode,\n" +
                "CustomerActivityCode,\n" +
                "CustomerLevelCode,\n" +
                "CityZone,\n" +
                "CityArea,\n" +
                "OwnerTypeRef,\n" +
                "OwnerTypeCode,\n" +
                "SalePathNo,\n" +
                "SalePathRef,\n" +
                "SaleAreaRef,\n" +
                "SaleAreaNo,\n" +
                "SaleZoneRef,\n" +
                "SaleZoneNo,\n" +
                "CityCode,\n" +
                "CountyRef,\n" +
                "CountyCode,\n" +
                "StateCode,\n" +
                "DistPathId,\n" +
                "DistPathNo,\n" +
                "DistAreaId,\n" +
                "DistAreaNo,\n" +
                "DistZoneId,\n" +
                "DistZoneNo,\n" +
                "RemainDebit,\n" +
                "RemainCredit,\n" +
                "OpenChequeAmount,\n" +
                "OpenChequeCount,\n" +
                "ReturnChequeCount,\n" +
                "ReturnChequeAmount,\n" +
                "OpenInvoiceCount,\n" +
                "OpenInvoiceAmount,\n" +
                "CustomerRemain,\n" +
                "InitDebit,\n" +
                "InitCredit,\n" +
                "CustChequeRetQty,\n" +
                "CustCtgrName,\n" +
                "CustActName,\n" +
                "CustLevelName,\n" +
                "CityName,\n" +
                "StateName,\n" +
                "DcName,\n" +
                "OwnerTypeName,\n" +
                "SalePathName,\n" +
                "SaleAreaName,\n" +
                "SaleZoneName,\n" +
                "DistPathName,\n" +
                "DistAreaName,\n" +
                "DistZoneName,\n" +
                "CountyName,\n" +
                "ExtraField1,\n" +
                "ExtraField2,\n" +
                "ExtraField3,\n" +
                "ExtraField4,\n" +
                "ExtraField5,\n" +
                "ExtraField6,\n" +
                "ExtraField7,\n" +
                "ExtraField8,\n" +
                "ExtraField9,\n" +
                "ExtraField10,\n" +
                "ExtraField11,\n" +
                "ExtraField12,\n" +
                "ExtraField13,\n" +
                "ExtraField14,\n" +
                "ExtraField15,\n" +
                "ExtraField16,\n" +
                "ExtraField17,\n" +
                "ExtraField18,\n" +
                "ExtraField19,\n" +
                "ExtraField20)\n" +
                "SELECT  \n" +
                " BackOfficeId CustomerId,  \n" +
                " CustomerCode,  \n" +
                " CustomerName,  \n" +
                " Address,  \n" +
                " Phone,  \n" +
                " StoreName,  \n" +
                " Mobile,  \n" +
                " Longitude,  \n" +
                " Latitude,  \n" +
                " Alarm,  \n" +
                " EconomicCode,  \n" +
                " NationalCode,  \n" +
                " 0 as totalorder,  \n" +
                " 0 as visitstatus,  \n" +
                " 0 as visitstatusreason,  \n" +
                " -1 as salesmanid,  \n" +
                " 0 as pasajid,  \n" +
                " 0 as visitlat,  \n" +
                " 0 as visitlng,  \n" +
                " 0 as gpssave,  \n" +
                " 0 as nosalereasonid,  \n" +
                " ErrorType,  \n" +
                " ErrorMessage,  \n" +
                " 0 as NoSend,  \n" +
                " 0 as SendDataStatus,  \n" +
                " 0 as IsInfoSend,  \n" +
                " 0 as IsInfoEdit,  \n" +
                " 0 as SortId,  \n" +
                " CityRef CityId,  \n" +
                " StateRef StateId,  \n" +
                " CustomerLevelRef as CustomerLevelId,  \n" +
                " CustomerActivityRef as  CustomerActivityId,  \n" +
                " CustomerCategoryRef as CustomerCategoryId,  \n" +
                " 0 as CenterId,  \n" +
                " SaleZoneRef as ZoneId,  \n" +
                " CityRef as AreaId,  \n" +
                " HasCancelOrder,  \n" +
                " 0 as IsPrinted,  \n" +
                " '' as ActionGUID,  \n" +
                " 0 as ActionType,  \n" +
                " 0 as IsNewCustomer,  \n" +
                " DCRef,  \n" +
                " CountyRef as CountyId,  \n" +
                " CustomerSubGroup1Id,  \n" +
                " CustomerSubGroup2Id,  \n" +
                " null UniqueId,  \n" +
                " DcCode,  \n" +
                " CustCtgrCode as CustomerCategoryCode,  \n" +
                " CustActCode as CustomerActivityCode,  \n" +
                " CustLevelCode as CustomerLevelCode,  \n" +
                "  CityZone,  \n" +
                "  CityArea,  \n" +
                " OwnerTypeRef,  \n" +
                " OwnerTypeCode,  \n" +
                " SalePathNo,  \n" +
                " SalePathRef,  \n" +
                " SaleAreaRef,  \n" +
                " SaleAreaNo,  \n" +
                " SaleZoneRef,  \n" +
                " SaleZoneNo,  \n" +
                " CityCode,  \n" +
                " CountyRef,  \n" +
                " CountyCode,  \n" +
                " StateCode,  \n" +
                " DistPathRef as DistPathId,  \n" +
                " DistPathNo,  \n" +
                " DistArearef as DistAreaId,  \n" +
                " DistAreaNo,  \n" +
                " DistZoneRef as DistZoneId,  \n" +
                " DistZoneNo,  \n" +
                " RemainDebit,  \n" +
                " RemainCredit,  \n" +
                " OpenChequeAmount,  \n" +
                " OpenChequeCount,  \n" +
                " ReturnChequeCount,  \n" +
                " ReturnChequeAmount,  \n" +
                " OpenInvoiceCount,  \n" +
                " OpenInvoiceAmount,  \n" +
                " CustomerRemain,  \n" +
                " InitDebit,  \n" +
                " InitCredit,  \n" +
                " 0 CustChequeRetQty,\n " +
                "'' as CustCtgrName,\n" +
                "'' as CustActName,\n" +
                "'' as CustLevelName,\n" +
                "'' as CityName,\n" +
                "'' as StateName,\n" +
                "'' as DcName,\n" +
                "'' as OwnerTypeName,\n" +
                "'' as SalePathName,\n" +
                "'' as SaleAreaName,\n" +
                "'' as SaleZoneName,\n" +
                "'' as DistPathName,\n" +
                "'' as DistAreaName,\n" +
                "'' as DistZoneName,\n" +
                "'' as CountyName,\n" +
                "'' as ExtraField1,\n" +
                "'' as ExtraField2,\n" +
                "'' as ExtraField3,\n" +
                "'' as ExtraField4,\n" +
                "'' as ExtraField5,\n" +
                "'' as ExtraField6,\n" +
                "'' as ExtraField7,\n" +
                "'' as ExtraField8,\n" +
                "'' as ExtraField9,\n" +
                "'' as ExtraField10,\n" +
                "'' as ExtraField11,\n" +
                "'' as ExtraField12,\n" +
                "'' as ExtraField13,\n" +
                "'' as ExtraField14,\n" +
                "'' as ExtraField15,\n" +
                "'' as ExtraField16,\n" +
                "'' as ExtraField17,\n" +
                "'' as ExtraField18,\n" +
                "'' as ExtraField19,\n" +
                "'' as ExtraField20\n" +
                "FROM Customer \n";
    }

    @Override
    public String getFillOrderPrizeQuery(int orderId) {
        return "INSERT INTO DiscountOrderPrize(Id," +
                "OrderRef," +
                "DiscountRef," +
                "GoodsRef," +
                "Qty," +
                "SaleQty," +
                "IsAutomatic," +
                "OrderDiscountRef)\n" +
                "SELECT Id," +
                "OrderRef," +
                "DiscountRef," +
                "GoodsRef," +
                "Qty," +
                "IFNULL(SaleQty,Qty)," +
                "IsAutomatic," +
                "null \n" +
                "FROM OrderPrizeHistory \n" +
                "WHERE OrderRef = " + orderId;

    }

    @Override
    public String getFillProductTaxInfoQuery() {
        return "";
    }

    @Override
    public String getFillDisSalePrizePackageSDSQuery() {
        return "INSERT INTO DiscountDisSalePrizePackageSDS \n" +
                "SELECT BackOfficeId as ID\n" +
                ",SaleRef\n" +
                ",DiscountRef\n" +
                ",MainGoodsPackageItemRef\n" +
                ",ReplaceGoodsPackageItemRef\n" +
                ",PrizeCount\n" +
                ",PrizeQty\n" +
                " FROM DisSalePrizePackageSDS ";
    }

    @Override
    public String getFillOrderTypeQuery() {
        return "INSERT INTO DiscountOrderType (OrderTypeId,OrderTypeName)\n" +
                "SELECT BackOfficeId\n" +
                ",OrderTypeName\n " +
                "FROM CustomerOrderType";
    }

    @Override
    public String getFillDisTypeQuery() {
        return "";
    }

}


