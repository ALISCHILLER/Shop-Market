package com.varanegar.vaslibrary.promotion.V3;

import android.content.Context;
import android.os.Environment;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.base.account.AccountManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.OwnerKeysWrapper;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;

import java.io.File;

import varanegar.com.discountcalculatorlib.handler.IDiscountInitializeHandlerV3;
import varanegar.com.discountcalculatorlib.utility.enumerations.ApplicationType;
import varanegar.com.discountcalculatorlib.viewmodel.OwnerKeysViewModel;

/**
 * Created by A.Razavi on 11/14/2017.
 */

public abstract class DiscountInitializeHandler implements IDiscountInitializeHandlerV3 {
    SysConfigManager sysConfigManager;
    private Context _context;

    public DiscountInitializeHandler(Context context) {
        _context = context;
        sysConfigManager = new SysConfigManager(_context);
    }

    @Override
    public boolean getCalcOnLine() {
        SysConfigModel serverAddress = sysConfigManager.read(ConfigKey.OnliveEvc, SysConfigManager.cloud);
        return SysConfigManager.compare(serverAddress, true);
    }

    @Override
    public String getServerUrl() {
        SysConfigModel serverAddress = sysConfigManager.read(ConfigKey.BaseAddress, SysConfigManager.cloud);
        if (serverAddress == null) return "";
        return serverAddress.Value;
    }

    @Override
    public OwnerKeysViewModel getOwnerKeys() {
        OwnerKeysViewModel key = new OwnerKeysViewModel();
        OwnerKeysWrapper ownerKeys = sysConfigManager.readOwnerKeys();
        key.Token = new AccountManager().readFromFile(getContext(), "user.token").accessToken;
        key.DataOwnerCenterKey = ownerKeys.DataOwnerCenterKey;
        key.DataOwnerKey = ownerKeys.DataOwnerKey;
        key.OwnerKey = ownerKeys.OwnerKey;
        return key;
    }


    @Override
    public Context getContext() {
        return _context;
    }

    @Override
    public String getMainDbAttachQuery() {
        String dbPath = VaranegarApplication.getInstance().getDbHandler().getDatabasePath();
        return "ATTACH DATABASE '" + dbPath + "' as VNDB;";
    }

    @Override
    public String getDiscountDbPath() {

//            File dbpath = new File(Environment.getExternalStorageDirectory(), "/Android/data/discount/");
//            if (!dbpath.exists()) {
//                dbpath.mkdirs();
//            }
//            return  dbpath.getPath();

        return VaranegarApplication.getInstance().getDbHandler().getDatabaseFolder() + "/";
    }

