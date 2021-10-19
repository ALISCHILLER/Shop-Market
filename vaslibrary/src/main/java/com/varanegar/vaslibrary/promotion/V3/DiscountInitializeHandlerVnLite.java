package com.varanegar.vaslibrary.promotion.V3;

import android.content.Context;

import com.varanegar.framework.base.VaranegarApplication;

/**
 * Created by A.Razavi on 11/14/2017.
 */

public class DiscountInitializeHandlerVnLite extends DiscountInitializeHandler {

    public DiscountInitializeHandlerVnLite(Context context){
        super(context);
    }

    @Override
    public String getInitQuery() {
        return null;
    }

    @Override
    public int getBackOffice() {
        return 2;
    }
    @Override
    public int getBackOfficeVersion() {
        return 0;
    }
    @Override
    public String getFillDiscountQuery() {
        return "INSERT INTO DiscountVnLite\n" +
                "(UniqueId\n" +
                ",DisGroup\n" +
                ",Id\n" +
                ",Priority\n" +
                ",StartDate\n" +
                ",EndDate\n" +
                ",Comment\n" +
                ",MinQty\n" +
                ",MaxQty\n" +
                ",MinAmount\n" +
                ",MaxAmount\n" +
                ",MinRowsCount\n" +
                ",MaxRowsCount\n" +
                ",PrizeQty\n" +
                ",AddPerc\n" +
                ",DisPerc\n" +
                ",IsPrize\n" +
                ",PrizeRef\n" +
                ",DisType\n" +
                ",ManufacturerRef\n" +
                ",ProductSubGroup1Id\n" +
                ",ProductSubGroup2Id\n" +
                ",CustomerSubGroup1Id\n" +
                ",CustomerSubGroup2Id\n" +
                ",ReduceOfQty\n" +
                ",GoodsRef\n" +
                ",DetailIsActive\n" +
                ",DetailPriority\n" +
                ",GoodsCtgrRef\n" +
                ",CustRef\n" +
                ",CustCtgrRef\n" +
                ",PromotionDetailCustomerGroupId\n" +
                ",PromotionDetailId\n" +
                ",PromotionDetailCustomerId\n" +
                ",DCRef\n" +
                ",CalcPriority\n" +
                ",PayType\n" +
                ",MinWeight\n" +
                ",MaxWeight\n" +
                ",PrizeStep\n" +
                ",CalcMethod\n" +
                ",DisPrice\n" +
                ",TotalDiscount)\n" +
                "SELECT UniqueId\n" +
                ",DisGroup \n" +
                ",PromotionId \n" +
                ",Priority \n" +
                ",StartDate \n" +
                ",EndDate \n" +
                ",Comment \n" +
                ",MinQty \n" +
                ",MaxQty \n" +
                ",MinAmount \n" +
                ",MaxAmount \n" +
                ",MinRowCount \n" +
                ",MaxRowCount \n" +
                ",PrizeQty \n" +
                ",AddPerc \n" +
                ",DiscountPerc \n" +
                ",IsPrize \n" +
                ",PrizeProductId \n" +
                ",PromotionTypeId  \n" +
                ",ManufacturerId \n" +
                ",ProductSubGroup1Id \n" +
                ",ProductSubGroup2Id \n" +
                ",CustomerSubGroup1Id \n" +
                ",CustomerSubGroup2Id \n" +
                ",ReduceOfQty \n" +
                ",ProductId \n" +
                ",DetailIsActive \n" +
                ",DetailPriority \n" +
                ",ProductGroupId \n" +
                ",CustomerId \n" +
                ",CustomerGroupId \n" +
                ",PromotionDetailCustomerGroupId \n" +
                ",PromotionDetailId \n" +
                ",PromotionDetailCustomerId \n" +
                ",CenterId \n" +
                ",DetailPriority \n" +
                ",PayTypeId \n" +
                ",MinWeight \n" +
                ",MaxWeight \n" +
                ",PrizeStep \n" +
                ",PromotionCalcBaseId \n" +
                ",DiscountAmount \n" +
                ",TotalDiscount \n" +
                "FROM DiscountVnLt\n";
    }

    @Override
    public String getFillDiscountConditionQuery() {
        return "";
    }

    @Override
    public String getFillDiscountItemQuery() {
        return "";
    }

    @Override
    public String getFillDiscountDisSaleQuery() {
        return "";
    }

    @Override
    public String getFillDiscountDisSaleVnLtQuery() {
        return "INSERT INTO DiscountDisSaleVnLt " +
                " SELECT DiscountAmount\n" +
                ",AddAmount\n" +
                ",PrizeAmount\n" +
                ",PrizeQty\n" +
                ",SellDetailPromotionDetailId\n" +
                ",SellDetailId\n" +
                ",SellId\n " +
                ",PromotionId\n " +
                ",PromotionDetailId\n " +
                " FROM CustomerOldInvoiceDisSaleVnLt ";
    }

    @Override
    public String getFillCPriceQuery() {
        return  "INSERT INTO DiscountContractPriceVnLite\n" +
                "SELECT UniqueId\n" +
                ",ProductRef\n" +
                ",CustomerRef\n" +
                ",CustomerGroupRef\n" +
                ",StartDate\n" +
                ",EndDate\n" +
                ",SellPrice\n" +
                ",UserPrice\n" +
                ",Comment\n" +
                ",ModifiedDate\n" +
                ",AppUserRef\n" +
                ",BatchRef\n" +
                ",PriceClassRef\n" +
                ",CustomerSubGroup1Ref\n" +
                ",CustomerSubGroup2Ref\n" +
                ",CenterRef\n" +
                ",TargetCenterRef\n" +
                ",BackOfficeId\n" +
                "FROM ContractPriceVnLite";
    }

    @Override
    public String getFillPriceHistory() {
        return "";
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
    public String getFillDisAccQuery() {
        return "";
    }

    @Override
    public String getFillOrderPrizeQuery(int orderId) {
        return "";
    }

    @Override
    public String getFillCustomerBoGroupQuery() {
        return "";
    }

    @Override
    public String getFillDisSalePrizePackageSDSQuery() {
        return "";
    }

    @Override
    public String getFillOrderTypeQuery() {
        return null;
    }

    @Override
    public String getFillDisTypeQuery() {
        return null;
    }


    @Override
    public String getFillProductTaxInfoQuery() {
        return "INSERT INTO DiscountProductTaxInfo \n" +
                "SELECT ProductRef\n" +
                ",TaxRate\n" +
                ",ChargeRate\n" +
                "FROM ProductTaxInfo";
    }

}