    @Override
    public ApplicationType getApplicationType() {
        if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales))
            return ApplicationType.PRESALE;
        else if (VaranegarApplication.is(VaranegarApplication.AppId.HotSales))
            return ApplicationType.HOTSALE;
        else
            return ApplicationType.DISTRIBUTION;
    }

    @Override
    public int getDigtsAfterDecimalDigits() {
        SysConfigModel config = sysConfigManager.read(ConfigKey.DigtsAfterDecimalDigits, SysConfigManager.cloud);
        return SysConfigManager.getIntValue(config, -1);
    }

    @Override
    public int getDigtsAfterDecimalForCPrice() {
        SysConfigModel config = sysConfigManager.read(ConfigKey.DigtsAfterDecimalForCPrice, SysConfigManager.cloud);
        return SysConfigManager.getIntValue(config, -1);
    }

    @Override
    public boolean getPrizeCalcType() {

        SysConfigModel config = sysConfigManager.read(ConfigKey.PrizeCalcType, SysConfigManager.cloud);
        return SysConfigManager.compare(config, "1");
    }

    @Override
    public int getDcRef() {

        SysConfigModel config = sysConfigManager.read(ConfigKey.DcRef, SysConfigManager.cloud);
        return SysConfigManager.getIntValue(config, -1);
    }

    @Override
    public int getRoundType() {
        SysConfigModel config = sysConfigManager.read(ConfigKey.RoundType, SysConfigManager.cloud);
        return SysConfigManager.getIntValue(config, -1);
    }

    @Override
    public abstract int getBackOffice();

    @Override
    public abstract int getBackOfficeVersion();

    @Override
    public abstract String getFillDiscountConditionQuery();

    @Override
    public abstract String getFillDiscountItemQuery();

    @Override
    public abstract String getFillDiscountDisSaleQuery();

    @Override
    public abstract String getFillCPriceQuery();

    @Override
    public abstract String getFillPriceHistory();

    @Override
    public abstract String getFillCustomerQuery();

    @Override
    public String getFillCustomerSellPayTypeQuery() {
        return "INSERT INTO DiscountSellPayType  \n" +
                "SELECT 0 CustomerId,  \n" +
                "PaymentTypeOrder.backofficeId SellPayTypeId,  \n" +
                "'' SellPayTypeName,  \n" +
                "PaymentTypeOrder.GroupBackOfficeId BuyTypeId,  \n" +
                "0 CheckDebit,  \n" +
                "0 CheckCredit,  \n" +
                "0 PaymentDeadLine,  \n" +
                "0 PaymentTime," +
                "'' AS PayTypeName  \n" +
                "FROM PaymentTypeOrder ";
    }

    @Override
    public String getFillCustomerMainSubTypeQuery() {
        return " INSERT INTO  DiscountCustomerMainSubType  \n" +
                "SELECT ifnull(Id,0) as Id,\n" +
                " CustRef,\n" +
                " MainTypeRef,\n" +
                " SubTypeRef\n" +
                " FROM CustomerMainSubType";
    }

    @Override
    public String getFillCustomerOldInvoiceHeaderQuery() {
        return "INSERT INTO DiscountCustomerOldInvoiceHeader \n" +
                "SELECT Customer.BackofficeId CustomerId \n" +
                ",DealerRef as DealerId \n" +
                ",StockBackOfficeId as StockId \n" +
                ",SaleRef SaleId \n" +
                ",SaleNo \n" +
                ",SalePDate as SaleDate \n" +
                ",SaleVocherNo \n" +
                ",'' as DistributerName \n" +
                ",GoodsGroupTreeXML \n" +
                ",GoodsDetailXML \n" +
                ",GoodsMainSubTypeDetailXML \n" +
                ",CustCtgrRef \n" +
                ",CustActRef \n" +
                ",CustLevelRef \n" +
                ",hdr.SaleOfficeRef \n" +
                ",Dis1Amount as Dis1 \n" +
                ",Dis2Amount as Dis2 \n" +
                ",Dis3Amount as Dis3 \n" +
                ",Add1Amount as Add1 \n" +
                ",Add2Amount as Add2 \n" +
                ",OrderType \n" +
                ",BuyTypeRef \n" +
                ",hdr.DCRef \n" +
                ",hdr.DisType \n" +
                ",hdr.AccYear \n" +
                ",hdr.DCSaleOfficeRef \n" +
                ",hdr.ChargeAmount as Charge \n" +
                ",TaxAmount as Tax \n" +
                ",paymentUsanceRef \n" +
                ",StockDCCode \n" +
                ",DealerCode \n" +
                ",SupervisorCode \n" +
                ",hdr.OrderId \n" +
                ",hdr.OrderNo \n" +
                "FROM CustomerOldInvoiceHeader hdr \n" +
                "JOIN Customer ON CustomerId = Customer.UniqueId";
    }

    @Override
    public String getFillCustomerOldInvoiceDetailQuery() {
        return "INSERT INTO DiscountCustomerOldInvoiceDetail \n" +
                "(SaleId,\n" +
                "ProductId,\n" +
                "UnitCapasity,\n" +
                "UnitRef,\n" +
                "UnitQty,\n" +
                "TotalQty,\n" +
                "PreviousRetQty,\n" +
                "UnitName,\n" +
                "UnitPrice,\n" +
                "PriceId,\n" +
                "CPriceRef,\n" +
                "Amount,\n" +
                "AmountNut,\n" +
                "Discount,\n" +
                "PrizeType,\n" +
                "SupAmount,\n" +
                "AddAmount,\n" +
                "CustPrice,\n" +
                "UserPrice,\n" +
                "Charge,\n" +
                "tax,\n" +
                "RowOrder,\n" +
                "FreeReasonId,\n" +
                "IsDeleted,\n" +
                "ProductCtgrId,\n" +
                "Dis1, Dis2, Dis3, OtherDiscount, Add1,Add2,OtherAddition\n) \n" +

                "SELECT SaleRef AS SaleId\n" +
                ",Product.BackofficeId as ProductId  \n" +
                ",dtl.UnitCapasity  \n" +
                ",dtl.UnitRef  \n" +
                ",dtl.UnitQty  \n" +
                ",dtl.TotalQty  \n" +
                ",dtl.PreviousRetQty  \n" +
                ",dtl.UnitName  \n" +
                ",dtl.UnitPrice  \n" +
                ",dtl.PriceId  \n" +
                ",dtl.CPriceRef  \n" +
                ",IFNULL(dtl.Amount, dtl.TotalQty * dtl.UnitPrice)  \n" +
                ",IFNULL(dtl.AmountNut , (dtl.TotalQty * dtl.UnitPrice) + " +
                "   IFNULL(dtl.AddAmount,0) + IFNULL(dtl.Charge, 0) + IFNULL(tax,0) - IFNULL(dtl.Discount , 0) ) \n" +
                ",dtl.Discount  \n" +
                ",dtl.PrizeType  \n" +
                ",dtl.SupAmount  \n" +
                ",dtl.AddAmount  \n" +
                ",UnitPrice as CustPrice  \n" +
                ",dtl.UserPrice  \n" +
                ",dtl.Charge  \n" +
                ",dtl.tax  \n" +
                ",dtl.RowOrder  \n" +
                ",FreeReasonId \n" +
                ",IsDeleted \n" +
                ",dtl.ProductCtgrId \n" +
                ",dtl.Dis1, dtl.Dis2, dtl.Dis3, dtl.OtherDiscount \n" +
                ",dtl.Add1, dtl.Add2, dtl.OtherAddition \n" +
                "FROM CustomerOldInvoiceDetail dtl \n" +
                "JOIN CustomerOldInvoiceHeader hdr ON hdr.UniqueId = dtl.SaleId \n" +
                "JOIN Product ON Product.UniqueId = dtl.ProductId" ;
    }

    @Override
    public String getFillDiscountQuery() {
        return "INSERT INTO DiscountSDS4_19 (" +
                " UniqueId" +
                ",goodsRef \n" +
                ",ID \n" +
                ",DisGroup \n" +
                ",Priority \n" +
                ",Code \n" +
                ",DisType \n" +
                ",PrizeType \n" +
                ",StartDate \n" +
                ",EndDate \n" +
                ",MinQty \n" +
                ",MaxQty \n" +
                ",QtyUnit \n" +
                ",MinAmount \n" +
                ",MaxAmount \n" +
                ",PrizeQty \n" +
                ",PrizeRef \n" +
                ",PrizeStep \n" +
                ",PrizeUnit \n" +
                ",DisPerc \n" +
                ",DisPrice \n" +
                ",GoodsCtgrRef \n" +
                ",DisAccRef \n" +
                ",SupPerc \n" +
                ",AddPerc \n" +
                ",Comment \n" +
                ",ApplyInGroup \n" +
                ",CalcPriority \n" +
                ",CalcMethod \n" +
                ",GoodsGroupRef \n" +
                ",ManufacturerRef \n" +
                ",MainTypeRef \n" +
                ",SubTypeRef \n" +
                ",BrandRef \n" +
                ",MinWeight \n" +
                ",MaxWeight \n" +
                ",UserRef \n" +
                ",ModifiedDate \n" +
                ",PrizeIncluded \n" +
                ",ModifiedDateBeforeSend \n" +
                ",UserRefBeforeSend \n" +
                ",MinRowsCount \n" +
                ",MinCustChequeRetQty \n" +
                ",MaxCustChequeRetQty \n" +
                ",MinCustRemAmount \n" +
                ",MaxCustRemAmount \n" +
                ",MaxRowsCount \n" +
                ",IsActive \n" +
                ",IsPrize \n" +
                ",CustomerSubGroup1Id \n" +
                ",CustomerSubGroup2Id \n" +
                ",ProductSubGroup1Id \n" +
                ",ProductSubGroup2Id \n" +
                ",DetailIsActive \n" +
                ",DetailPriority \n" +
                ",PromotionDetailCustomerGroupId \n" +
                ",PromotionDetailId \n" +
                ",PromotionDetailCustomerId \n" +
                ",ReduceOfQty \n" +
                ",HasAdvanceCondition \n" +
                ",SqlCondition \n" +
                ",PrizeStepType \n" +
                ",PrizePackageRef \n" +
                ",PrizeStepUnit \n" +
                ",TotalMinAmount \n" +
                ",TotalMaxAmount \n" +
                ",TotalMinRowCount \n" +
                ",TotalMaxRowCount \n" +
                ",MixCondition \n" +
                ",CustRefList \n" +
                ",DiscountAreaRefList \n" +
                ",DiscountCustActRefList \n" +
                ",DiscountCustCtgrRefList \n" +
                ",DiscountCustGroupRefList \n" +
                ",DiscountCustLevelRefList \n" +
                ",DiscountDcRefList \n" +
                ",DiscountGoodRefList \n" +
                ",DiscountMainCustTypeRefList \n" +
                ",DiscountOrderNoList \n" +
                ",DiscountOrderRefList \n" +
                ",DiscountOrderTypeList \n" +
                ",DiscountPaymentUsanceRefList \n" +
                ",DiscountPayTypeList \n" +
                ",DiscountSaleOfficeRefList \n" +
                ",DiscountSaleZoneRefList \n" +
                ",DiscountStateRefList \n" +
                ",DiscountSubCustTypeRefList \n" +
                ",IsComplexCondition \n" +
                ",PrizeSelectionList \n" +
                ",IsSelfPrize \n"  +
                ") \n" +
                "SELECT" +
                " UniqueId" +
                " ,goodsRef \n" +
                ",BackofficeId ID \n" +
                ",DisGroup \n" +
                ",Priority \n" +
                ",Code \n" +
                ",DisType \n" +
                ",PrizeType \n" +
                ",StartDate \n" +
                ",EndDate \n" +
                ",MinQty \n" +
                ",MaxQty \n" +
                ",QtyUnit \n" +
                ",MinAmount \n" +
                ",MaxAmount \n" +
                ",PrizeQty \n" +
                ",PrizeRef \n" +
                ",PrizeStep \n" +
                ",PrizeUnit \n" +
                ",DisPerc \n" +
                ",DisPrice \n" +
                ",GoodsCtgrRef \n" +
                ",DisAccRef \n" +
                ",SupPerc \n" +
                ",AddPerc \n" +
                ",Comment \n" +
                ",ApplyInGroup \n" +
                ",CalcPriority \n" +
                ",CalcMethod \n" +
                ",GoodsGroupRef \n" +
                ",ManufacturerRef \n" +
                ",MainTypeRef \n" +
                ",SubTypeRef \n" +
                ",BrandRef \n" +
                ",MinWeight \n" +
                ",MaxWeight \n" +
                ",UserRef \n" +
                ",ModifiedDate \n" +
                ",PrizeIncluded \n" +
                ",ModifiedDateBeforeSend \n" +
                ",UserRefBeforeSend \n" +
                ",MinRowsCount \n" +
                ",MinCustChequeRetQty \n" +
                ",MaxCustChequeRetQty \n" +
                ",MinCustRemAmount \n" +
                ",MaxCustRemAmount \n" +
                ",MaxRowsCount \n" +
                ",IsActive \n" +
                ",IsPrize \n" +
                ",0 as CustomerSubGroup1Id \n" +
                ",0 as CustomerSubGroup2Id \n" +
                ",0 as ProductSubGroup1Id \n" +
                ",0 as ProductSubGroup2Id \n" +
                ",0 as DetailIsActive \n" +
                ",DetailPriority \n" +
                ",PromotionDetailCustomerGroupId \n" +
                ",PromotionDetailId \n" +
                ",PromotionDetailCustomerId \n" +
                ",ReduceOfQty \n" +
                ",HasAdvanceCondition \n" +
                ",SqlCondition \n" +
                ",PrizeStepType \n" +
                ",PrizePackageRef \n" +
                ",PrizeStepUnit \n" +
                ",TotalMinAmount \n" +
                ",TotalMaxAmount \n" +
                ",TotalMinRowCount \n" +
                ",TotalMaxRowCount \n" +
                ",MixCondition \n" +
                ",CustRefList \n" +
                ",DiscountAreaRefList \n" +
                ",DiscountCustActRefList \n" +
                ",DiscountCustCtgrRefList \n" +
                ",DiscountCustGroupRefList \n" +
                ",DiscountCustLevelRefList \n" +
                ",DiscountDcRefList \n" +
                ",DiscountGoodRefList \n" +
                ",DiscountMainCustTypeRefList \n" +
                ",DiscountOrderNoList \n" +
                ",DiscountOrderRefList \n" +
                ",DiscountOrderTypeList \n" +
                ",DiscountPaymentUsanceRefList \n" +
                ",DiscountPayTypeList \n" +
                ",DiscountSaleOfficeRefList \n" +
                ",DiscountSaleZoneRefList \n" +
                ",DiscountStateRefList \n" +
                ",DiscountSubCustTypeRefList \n" +
                ",IsComplexCondition \n" +
                ",PrizeSelectionList \n" +
                ",IsSelfPrize \n" +
                " FROM DiscountSDS;";
    }

    @Override
    public String getFillProductQuery() {
        return "INSERT INTO DiscountProduct (" +
                " ProductId,    \n" +
                " ProductName,\n" +
                " ProductCode,   \n" +
                " ProductSubGroupId,   \n" +
                " SmallUnitId,   \n" +
                " SmallUnitName, \n" +
                " SmallUnitQty,  \n" +
                " LargeUnitId,   \n" +
                " LargeUnitName, \n" +
                " LargeUnitQty,  \n" +
                " Description,   \n" +
                " ProductTypeId, \n" +
                " PackUnitRef,   \n" +
                " UnitRef,       \n" +
                " ManufacturerId, \n" +
                " ProductBoGroupId,    \n" +
                " BrandId,    \n" +
                " ProductCtgrId,    \n" +
                " Tax,    \n" +
                " Charge,    \n" +
                " CartonType,    \n" +
                " ProductWeight,    \n" +
                " IsForSale,   \n" +
                " IsForReturn, \n" +
                " HasBatch,    \n" +
                " ProductSubGroup1id, \n" +
                " ProductSubGroup2id, \n" +
                " CartonPrizeQty,    \n" +
                " GoodsVolume,    \n" +
                " ManufacturerCode,    \n" +
                " ShipTypeId,    \n" +
                " HasImage,    \n" +
                " CanBeFree,    \n" +
                " ManufacturerName,\n" +
                " BrandName,\n" +
                " ShipTypeName\n"+
                ") \n" +
                "SELECT Product.BackOfficeId ProductId,    \n" +
                " ProductName,\n" +
                " ProductCode,   \n" +
                " 0 as ProductSubGroupId,   \n" +
                " SmallUnitId,   \n" +
                " SmallUnitName, \n" +
                " SmallUnitQty,  \n" +
                " LargeUnitId,   \n" +
                " LargeUnitName, \n" +
                " LargeUnitQty,  \n" +
                " Description,   \n" +
                " ProductTypeId, \n" +
                " PackUnitRef,   \n" +
                " UnitRef,       \n" +
                " ManufacturerRef ManufacturerId, \n" +
                " ProductBoGroup.BackOfficeId AS ProductBoGroupId,    \n" +
                " BrandRef as BrandId,    \n" +
                " ProductCtgrRef as ProductCtgrId,    \n" +
                " TaxPercent as Tax,    \n" +
                " ChargePercent as Charge,    \n" +
                " CartonType as CartonType,    \n" +
                " ProductWeight, \n" +
                " 1 as IsForSale,   \n" +
                " 1 as IsForReturn, \n" +
                " 0 as HasBatch,    \n" +
                " ProductSubGroup1IdVnLite as ProductSubGroup1id,    \n" +
                " ProductSubGroup2IdVnLite as ProductSubGroup2id,    \n" +
                " CartonPrizeQty,    \n" +
                " GoodsVolume,    \n" +
                " ManufacturerCode,    \n" +
                " 0 as ShipTypeId,    \n" +
                " 0 as HasImage,    \n" +
                " 0 as CanBeFree,    \n" +
                "'' as ManufacturerName,\n" +
                "'' as BrandName,\n" +
                "'' as ShipTypeName\n"+
                " FROM Product     \n" +
                " LEFT JOIN ProductBoGroup ON ProductBoGroup.UniqueId = ProductBoGroupId \n" +
                " JOIN    \n" +
                " ( SELECT Unit.BackOfficeId SmallUnitId,   \n" +
                "     Unit.UnitName SmallUnitName,   \n" +
                "     ProductUnit.ConvertFactor SmallUnitQty,  \n" +
                "     ProductUnit.ProductId\n" +
                " FROM ProductUnit JOIN Unit ON ProductUnit.UnitId = Unit.UniqueId  \n" +
                " WHERE UnitStatusId = '1f68ee36-13d7-4a3c-a082-fe6014ac77cc' ) AS SmallUnit ON SmallUnit.ProductId = Product.UniqueId\n" +
                " LEFT JOIN\n" +
                " (SELECT Unit.BackOfficeId LargeUnitId,   \n" +
                "    Unit.UnitName LargeUnitName,  \n" +
                "    ProductUnit.ConvertFactor LargeUnitQty,\n" +
                "    ProductUnit.ProductId\n" +
                " FROM ProductUnit JOIN Unit ON ProductUnit.UnitId = Unit.UniqueId  \n" +
                " WHERE UnitStatusId = 'c3c4fcb6-10df-4841-ba9b-6782fa6fbe93' ) AS LargeUnit ON LargeUnit.ProductId = Product.UniqueId\n";
    }

    @Override
    public String getFillProductUnitQuery() {
        return "INSERT INTO DiscountProductUnit(" +
                "ProductUnitId,ProductUnitName,ProductId,Quantity," +
                "IsDefault,ForSale,BackOfficeId,Status,ForRetSale" +
                ") \n" +
                "SELECT Unit.BackOfficeId AS ProductUnitId\n" +
                ",Unit.UnitName AS ProductUnitName \n" +
                ",Product.BackofficeId ProductId \n" +
                ",ConvertFactor Quantity \n" +
                ",IsDefault \n" +
                ",ProductUnit.IsForSale AS ForSale \n" +
                ",Unit.BackOfficeId as BackOfficeId \n" +
                ",CASE  UnitStatusId WHEN '1f68ee36-13d7-4a3c-a082-fe6014ac77cc' THEN 1 ELSE 0 END   as Status \n" +
                ",ProductUnit.IsForReturn AS ForRetSale \n" +
                "FROM ProductUnit JOIN Unit ON ProductUnit.UnitId =  Unit.UniqueId \n" +
                "JOIN Product ON ProductUnit.ProductId = Product.UniqueId";
    }

    @Override
    public String getFillProductBoGroupQuery() {
        return "INSERT INTO DiscountProductBoGroup \n" +
                "SELECT BackOfficeId ID\n" +
                ",ParentRef\n" +
                ",GoodsGroupName\n" +
                ",BarCode\n" +
                ",DLCode\n" +
                ",NLeft\n" +
                ",NRight\n" +
                ",NLevel\n" +
                " FROM ProductBoGroup ";
    }

    @Override
    public String getFillProductMainSubTypeQuery() {
        return "INSERT INTO DiscountProductMainSubType  \n" +
                "SELECT \n" +
                " BackofficeId Id, \n" +
                " GoodsRef, \n" +
                " MainTypeRef, \n" +
                " SubTypeRef, \n" +
                " UniqueId \n" +
                "FROM ProductMainSubType;";
    }

    @Override
    public String getFillProductPackageQuery() {
        return "INSERT INTO DiscountGoodsPackage \n" +
                "SELECT BackofficeId as ID,\n" +
                "DiscountRef\n" +
                " FROM GoodsPackage";
    }

    @Override
    public String getFillProductPackageItemQuery() {
        return "INSERT INTO DiscountGoodsPackageItem \n" +
                "SELECT BackofficeId  AS ID\n" +
                ",GoodsPackageRef\n" +
                ",GoodsRef\n" +
                ",UnitQty\n" +
                ",UnitRef\n" +
                ",TotalQty\n" +
                ",ReplaceGoodsRef\n" +
                ",PrizePriority\n" +
                ",DiscountRef\n" +
                "FROM GoodsPackageItem ";
    }

    @Override
    public String getFillDisAccQuery() {
        return "INSERT INTO DiscountDisAcc\n" +
                "(id, IsDiscount,\n" +
                "UniqueId,\n" +
                "Code,\n" +
                "BackOfficeId,\n" +
                "Name,\n" +
                "IsDefault) \n" +
                "SELECT Id,\n" +
                "IsDiscount,\n" +
                "UniqueId,\n" +
                "Code,\n" +
                "BackOfficeId,\n" +
                "Name,\n" +
                "IsDefault \n" +
                "FROM DisAcc ";
    }

    @Override
    public String getFillTourProductQuery() {
        return "INSERT INTO DiscountTourProduct \n" +
                "(UniqueId,\n" +
                "ProductId,\n" +
                "OnHandQty,\n" +
                "RenewQty,\n" +
                "StockDcRef)\n" +
                "SELECT UniqueId,\n" +
                "ProductId,\n" +
                "OnHandQty,\n" +
                "RenewQty,\n" +
                "StockDcRef\n" +
                " FROM OnHandQty ";
    }

    @Override
    public abstract String getFillProductTaxInfoQuery();

    @Override
    public String getFillFreeReasonQuery() {
        return "INSERT INTO DiscountFreeReason \n" +
                "SELECT BackOfficeId\n" +
                ",FreeReasonName\n" +
                ",CalcPriceType\n" +
                ",DisAccTypeid\n" +
                "FROM FreeReason";
    }

    @Override
    public abstract String getFillDisSalePrizePackageSDSQuery();


    @Override
    public String getFillDiscountGoodQuery() {
        return "INSERT INTO DiscountDiscountGood \n" +
                "(uniqueId,\n" +
                "BackOfficeId,\n" +
                "DiscountRef,\n" +
                "GoodsRef)\n" +
                "SELECT uniqueId,\n" +
                "BackOfficeId,\n" +
                "DiscountRef,\n" +
                "GoodsRef\n" +
                " FROM DiscountGood ";
    }

    @Override
    public String getFillGoodsNosaleQuery() {
        return "INSERT INTO DiscountGoodsNosale \n" +
                "(uniqueId,\n" +
                "BackOfficeId,\n" +
                "GoodsRef,\n" +
                "Status,\n" +
                "StartDate,\n" +
                "EndDate,\n" +
                "CustRef,\n" +
                "DCRef,\n" +
                "CustActRef,\n" +
                "CustCtgrRef,\n" +
                "CustLevelRef,\n" +
                "StateRef,\n" +
                "AreaRef,\n" +
                "CountyRef)\n" +
                "SELECT uniqueId,\n" +
                "BackOfficeId,\n" +
                "GoodsRef,\n" +
                "Status,\n" +
                "StartDate,\n" +
                "EndDate,\n" +
                "CustRef,\n" +
                "DCRef,\n" +
                "CustActRef,\n" +
                "CustCtgrRef,\n" +
                "CustLevelRef,\n" +
                "StateRef,\n" +
                "AreaRef,\n" +
                "CountyRef\n" +
                " FROM GoodsNosale ";
    }

    @Override
    public String getFillGoodsFixUnitQuery() {
        return "INSERT INTO DiscountGoodsFixUnit \n" +
                "(uniqueId,\n" +
                "GoodsRef,\n" +
                "UnitRef,\n" +
                "UnitName,\n" +
                "Qty)\n" +
                "SELECT uniqueId,\n" +
                "GoodsRef,\n" +
                "UnitRef,\n" +
                "UnitName,\n" +
                "Qty\n" +
                " FROM GoodsFixUnit ";
    }

    @Override
    public String getFillPaymentUsancesQuery() {
        return "INSERT INTO DiscountPaymentUsances \n" +
                "(uniqueId,\n" +
                "BackOfficeId,\n" +
                "Title,\n" +
                "BuyTypeId,\n" +
                "DeferTo,\n" +
                "ClearTo,\n" +
                "Status,\n" +
                "IsCash,\n" +
                "ModifiedDateBeforeSend,\n" +
                "UserRefBeforeSend,\n" +
                "AdvanceControl)\n" +
                "SELECT uniqueId,\n" +
                "BackOfficeId,\n" +
                "Title,\n" +
                "BuyTypeId,\n" +
                "DeferTo,\n" +
                "ClearTo,\n" +
                "Status,\n" +
                "IsCash,\n" +
                "ModifiedDateBeforeSend,\n" +
                "UserRefBeforeSend,\n" +
                "AdvanceControl\n" +
                " FROM PaymentUsances ";
    }

    @Override
    public String getFillRetSaleHdrQuery() {
        return "INSERT INTO DiscountRetSaleHdr \n" +
                "(uniqueId,\n" +
                "BackOfficeId,\n" +
                "RetSaleNo,\n" +
                "RetSaleDate,\n" +
                "SaleDate,\n" +
                "SaleRef,\n" +
                "VocherFlag,\n" +
                "HealthCode,\n" +
                "RetTypeCode,\n" +
                "RetCauseRef,\n" +
                "Dis1,\n" +
                "Dis2,\n" +
                "Dis3,\n" +
                "Add1,\n" +
                "Add2,\n" +
                "CancelFlag,\n" +
                "TotalAmount,\n" +
                "UserRef,\n" +
                "ChangeDate,\n" +
                "BuyType,\n" +
                "AccYear,\n" +
                "DCRef,\n" +
                "StockDCRef,\n" +
                "CustRef,\n" +
                "DealerRef,\n" +
                "DCSaleOfficeRef,\n" +
                "RetOrderRef,\n" +
                "DistRef,\n" +
                "TSaleRef,\n" +
                "Comment,\n" +
                "Tax,\n" +
                "Charge,\n" +
                "SupervisorRef,\n" +
                "OtherDiscount,\n" +
                "OtherAddition)\n" +
                "SELECT uniqueId,\n" +
                "BackOfficeId,\n" +
                "RetSaleNo,\n" +
                "RetSaleDate,\n" +
                "SaleDate,\n" +
                "SaleRef,\n" +
                "VocherFlag,\n" +
                "HealthCode,\n" +
                "RetTypeCode,\n" +
                "RetCauseRef,\n" +
                "Dis1,\n" +
                "Dis2,\n" +
                "Dis3,\n" +
                "Add1,\n" +
                "Add2,\n" +
                "CancelFlag,\n" +
                "TotalAmount,\n" +
                "UserRef,\n" +
                "ChangeDate,\n" +
                "BuyType,\n" +
                "AccYear,\n" +
                "DCRef,\n" +
                "StockDCRef,\n" +
                "CustRef,\n" +
                "DealerRef,\n" +
                "DCSaleOfficeRef,\n" +
                "RetOrderRef,\n" +
                "DistRef,\n" +
                "TSaleRef,\n" +
                "Comment,\n" +
                "Tax,\n" +
                "Charge,\n" +
                "SupervisorRef,\n" +
                "OtherDiscount,\n" +
                "OtherAddition\n" +
                " FROM RetSaleHdr ";
    }

    @Override
    public String getFillRetSaleItemQuery() {
        return "INSERT INTO DiscountRetSaleItem \n" +
                "(uniqueId,\n" +
                "BackOfficeId,\n" +
                "HdrRef,\n" +
                "RowOrder,\n" +
                "RetCauseRef,\n" +
                "GoodsRef,\n" +
                "UnitRef,\n" +
                "UnitCapasity,\n" +
                "UnitQty,\n" +
                "TotalQty,\n" +
                "UnitPrice,\n" +
                "AmountNut,\n" +
                "Discount,\n" +
                "Amount,\n" +
                "AccYear,\n" +
                "PrizeType,\n" +
                "SaleRef,\n" +
                "SupAmount,\n" +
                "AddAmount,\n" +
                "Dis1,\n" +
                "Dis2,\n" +
                "Dis3,\n" +
                "Add1,\n" +
                "Add2,\n" +
                "Tax,\n" +
                "Charge,\n" +
                "FreeReasonId,\n" +
                "OtherDiscount,\n" +
                "OtherAddition)\n" +
                "SELECT uniqueId,\n" +
                "BackOfficeId,\n" +
                "HdrRef,\n" +
                "RowOrder,\n" +
                "RetCauseRef,\n" +
                "GoodsRef,\n" +
                "UnitRef,\n" +
                "UnitCapasity,\n" +
                "UnitQty,\n" +
                "TotalQty,\n" +
                "UnitPrice,\n" +
                "AmountNut,\n" +
                "Discount,\n" +
                "Amount,\n" +
                "AccYear,\n" +
                "PrizeType,\n" +
                "SaleRef,\n" +
                "SupAmount,\n" +
                "AddAmount,\n" +
                "Dis1,\n" +
                "Dis2,\n" +
                "Dis3,\n" +
                "Add1,\n" +
                "Add2,\n" +
                "Tax,\n" +
                "Charge,\n" +
                "FreeReasonId,\n" +
                "OtherDiscount,\n" +
                "OtherAddition\n" +
                " FROM RetSaleItem ";
    }

    @Override
    public String getFillCustomerBoGroupQuery() {
        return "INSERT INTO DiscountCustomerBoGroup " +
                "SELECT Id,\n" +
                "ParentRef,\n" +
                "NLeft,\n" +
                "NRight,\n" +
                "NLevel\n" +
                "FROM CustomerBoGroup";
    }

    @Override
    public String getAppId() {
        return VaranegarApplication.getInstance().getAppId().toString();
    }

}


