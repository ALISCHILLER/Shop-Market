/*
Navicat SQLite Data Transfer

Source Server         : DiscountDbV3
Source Server Version : 31202
Source Host           : :0

Target Server Type    : SQLite
Target Server Version : 31202
File Encoding         : 65001

Date: 2018-02-26 13:32:50
*/

PRAGMA foreign_keys = OFF;

-- ----------------------------
-- Table structure for DiscountConditionSDS4_19
-- ----------------------------
DROP TABLE IF EXISTS "main"."DiscountConditionSDS4_19";
CREATE TABLE "DiscountConditionSDS4_19"([Id]  INTEGER  NULL ,[DiscountRef]  INTEGER  NULL ,[DCRef]  INTEGER  NULL ,[CustCtgrRef]  INTEGER  NULL ,[CustActRef]  INTEGER  NULL ,[CustLevelRef]  INTEGER  NULL ,[PayType]  INTEGER  NULL ,[PaymentUsanceRef]  INTEGER  NULL ,[OrderType]  INTEGER  NULL ,[SaleOfficeRef]  INTEGER  NULL ,[CustGroupRef]  INTEGER  NULL ,[CustRef]  INTEGER  NULL ,[OrderNo]  INTEGER  NULL ,[StateRef]  INTEGER  NULL ,[AreaRef]  INTEGER  NULL ,[SaleZoneRef]  INTEGER  NULL ,[MainCustTypeRef]  INTEGER  NULL ,[SubCustTypeRef]  INTEGER  NULL );

-- ----------------------------
-- Table structure for DiscountContractPriceSDS
-- ----------------------------
DROP TABLE IF EXISTS "main"."DiscountContractPriceSDS";
CREATE TABLE "DiscountContractPriceSDS" (
"ID"  INTEGER,
"CPriceType"  INTEGER,
"Code"  INTEGER,
"GoodsRef"  INTEGER,
"UnitRef"  INTEGER,
"CustRef"  INTEGER,
"CustCtgrRef"  INTEGER,
"DCRef"  INTEGER,
"CustActRef"  INTEGER,
"MinQty"  INTEGER,
"MaxQty"  INTEGER,
"SalePrice"  DECIMAL,
"StartDate"  NVARCHAR(20),
"EndDate"  NVARCHAR(20),
"BuyTypeRef"  INTEGER,
"UsanceDay"  INTEGER,
"StateRef"  INTEGER,
"CountyRef"  INTEGER,
"AreaRef"  INTEGER,
"Priority"  DECIMAL,
"OrderTypeRef"  INTEGER,
"GoodsGroupRef"  INTEGER,
"MainTypeRef"  INTEGER,
"SubTypeRef"  INTEGER,
"CustLevelRef"  INTEGER,
"UserRef"  INTEGER,
"ModifiedDate"  NVARCHAR(20),
"DealerCtgrRef"  INTEGER,
"ModifiedDateBeforeSend"  NVARCHAR(20),
"UserRefBeforeSend"  INTEGER,
"BatchNoRef"  INTEGER,
"BatchNo"  NVARCHAR(50),
"UniqueId"  NVARCHAR(50)
);

-- ----------------------------
-- Table structure for DiscountContractPriceVnLite
-- ----------------------------
DROP TABLE IF EXISTS "main"."DiscountContractPriceVnLite";
CREATE TABLE "DiscountContractPriceVnLite" (
"UniqueId"  TEXT NOT NULL,
"ProductRef"  INTEGER,
"CustomerRef"  INTEGER,
"CustomerGroupRef"  INTEGER,
"StartDate"  TEXT,
"EndDate"  TEXT,
"SellPrice"  REAL,
"UserPrice"  REAL,
"Comment"  TEXT,
"ModifiedDate"  TEXT,
"AppUserRef"  INTEGER,
"BatchRef"  INTEGER,
"PriceClassRef"  INTEGER,
"CustomerSubGroup1Ref"  INTEGER,
"CustomerSubGroup2Ref"  INTEGER,
"CenterRef"  INTEGER,
"TargetCenterRef"  INTEGER,
"BackOfficeId"  INTEGER,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for DiscountCustomer
-- ----------------------------
DROP TABLE IF EXISTS "main"."DiscountCustomer";
CREATE TABLE "DiscountCustomer"([CustomerId]  INTEGER  NULL ,[CustomerCode]  NVARCHAR(2048)  NULL ,[CustomerName]  NVARCHAR(2048)  NULL ,[Address]  NVARCHAR(2048)  NULL ,[Phone]  NVARCHAR(2048)  NULL ,[StoreName]  NVARCHAR(2048)  NULL ,[Mobile]  NVARCHAR(2048)  NULL ,[Longitude]  INTEGER  NULL ,[Latitude]  INTEGER  NULL ,[Alarm]  NVARCHAR(2048)  NULL ,[EconomicCode]  NVARCHAR(2048)  NULL ,[NationalCode]  NVARCHAR(2048)  NULL ,[totalorder]  INTEGER  NULL ,[visitstatus]  NVARCHAR(2048)  NULL ,[visitstatusreason]  INTEGER  NULL ,[salesmanid]  INTEGER  NULL ,[pasajid]  INTEGER  NULL ,[visitlat]  INTEGER  NULL ,[visitlng]  INTEGER  NULL ,[gpssave]  INTEGER  NULL ,[nosalereasonid]  INTEGER  NULL ,[ErrorType]  INTEGER  NULL ,[ErrorMessage]  INTEGER  NULL ,[NoSend]  INTEGER  NULL ,[SendDataStatus]  INTEGER  NULL ,[IsInfoSend]  INTEGER  NULL ,[IsInfoEdit]  INTEGER  NULL ,[SortId]  INTEGER  NULL ,[CityId]  INTEGER  NULL ,[StateId]  INTEGER  NULL ,[CustomerLevelId]  INTEGER  NULL ,[CustomerActivityId]  INTEGER  NULL ,[CustomerCategoryId]  INTEGER  NULL ,[CenterId]  INTEGER  NULL ,[ZoneId]  INTEGER  NULL ,[AreaId]  INTEGER  NULL ,[HasCancelOrder]  INTEGER  NULL ,[IsPrinted]  INTEGER  NULL ,[ActionGUID]  INTEGER  NULL ,[ActionType]  INTEGER  NULL ,[IsNewCustomer]  INTEGER  NULL ,[DCRef]  INTEGER  NULL ,[CountyId]  INTEGER  NULL ,[CustomerSubGroup1Id]  INTEGER  NULL ,[CustomerSubGroup2Id]  INTEGER  NULL ,[UniqueId]  NVARCHAR(2048)  NULL ,[DcCode]  NVARCHAR(2048)  NULL ,[CustomerCategoryCode]  NVARCHAR(2048)  NULL ,[CustomerActivityCode]  NVARCHAR(2048)  NULL ,[CustomerLevelCode]  NVARCHAR(2048)  NULL ,[CityZone]  INTEGER  NULL ,[CityArea]  INTEGER  NULL ,[OwnerTypeRef]  INTEGER  NULL ,[OwnerTypeCode]  NVARCHAR(2048)  NULL ,[SalePathNo]  INTEGER  NULL ,[SalePathRef]  INTEGER  NULL ,[SaleAreaRef]  INTEGER  NULL ,[SaleAreaNo]  NVARCHAR(2048)  NULL ,[SaleZoneRef]  INTEGER  NULL ,[SaleZoneNo]  INTEGER  NULL ,[CityCode]  NVARCHAR(2048)  NULL ,[CountyRef]  INTEGER  NULL ,[CountyCode]  NVARCHAR(2048)  NULL ,[StateCode]  NVARCHAR(2048)  NULL ,[DistPathId]  INTEGER  NULL ,[DistPathNo]  INTEGER  NULL ,[DistAreaId]  INTEGER  NULL ,[DistAreaNo]  INTEGER  NULL ,[DistZoneId]  INTEGER  NULL ,[DistZoneNo]  INTEGER  NULL ,[RemainDebit]  DECIMAL  NULL ,[RemainCredit]  DECIMAL  NULL ,[OpenChequeAmount]  DECIMAL  NULL ,[OpenChequeCount]  INTEGER  NULL ,[ReturnChequeCount]  INTEGER  NULL ,[ReturnChequeAmount]  DECIMAL  NULL ,[OpenInvoiceCount]  INTEGER  NULL ,[OpenInvoiceAmount]  DECIMAL  NULL ,[CustomerRemain]  DECIMAL  NULL ,[InitDebit]  DECIMAL  NULL ,[InitCredit]  DECIMAL  NULL );

-- ----------------------------
-- Table structure for DiscountCustomerMainSubType
-- ----------------------------
DROP TABLE IF EXISTS "main"."DiscountCustomerMainSubType";
CREATE TABLE "DiscountCustomerMainSubType" (
"Id"  INTEGER NOT NULL,
"CustRef"  INTEGER,
"MainTypeRef"  INTEGER,
"SubTypeRef"  INTEGER,
PRIMARY KEY ("Id" ASC)
);

-- ----------------------------
-- Table structure for DiscountCustomerOldInvoiceDetail
-- ----------------------------
DROP TABLE IF EXISTS "main"."DiscountCustomerOldInvoiceDetail";
CREATE TABLE "DiscountCustomerOldInvoiceDetail"([SaleId]  INTEGER  NULL ,[ProductId]  INTEGER  NULL ,[UnitCapasity]  INTEGER  NULL ,[UnitRef]  INTEGER  NULL ,[UnitQty]  INTEGER  NULL ,[TotalQty]  INTEGER  NULL ,[UnitName]  NVARCHAR(2048)  NULL ,[UnitPrice]  INTEGER  NULL ,[PriceId]  INTEGER  NULL ,[CPriceRef]  INTEGER  NULL ,[Amount]  DECIMAL  NULL ,[AmountNut]  DECIMAL  NULL ,[Discount]  DECIMAL  NULL ,[PrizeType]  INTEGER  NULL ,[SupAmount]  DECIMAL  NULL ,[AddAmount]  DECIMAL  NULL ,[CustPrice]  DECIMAL  NULL ,[UserPrice]  DECIMAL  NULL ,[Charge]  DECIMAL  NULL ,[tax]  DECIMAL  NULL ,[RowOrder]  DECIMAL  NULL ,[ProductCtgrId]  INTEGER  NULL );

-- ----------------------------
-- Table structure for DiscountCustomerOldInvoiceHeader
-- ----------------------------
DROP TABLE IF EXISTS "main"."DiscountCustomerOldInvoiceHeader";
CREATE TABLE "DiscountCustomerOldInvoiceHeader"([CustomerId]  INTEGER  NULL ,[DealerId]  INTEGER  NULL ,[StockId]  INTEGER  NULL ,[SaleId]  INTEGER  NULL ,[SaleNo]  INTEGER  NULL ,[SaleDate]  NVARCHAR(2048)  NULL ,[SaleVocherNo]  INTEGER  NULL ,[DistributerName]  NVARCHAR(2048)  NULL ,[GoodsGroupTreeXML]  NVARCHAR(2048)  NULL ,[GoodsDetailXML]  NVARCHAR(2048)  NULL ,[GoodsMainSubTypeDetailXML]  NVARCHAR(2048)  NULL ,[CustCtgrRef]  INTEGER  NULL ,[CustActRef]  INTEGER  NULL ,[CustLevelRef]  INTEGER  NULL ,[SaleOfficeRef]  INTEGER  NULL ,[Dis1]  DECIMAL  NULL ,[Dis2]  DECIMAL  NULL ,[Dis3]  DECIMAL  NULL ,[Add1]  DECIMAL  NULL ,[Add2]  DECIMAL  NULL ,[OrderType]  INTEGER  NULL ,[BuyTypeId]  INTEGER  NULL ,[DCRef]  INTEGER  NULL ,[DisType]  INTEGER  NULL ,[AccYear]  INTEGER  NULL ,[DCSaleOfficeRef]  INTEGER  NULL ,[Charge]  DECIMAL  NULL ,[Tax]  DECIMAL  NULL ,[paymentUsanceRef]  INTEGER  NULL ,[StockDCCode]  NVARCHAR(2048)  NULL ,[DealerCode]  NVARCHAR(2048)  NULL ,[SupervisorCode]  NVARCHAR(2048)  NULL ,[OrderId]  INTEGER  NULL ,[OrderNo]  INTEGER  NULL );

-- ----------------------------
-- Table structure for DiscountDiscountItemCount
-- ----------------------------
DROP TABLE IF EXISTS "main"."DiscountDiscountItemCount";
CREATE TABLE "DiscountDiscountItemCount"([DisRef]  INTEGER  NULL ,[GoodsRef]  INTEGER  NULL );

-- ----------------------------
-- Table structure for DiscountDisSalePrizePackageSDS
-- ----------------------------
DROP TABLE IF EXISTS "main"."DiscountDisSalePrizePackageSDS";
CREATE TABLE "DiscountDisSalePrizePackageSDS"([ID]  INTEGER  NULL ,[SaleRef]  INTEGER  NULL ,[DiscountRef]  INTEGER  NULL ,[MainGoodsPackageItemRef]  INTEGER  NULL ,[ReplaceGoodsPackageItemRef]  INTEGER  NULL ,[PrizeCount]  INTEGER  NULL ,[PrizeQty]  INTEGER  NULL );

-- ----------------------------
-- Table structure for DiscountDisSaleSDS
-- ----------------------------
DROP TABLE IF EXISTS "main"."DiscountDisSaleSDS";
CREATE TABLE "DiscountDisSaleSDS"([Id]  INTEGER  NULL ,[HdrRef]  INTEGER  NULL ,[ItemRef]  INTEGER  NULL ,[RowNo]  INTEGER  NULL ,[ItemType]  INTEGER  NULL ,[DisRef]  INTEGER  NULL ,[DisGroup]  INTEGER  NULL );

-- ----------------------------
-- Table structure for DiscountFreeReason
-- ----------------------------
DROP TABLE IF EXISTS "main"."DiscountFreeReason";
CREATE TABLE "DiscountFreeReason"([FreeReasonId]  INTEGER  NULL ,[FreeReasonName]  NVARCHAR(2048)  NULL ,[CalcPriceType]  INTEGER  NULL ,[DisAccType]  INTEGER  NULL );

-- ----------------------------
-- Table structure for DiscountGoodsPackage
-- ----------------------------
DROP TABLE IF EXISTS "main"."DiscountGoodsPackage";
CREATE TABLE "DiscountGoodsPackage"([ID]  INTEGER  NULL ,[DiscountRef]  INTEGER  NULL );

-- ----------------------------
-- Table structure for DiscountGoodsPackageItem
-- ----------------------------
DROP TABLE IF EXISTS "main"."DiscountGoodsPackageItem";
CREATE TABLE "DiscountGoodsPackageItem"([ID]  INTEGER  NULL ,[GoodsPackageRef]  INTEGER  NULL ,[GoodsRef]  INTEGER  NULL ,[UnitQty]  INTEGER  NULL ,[UnitRef]  INTEGER  NULL ,[TotalQty]  INTEGER  NULL ,[ReplaceGoodsRef]  INTEGER  NULL ,[PrizePriority]  DECIMAL  NULL );

-- ----------------------------
-- Table structure for DiscountPriceHistory
-- ----------------------------
DROP TABLE IF EXISTS "main"."DiscountPriceHistory";
CREATE TABLE "DiscountPriceHistory" (
"ID"  INTEGER,
"GoodsRef"  INTEGER,
"DCRef"  INTEGER,
"SalePrice"  DECIMAL,
"UserPrice"  DECIMAL,
"GoodsCtgrRef"  INTEGER,
"StartDate"  NVARCHAR(20),
"EndDate"  NVARCHAR(20),
"IsActive"  INTEGER,
"UsanceDay"  INTEGER,
"CustRef"  INTEGER,
"CustActRef"  INTEGER,
"CustCtgrRef"  INTEGER,
"CustLevelRef"  INTEGER,
"StateRef"  INTEGER,
"AreaRef"  INTEGER,
"CountyRef"  INTEGER,
"UserRef"  INTEGER,
"ModifiedDate"  NVARCHAR(20),
"ModifiedDateBeforeSend"  NVARCHAR(20),
"UserRefBeforeSend"  INTEGER,
"UniqueId"  NVARCHAR(50)
);

-- ----------------------------
-- Table structure for DiscountProduct
-- ----------------------------
DROP TABLE IF EXISTS "main"."DiscountProduct";
CREATE TABLE "DiscountProduct"([ProductId]  INTEGER  NULL ,[ProductName]  NVARCHAR(2048)  NULL ,[ProductCode]  NVARCHAR(2048)  NULL ,[ProductSubGroupId]  INTEGER  NULL ,[SmallUnitId]  INTEGER  NULL ,[SmallUnitName]  NVARCHAR(2048)  NULL ,[SmallUnitQty]  DECIMAL  NULL ,[LargeUnitId]  INTEGER  NULL ,[LargeUnitName]  NVARCHAR(2048)  NULL ,[LargeUnitQty]  DECIMAL  NULL ,[Description]  NVARCHAR(2048)  NULL ,[ProductTypeId]  INTEGER  NULL ,[ManufacturerId]  INTEGER  NULL ,[ProductBoGroupId]  INTEGER  NULL ,[BrandId]  INTEGER  NULL ,[ProductCtgrId]  INTEGER  NULL ,[Tax]  DECIMAL  NULL ,[Charge]  DECIMAL  NULL ,[CartonType]  INTEGER  NULL ,[ProductWeight]  DECIMAL  NULL ,[IsForSale]  INTEGER  NULL ,[IsForReturn]  INTEGER  NULL ,[HasBatch]  INTEGER  NULL ,[ProductSubGroup1id]  INTEGER  NULL ,[ProductSubGroup2id]  INTEGER  NULL ,[CartonPrizeQty]  INTEGER  NULL ,[GoodsVolume]  INTEGER  NULL ,[ManufacturerCode]  NVARCHAR(2048)  NULL ,[ShipTypeId]  INTEGER  NULL ,[HasImage]  INTEGER  NULL ,[CanBeFree]  INTEGER  NULL );

-- ----------------------------
-- Table structure for DiscountProductBoGroup
-- ----------------------------
DROP TABLE IF EXISTS "main"."DiscountProductBoGroup";
CREATE TABLE "DiscountProductBoGroup"([ID]  INTEGER  NULL ,[ParentRef]  INTEGER  NULL ,[GoodsGroupName]  NVARCHAR(2048)  NULL ,[BarCode]  NVARCHAR(2048)  NULL ,[DLCode]  NVARCHAR(2048)  NULL ,[NLeft]  INTEGER  NULL ,[NRight]  INTEGER  NULL ,[NLevel]  INTEGER  NULL );

-- ----------------------------
-- Table structure for DiscountProductMainSubType
-- ----------------------------
DROP TABLE IF EXISTS "main"."DiscountProductMainSubType";
CREATE TABLE "DiscountProductMainSubType"([Id]  INTEGER  NULL ,[GoodsRef]  INTEGER  NULL ,[MainTypeRef]  INTEGER  NULL ,[SubTypeRef]  INTEGER  NULL ,[UniqueId]  NULL );

-- ----------------------------
-- Table structure for DiscountProductTaxInfo
-- ----------------------------
DROP TABLE IF EXISTS "main"."DiscountProductTaxInfo";
CREATE TABLE "DiscountProductTaxInfo"([ProductId]  INTEGER  NULL ,[TaxRate]  DECIMAL  NULL ,[ChargeRate]  DECIMAL  NULL );

-- ----------------------------
-- Table structure for DiscountProductUnit
-- ----------------------------
DROP TABLE IF EXISTS "main"."DiscountProductUnit";
CREATE TABLE "DiscountProductUnit"([ProductUnitId]  INTEGER  NULL ,[ProductUnitName]  NVARCHAR(2048)  NULL ,[ProductId]  INTEGER  NULL ,[Quantity]  DECIMAL  NULL ,[IsDefault]  INTEGER  NULL ,[ForSale]  INTEGER  NULL ,[BackOfficeId]  INTEGER  NULL ,[Status]  INTEGER  NULL );

-- ----------------------------
-- Table structure for DiscountSDS4_16
-- ----------------------------
DROP TABLE IF EXISTS "main"."DiscountSDS4_16";
CREATE TABLE "DiscountSDS4_16"([ID]  INTEGER  NULL ,[DisGroup]  INTEGER  NULL ,[Priority]  INTEGER  NULL ,[Code]  INTEGER  NULL ,[DisType]  INTEGER  NULL ,[PrizeType]  INTEGER  NULL ,[StartDate]  NVARCHAR(2048)  NULL ,[EndDate]  NVARCHAR(2048)  NULL ,[MinQty]  DECIMAL  NULL ,[MaxQty]  DECIMAL  NULL ,[QtyUnit]  INTEGER  NULL ,[MinAmount]  DECIMAL  NULL ,[MaxAmount]  DECIMAL  NULL ,[PrizeQty]  INTEGER  NULL ,[PrizeRef]  INTEGER  NULL ,[PrizeStep]  INTEGER  NULL ,[PrizeUnit]  DECIMAL  NULL ,[DisPerc]  DECIMAL  NULL ,[DisPrice]  DECIMAL  NULL ,[GoodsRef]  INTEGER  NULL ,[DCRef]  INTEGER  NULL ,[CustActRef]  INTEGER  NULL ,[CustCtgrRef]  INTEGER  NULL ,[StateRef]  INTEGER  NULL ,[AreaRef]  INTEGER  NULL ,[GoodsCtgrRef]  INTEGER  NULL ,[CustRef]  INTEGER  NULL ,[DisAccRef]  INTEGER  NULL ,[PayType]  INTEGER  NULL ,[OrderType]  INTEGER  NULL ,[SupPerc]  DECIMAL  NULL ,[AddPerc]  DECIMAL  NULL ,[SaleOfficeRef]  INTEGER  NULL ,[Comment]  NVARCHAR(2048)  NULL ,[ApplyInGroup]  INTEGER  NULL ,[CalcPriority]  INTEGER  NULL ,[CalcMethod]  INTEGER  NULL ,[CustLevelRef]  INTEGER  NULL ,[GoodsGroupRef]  INTEGER  NULL ,[ManufacturerRef]  INTEGER  NULL ,[SaleZoneRef]  INTEGER  NULL ,[MainTypeRef]  INTEGER  NULL ,[SubTypeRef]  INTEGER  NULL ,[BrandRef]  INTEGER  NULL ,[MinWeight]  DECIMAL  NULL ,[MaxWeight]  DECIMAL  NULL ,[UserRef]  INTEGER  NULL ,[ModifiedDate]  NVARCHAR(2048)  NULL ,[PrizeIncluded]  INTEGER  NULL ,[ModifiedDateBeforeSend]  NVARCHAR(2048)  NULL ,[UserRefBeforeSend]  INTEGER  NULL ,[MinRowsCount]  INTEGER  NULL ,[MinCustChequeRetQty]  INTEGER  NULL ,[MaxCustChequeRetQty]  INTEGER  NULL ,[MinCustRemAmount]  DECIMAL  NULL ,[MaxCustRemAmount]  DECIMAL  NULL ,[MaxRowsCount]  INTEGER  NULL ,[IsActive]  INTEGER  NULL ,[IsPrize]  INTEGER  NULL ,[CustomerSubGroup1Id]  INTEGER  NULL ,[CustomerSubGroup2Id]  INTEGER  NULL ,[ProductSubGroup1Id]  INTEGER  NULL ,[ProductSubGroup2Id]  INTEGER  NULL ,[DetailIsActive]  INTEGER  NULL ,[DetailPriority]  INTEGER  NULL ,[PromotionDetailCustomerGroupId]  INTEGER  NULL ,[PromotionDetailId]  INTEGER  NULL ,[PromotionDetailCustomerId]  INTEGER  NULL ,[DetailPriority1]  INTEGER  NULL ,[ReduceOfQty]  INTEGER  NULL ,[HasAdvanceCondition]  INTEGER  NULL ,[SqlCondition]  NVARCHAR(2048)  NULL ,[OrderNo]  INTEGER  NULL ,[PrizeStepType]  INTEGER  NULL ,[PrizePackageRef]  INTEGER  NULL );

-- ----------------------------
-- Table structure for DiscountSDS4_19
-- ----------------------------
DROP TABLE IF EXISTS "main"."DiscountSDS4_19";
CREATE TABLE "DiscountSDS4_19"([goodsRef]  INTEGER  NULL ,[ID]  INTEGER  NULL ,[DisGroup]  INTEGER  NULL ,[Priority]  INTEGER  NULL ,[Code]  INTEGER  NULL ,[DisType]  INTEGER  NULL ,[PrizeType]  INTEGER  NULL ,[StartDate]  NVARCHAR(2048)  NULL ,[EndDate]  NVARCHAR(2048)  NULL ,[MinQty]  DECIMAL  NULL ,[MaxQty]  DECIMAL  NULL ,[QtyUnit]  INTEGER  NULL ,[MinAmount]  DECIMAL  NULL ,[MaxAmount]  DECIMAL  NULL ,[PrizeQty]  INTEGER  NULL ,[PrizeRef]  INTEGER  NULL ,[PrizeStep]  INTEGER  NULL ,[PrizeUnit]  DECIMAL  NULL ,[DisPerc]  DECIMAL  NULL ,[DisPrice]  DECIMAL  NULL ,[GoodsCtgrRef]  INTEGER  NULL ,[DisAccRef]  INTEGER  NULL ,[SupPerc]  DECIMAL  NULL ,[AddPerc]  DECIMAL  NULL ,[Comment]  NVARCHAR(2048)  NULL ,[ApplyInGroup]  INTEGER  NULL ,[CalcPriority]  INTEGER  NULL ,[CalcMethod]  INTEGER  NULL ,[GoodsGroupRef]  INTEGER  NULL ,[ManufacturerRef]  INTEGER  NULL ,[MainTypeRef]  INTEGER  NULL ,[SubTypeRef]  INTEGER  NULL ,[BrandRef]  INTEGER  NULL ,[MinWeight]  DECIMAL  NULL ,[MaxWeight]  DECIMAL  NULL ,[UserRef]  INTEGER  NULL ,[ModifiedDate]  NVARCHAR(2048)  NULL ,[PrizeIncluded]  INTEGER  NULL ,[ModifiedDateBeforeSend]  NVARCHAR(2048)  NULL ,[UserRefBeforeSend]  INTEGER  NULL ,[MinRowsCount]  INTEGER  NULL ,[MinCustChequeRetQty]  INTEGER  NULL ,[MaxCustChequeRetQty]  INTEGER  NULL ,[MinCustRemAmount]  DECIMAL  NULL ,[MaxCustRemAmount]  DECIMAL  NULL ,[MaxRowsCount]  INTEGER  NULL ,[IsActive]  INTEGER  NULL ,[IsPrize]  INTEGER  NULL ,[CustomerSubGroup1Id]  INTEGER  NULL ,[CustomerSubGroup2Id]  INTEGER  NULL ,[ProductSubGroup1Id]  INTEGER  NULL ,[ProductSubGroup2Id]  INTEGER  NULL ,[DetailIsActive]  INTEGER  NULL ,[DetailPriority]  INTEGER  NULL ,[PromotionDetailCustomerGroupId]  INTEGER  NULL ,[PromotionDetailId]  INTEGER  NULL ,[PromotionDetailCustomerId]  INTEGER  NULL ,[ReduceOfQty]  INTEGER  NULL ,[HasAdvanceCondition]  INTEGER  NULL ,[SqlCondition]  NVARCHAR(2048)  NULL ,[PrizeStepType]  INTEGER  NULL ,[PrizePackageRef]  INTEGER  NULL ,[PrizeStepUnit]  INTEGER  NULL ,[TotalMinAmount]  DECIMAL  NULL ,[TotalMaxAmount]  DECIMAL  NULL ,[TotalMinRowCount]  INTEGER  NULL ,[TotalMaxRowCount]  INTEGER  NULL ,[MixCondition]  INTEGER  NULL );

-- ----------------------------
-- Table structure for DiscountSellPayType
-- ----------------------------
DROP TABLE IF EXISTS "main"."DiscountSellPayType";
CREATE TABLE "DiscountSellPayType"([CustomerId]  INTEGER  NULL ,[SellPayTypeId]  INTEGER  NULL ,[SellPayTypeName]  NVARCHAR(2048)  NULL ,[BuyTypeId]  INTEGER  NULL ,[CheckDebit]  INTEGER  NULL ,[CheckCredit]  INTEGER  NULL ,[PaymentDeadLine]  INTEGER  NULL ,[PaymentTime]  INTEGER  NULL );

-- ----------------------------
-- Table structure for DiscountTourProduct
-- ----------------------------
DROP TABLE IF EXISTS "main"."DiscountTourProduct";
CREATE TABLE "DiscountTourProduct"([ProductId]  INTEGER  NULL ,[OnHandQty]  INTEGER  NULL ,[SoldQty]  INTEGER  NULL ,[RenewQty]  INTEGER  NULL ,[OnHandBulkQty]  DECIMAL  NULL ,[SoldBulkQty]  DECIMAL  NULL ,[RenewBulkQty]  INTEGER  NULL );

-- ----------------------------
-- Table structure for DiscountVnLite
-- ----------------------------
DROP TABLE IF EXISTS "main"."DiscountVnLite";
CREATE TABLE "DiscountVnLite" (
"UniqueId"  TEXT NOT NULL,
"DisGroup"  INTEGER,
"Id"  INTEGER,
"Priority"  INTEGER,
"StartDate"  NVARCHAR(20),
"EndDate"  NVARCHAR(20),
"Comment"  TEXT(2048),
"MinQty"  REAL,
"MaxQty"  REAL,
"MinAmount"  REAL,
"MaxAmount"  REAL,
"MinRowsCount"  INTEGER,
"MaxRowsCount"  INTEGER,
"PrizeQty"  REAL,
"AddPerc"  REAL,
"DisPerc"  REAL,
"IsPrize"  INTEGER,
"PrizeRef"  INTEGER,
"DisType"  INTEGER,
"ManufacturerRef"  INTEGER,
"ProductSubGroup1Id"  INTEGER,
"ProductSubGroup2Id"  INTEGER,
"CustomerSubGroup1Id"  INTEGER,
"CustomerSubGroup2Id"  INTEGER,
"ReduceOfQty"  REAL,
"GoodsRef"  INTEGER,
"DetailIsActive"  INTEGER,
"DetailPriority"  INTEGER,
"GoodsCtgrRef"  INTEGER,
"CustRef"  INTEGER,
"CustCtgrRef"  INTEGER,
"PromotionDetailCustomerGroupId"  INTEGER,
"PromotionDetailId"  INTEGER,
"PromotionDetailCustomerId"  INTEGER,
"DCRef"  INTEGER,
"CalcPriority"  INTEGER,
"PayType"  INTEGER,
"MinWeight"  REAL,
"MaxWeight"  REAL,
"PrizeStep"  INTEGER,
"CalcMethod"  INTEGER,
"DisPrice"  REAL,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for EVCHeaderSDS
-- ----------------------------
DROP TABLE IF EXISTS "main"."EVCHeaderSDS";
CREATE TABLE [EVCHeaderSDS]([_id]  INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL ,[RefId]  INTEGER  NULL ,[Dis1]  DECIMAL  NULL ,[Dis2]  DECIMAL  NULL ,[Dis3]  DECIMAL  NULL ,[Add1]  DECIMAL  NULL ,[Add2]  DECIMAL  NULL ,[OrderType]  INTEGER  NULL ,[PayType]  INTEGER  NULL ,[StockDCRef]  INTEGER  NULL ,[DCRef]  INTEGER  NULL ,[DisType]  INTEGER  NULL ,[CustRef]  INTEGER  NULL ,[AccYear]  INTEGER  NULL ,[DCSaleOfficeRef]  INTEGER  NULL ,[CallId]  NVARCHAR(2048)  NULL ,[EVCId]  NVARCHAR(2048)  NULL ,[Tax]  INTEGER  NULL ,[Charge]  INTEGER  NULL ,[Amount]  DECIMAL  NULL ,[NetAmount]  DECIMAL  NULL ,[EVCType]  INTEGER  NULL ,[SaleOfficeRef]  INTEGER  NULL ,[DCCode]  NVARCHAR(2048)  NULL ,[EvcDate]  NVARCHAR(2048)  NULL ,[StockDCCode]  NVARCHAR(2048)  NULL ,[DealerRef]  INTEGER  NULL ,[DealerCode]  NVARCHAR(2048)  NULL ,[SupervisorRef]  INTEGER  NULL ,[SupervisorCode]  NVARCHAR(2048)  NULL ,[PaymentUsanceRef]  INTEGER  NULL ,[DiscountAmount]  DECIMAL  NULL ,[AddAmount]  DECIMAL  NULL );

-- ----------------------------
-- Table structure for EVCHeaderVnLite
-- ----------------------------
DROP TABLE IF EXISTS "main"."EVCHeaderVnLite";
CREATE TABLE "EVCHeaderVnLite" (
"_id"  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
"RefId"  INTEGER,
"DiscountAmount"  REAL,
"AddAmount"  REAL,
"OrderType"  INTEGER,
"PayType"  INTEGER,
"StockDCRef"  INTEGER,
"DCRef"  INTEGER,
"DisType"  INTEGER,
"AccYear"  INTEGER,
"DCSaleOfficeRef"  INTEGER,
"CallId"  TEXT,
"EVCId"  TEXT,
"Tax"  INTEGER,
"Charge"  INTEGER,
"CustRef"  INTEGER,
"Amount"  REAL,
"NetAmount"  REAL,
"EVCType"  INTEGER
);

-- ----------------------------
-- Table structure for EVCItemSDS
-- ----------------------------
DROP TABLE IF EXISTS "main"."EVCItemSDS";
CREATE TABLE [EVCItemSDS]([_id]  INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL ,[RowOrder]  INTEGER  NULL ,[GoodsRef]  INTEGER  NULL ,[UnitQty]  INTEGER  NULL ,[UnitCapasity]  DECIMAL  NULL ,[TotalQty]  DECIMAL  NULL ,[AmountNut]  DECIMAL  NULL ,[Discount]  DECIMAL  NULL ,[Amount]  DECIMAL  NULL ,[PrizeType]  INTEGER  NULL ,[SupAmount]  INTEGER  NULL ,[AddAmount]  INTEGER  NULL ,[CustPrice]  DECIMAL  NULL ,[UserPrice]  DECIMAL  NULL ,[ChargePercent]  DECIMAL  NULL ,[TaxPercent]  DECIMAL  NULL ,[EVCRef]  NVARCHAR(2048)  NULL ,[CallId]  NVARCHAR(2048)  NULL ,[TotalWeight]  DECIMAL  NULL ,[UnitRef]  INTEGER  NULL ,[DisRef]  INTEGER  NULL ,[Tax]  INTEGER  NULL ,[Charge]  INTEGER  NULL ,[PriceId]  INTEGER  NULL ,[PackQty]  INTEGER  NULL ,[BatchId]  INTEGER  NULL ,[ReduceOfQty]  INTEGER  NULL ,[ItemVolume]  INTEGER  NULL ,[CPriceRef]  INTEGER  NULL ,[PeriodicDiscountRef]  INTEGER  NULL ,[EvcItemDis1]  DECIMAL  NULL ,[EvcItemDis2]  DECIMAL  NULL ,[EvcItemDis3]  DECIMAL  NULL ,[EvcItemAdd1]  DECIMAL  NULL ,[EvcItemAdd2]  DECIMAL  NULL ,[FreeReasonId]  INTEGER  NULL );

-- ----------------------------
-- Table structure for EVCItemStatutesSDS
-- ----------------------------
DROP TABLE IF EXISTS "main"."EVCItemStatutesSDS";
CREATE TABLE [EVCItemStatutesSDS]([_id]  INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL ,[EVCItemRef]  INTEGER  NULL ,[RowOrder]  INTEGER  NULL ,[DisRef]  INTEGER  NULL ,[DisGroup]  INTEGER  NULL ,[AddAmount]  INTEGER  NULL ,[SupAmount]  INTEGER  NULL ,[Discount]  INTEGER  NULL ,[EvcId]  NVARCHAR(2048)  NULL );

-- ----------------------------
-- Table structure for EVCItemStatutesVnLite
-- ----------------------------
DROP TABLE IF EXISTS "main"."EVCItemStatutesVnLite";
CREATE TABLE "EVCItemStatutesVnLite" (
"_id"  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
"ProductId"  INTEGER,
"DisRef"  INTEGER,
"DisGroup"  INTEGER,
"DisAmount"  REAL,
"AddAmount"  REAL,
"EVCId"  TEXT,
"EVCDetailRowOrder"  INTEGER,
"EVCItemRef"  INTEGER,
"RowOrder"  INTEGER
);

-- ----------------------------
-- Table structure for EVCItemVnLite
-- ----------------------------
DROP TABLE IF EXISTS "main"."EVCItemVnLite";
CREATE TABLE "EVCItemVnLite" (
"_id"  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
"RowOrder"  INTEGER,
"GoodsRef"  INTEGER,
"UnitQty"  INTEGER,
"UnitCapasity"  REAL,
"TotalQty"  REAL,
"AmountNut"  REAL,
"Discount"  REAL,
"Amount"  REAL,
"PrizeType"  INTEGER,
"SupAmount"  INTEGER,
"AddAmount"  INTEGER,
"CustPrice"  REAL,
"UserPrice"  REAL,
"ChargePercent"  REAL,
"TaxPercent"  REAL,
"EVCRef"  TEXT,
"CallId"  TEXT,
"TotalWeight"  REAL,
"UnitRef"  INTEGER,
"DisRef"  INTEGER,
"Tax"  INTEGER,
"Charge"  INTEGER,
"PriceId"  INTEGER,
"PackQty"  INTEGER,
"BatchId"  INTEGER,
"ReduceOfQty"  INTEGER,
"EvcItemAdd1"  INTEGER,
"EvcItemAdd2"  INTEGER,
"EvcItemDis1"  INTEGER,
"EvcItemDis2"  INTEGER,
"EvcItemDis3"  INTEGER
);

-- ----------------------------
-- Table structure for EVCSkipDiscount
-- ----------------------------
DROP TABLE IF EXISTS "main"."EVCSkipDiscount";
CREATE TABLE "EVCSkipDiscount" (
"ID"  INTEGER,
"EvcRef"  INTEGER,
"SaleRef"  INTEGER,
"DisRef"  INTEGER,
"SkipGoodsRef"  INTEGER
);

-- ----------------------------
-- Table structure for EVCTempAcceptedDiscountSDS
-- ----------------------------
DROP TABLE IF EXISTS "main"."EVCTempAcceptedDiscountSDS";
CREATE TABLE [EVCTempAcceptedDiscountSDS]([DiscountId]  INTEGER  NULL ,[PrizeCount]  INTEGER  NULL ,[AccYear]  INTEGER  NULL ,[EVCOprDate]  NVARCHAR(2048)  NULL ,[DisAccRef]  INTEGER  NULL );

-- ----------------------------
-- Table structure for EVCTempCustomerSDS
-- ----------------------------
DROP TABLE IF EXISTS "main"."EVCTempCustomerSDS";
CREATE TABLE "EVCTempCustomerSDS" (
"CustRef"  INTEGER,
"CustCtgrRef"  INTEGER,
"CustActRef"  INTEGER,
"CustLevelRef"  INTEGER,
"AreaRef"  INTEGER,
"SalePathRef"  INTEGER,
"ZoneId"  INTEGER,
"CustomerRemain"  INTEGER,
"CountyRef"  INTEGER,
"StateRef"  INTEGER
);

-- ----------------------------
-- Table structure for EVCTempCustomersMainSubTypeSDS
-- ----------------------------
DROP TABLE IF EXISTS "main"."EVCTempCustomersMainSubTypeSDS";
CREATE TABLE "EVCTempCustomersMainSubTypeSDS" (
"Id"  INTEGER NOT NULL,
"CustRef"  INTEGER,
"MainTypeRef"  INTEGER,
"SubTypeRef"  INTEGER,
PRIMARY KEY ("Id")
);

-- ----------------------------
-- Table structure for EvcTempDecreasePrizeSDS
-- ----------------------------
DROP TABLE IF EXISTS "main"."EvcTempDecreasePrizeSDS";
CREATE TABLE "EvcTempDecreasePrizeSDS" (
"GoodsRef"  INTEGER,
"DecreasePrizeQty"  INTEGER
);

-- ----------------------------
-- Table structure for EVCTempDiscountSDS4_16
-- ----------------------------
DROP TABLE IF EXISTS "main"."EVCTempDiscountSDS4_16";
CREATE TABLE "EVCTempDiscountSDS4_16" (
"ID"  INTEGER,
"DisGroup"  INTEGER,
"DisType"  INTEGER,
"Priority"  INTEGER,
"ApplyInGroup"  INTEGER,
"QtyUnit"  DECIMAL,
"MinQty"  DECIMAL,
"MaxQty"  DECIMAL,
"MinAmount"  DECIMAL,
"MaxAmount"  DECIMAL,
"MinWeight"  DECIMAL,
"MaxWeight"  DECIMAL,
"MinRowsCount"  INTEGER,
"MaxRowsCount"  INTEGER,
"OrderType"  INTEGER,
"GoodsRef"  INTEGER,
"IsActive"  INTEGER,
"PayType"  INTEGER,
"AreaRef"  INTEGER,
"StateRef"  INTEGER,
"DcRef"  INTEGER,
"CustActRef"  INTEGER,
"CustCtgrRef"  INTEGER,
"CustLevelRef"  INTEGER,
"CustRef"  INTEGER,
"SaleOfficeRef"  INTEGER,
"SaleZoneRef"  INTEGER,
"GoodsGroupRef"  INTEGER,
"GoodsCtgrRef"  INTEGER,
"ManufacturerRef"  INTEGER,
"OrderNo"  INTEGER,
"MainTypeRef"  INTEGER,
"SubTypeRef"  INTEGER,
"BrandRef"  INTEGER,
"PrizeRef"  INTEGER,
"PrizeStep"  INTEGER,
"PrizeStepType"  INTEGER,
"StartDate"  NVARCHAR(2048),
"EndDate"  NVARCHAR(2048),
"CalcMethod"  INTEGER,
"PrizePackageRef"  INTEGER,
"MinCustRemAmount"  DECIMAL,
"MaxCustRemAmount"  DECIMAL
);

-- ----------------------------
-- Table structure for EVCTempDiscountSDS4_19
-- ----------------------------
DROP TABLE IF EXISTS "main"."EVCTempDiscountSDS4_19";
CREATE TABLE "EVCTempDiscountSDS4_19" (
"ID"  INTEGER,
"DisGroup"  INTEGER,
"DisType"  INTEGER,
"Priority"  INTEGER,
"ApplyInGroup"  INTEGER,
"QtyUnit"  DECIMAL,
"MinQty"  DECIMAL,
"MaxQty"  DECIMAL,
"MinAmount"  DECIMAL,
"MaxAmount"  DECIMAL,
"MinWeight"  DECIMAL,
"MaxWeight"  DECIMAL,
"MinRowsCount"  INTEGER,
"MaxRowsCount"  INTEGER,
"GoodsRef"  INTEGER,
"IsActive"  INTEGER,
"CustRef"  INTEGER,
"GoodsGroupRef"  INTEGER,
"GoodsCtgrRef"  INTEGER,
"ManufacturerRef"  INTEGER,
"OrderNo"  INTEGER,
"MainTypeRef"  INTEGER,
"SubTypeRef"  INTEGER,
"BrandRef"  INTEGER,
"PrizeRef"  INTEGER,
"PrizeStep"  INTEGER,
"PrizeStepType"  INTEGER,
"StartDate"  NVARCHAR(2048),
"EndDate"  NVARCHAR(2048),
"CalcMethod"  INTEGER,
"PrizePackageRef"  INTEGER,
"MinCustRemAmount"  DECIMAL,
"MaxCustRemAmount"  DECIMAL,
"TotalMinAmount"  DECIMAL,
"TotalMaxAmount"  DECIMAL,
"TotalMinRowCount"  INTEGER,
"TotalMaxRowCount"  INTEGER,
"PrizeStepUnit"  INTEGER,
"MixCondition"  INTEGER,
"OrderType"  INTEGER
);

-- ----------------------------
-- Table structure for EVCTempGoodsCategorySDS
-- ----------------------------
DROP TABLE IF EXISTS "main"."EVCTempGoodsCategorySDS";
CREATE TABLE [EVCTempGoodsCategorySDS]([_id]  INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL ,[GoodsRef]  INTEGER  NULL ,[GoodsCtgrRef]  INTEGER  NULL );

-- ----------------------------
-- Table structure for EVCTempGoodsDetailSDS
-- ----------------------------
DROP TABLE IF EXISTS "main"."EVCTempGoodsDetailSDS";
CREATE TABLE [EVCTempGoodsDetailSDS]([Id]  INTEGER  NULL ,[GoodsGroupRef]  INTEGER  NULL ,[ManufacturerRef]  INTEGER  NULL ,[BrandRef]  INTEGER  NULL ,[CartonType]  INTEGER  NULL ,[Weight]  INTEGER  NULL ,[CartonPrizeQty]  INTEGER  NULL ,[GoodsCtgrRef]  INTEGER  NULL );

-- ----------------------------
-- Table structure for EVCTempGoodsGroupDetailSDS
-- ----------------------------
DROP TABLE IF EXISTS "main"."EVCTempGoodsGroupDetailSDS";
CREATE TABLE [EVCTempGoodsGroupDetailSDS]([Id]  INTEGER  NULL ,[ParentId]  INTEGER  NULL ,[NLeft]  INTEGER  NULL ,[NRight]  INTEGER  NULL );

-- ----------------------------
-- Table structure for EVCTempGoodsMainSubTypeSDS
-- ----------------------------
DROP TABLE IF EXISTS "main"."EVCTempGoodsMainSubTypeSDS";
CREATE TABLE [EVCTempGoodsMainSubTypeSDS]([_id]  INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL ,[id]  INTEGER  NULL ,[GoodsRef]  INTEGER  NULL ,[MainTypeRef]  INTEGER  NULL ,[SubTypeRef]  INTEGER  NULL );

-- ----------------------------
-- Table structure for EVCTempGoodsPackageItemSDS
-- ----------------------------
DROP TABLE IF EXISTS "main"."EVCTempGoodsPackageItemSDS";
CREATE TABLE [EVCTempGoodsPackageItemSDS]([DiscountId]  INTEGER  NULL ,[GoodsPackageRef]  INTEGER  NULL ,[BaseGoodsRef]  INTEGER  NULL ,[GoodsRef]  INTEGER  NULL ,[UnitQty]  INTEGER  NULL ,[UnitRef]  INTEGER  NULL ,[TotalQty]  INTEGER  NULL ,[ReplaceGoodsPackageItemRef]  INTEGER  NULL ,[PrizeCount]  INTEGER  NULL ,[EVCRef]  INTEGER  NULL );

-- ----------------------------
-- Table structure for EVCTempRemPrizeSDS
-- ----------------------------
DROP TABLE IF EXISTS "main"."EVCTempRemPrizeSDS";
CREATE TABLE "EVCTempRemPrizeSDS" (
"GoodsRef"  INTEGER,
"RemPrizeQty"  INTEGER
);

-- ----------------------------
-- Table structure for EVCTempReturnItemSDS
-- ----------------------------
DROP TABLE IF EXISTS "main"."EVCTempReturnItemSDS";
CREATE TABLE "EVCTempReturnItemSDS" (
"_id"  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
"EVCId"  INTEGER,
"RowOrder"  INTEGER,
"GoodsRef"  INTEGER,
"UnitQty"  DECIMAL,
"CPriceRef"  INTEGER,
"UnitRef"  INTEGER,
"UnitCapasity"  DECIMAL,
"TotalQty"  DECIMAL,
"AmountNut"  DECIMAL,
"Discount"  DECIMAL,
"Amount"  DECIMAL,
"PrizeType"  DECIMAL,
"SupAmount"  DECIMAL,
"AddAmount"  DECIMAL,
"Add1"  DECIMAL,
"Add2"  DECIMAL,
"UserPrice"  DECIMAL,
"CustPrice"  DECIMAL,
"PriceId"  INTEGER,
"Charge"  DECIMAL,
"Tax"  DECIMAL,
"Dis1"  DECIMAL,
"Dis2"  DECIMAL,
"Dis3"  DECIMAL,
"FreeReasonId"  INTEGER
);

-- ----------------------------
-- Table structure for EVCTempReturnItemSummaryFinalSDS
-- ----------------------------
DROP TABLE IF EXISTS "main"."EVCTempReturnItemSummaryFinalSDS";
CREATE TABLE "EVCTempReturnItemSummaryFinalSDS" (
"_id"  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
"EVCId"  INTEGER,
"RowOrder"  INTEGER,
"GoodsRef"  INTEGER,
"UnitQty"  DECIMAL,
"CPriceRef"  INTEGER,
"UnitRef"  INTEGER,
"UnitCapasity"  DECIMAL,
"TotalQty"  DECIMAL,
"AmountNut"  DECIMAL,
"Discount"  DECIMAL,
"RemainDiscount"  DECIMAL,
"Amount"  DECIMAL,
"PrizeType"  DECIMAL,
"SupAmount"  DECIMAL,
"AddAmount"  DECIMAL,
"RemainAddAmount"  DECIMAL,
"Add1"  DECIMAL,
"Add2"  DECIMAL,
"RemainAdd1"  DECIMAL,
"RemainAdd2"  DECIMAL,
"UserPrice"  DECIMAL,
"CustPrice"  DECIMAL,
"PriceId"  INTEGER,
"Charge"  DECIMAL,
"Tax"  DECIMAL,
"Dis1"  DECIMAL,
"Dis2"  DECIMAL,
"Dis3"  DECIMAL,
"RemainDis1"  DECIMAL,
"RemainDis2"  DECIMAL,
"RemainDis3"  DECIMAL,
"FreeReasonId"  INTEGER
);

-- ----------------------------
-- Table structure for EVCTempReturnItemSummarySDS
-- ----------------------------
DROP TABLE IF EXISTS "main"."EVCTempReturnItemSummarySDS";
CREATE TABLE "EVCTempReturnItemSummarySDS" (
"_id"  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
"EVCId"  INTEGER,
"RowOrder"  INTEGER,
"GoodsRef"  INTEGER,
"UnitQty"  DECIMAL,
"CPriceRef"  INTEGER,
"UnitRef"  INTEGER,
"UnitCapasity"  DECIMAL,
"TotalQty"  DECIMAL,
"AmountNut"  DECIMAL,
"Discount"  DECIMAL,
"Amount"  DECIMAL,
"PrizeType"  DECIMAL,
"SupAmount"  DECIMAL,
"AddAmount"  DECIMAL,
"Add1"  DECIMAL,
"Add2"  DECIMAL,
"UserPrice"  DECIMAL,
"CustPrice"  DECIMAL,
"PriceId"  INTEGER,
"Charge"  DECIMAL,
"Tax"  DECIMAL,
"Dis1"  DECIMAL,
"Dis2"  DECIMAL,
"Dis3"  DECIMAL,
"FreeReasonId"  INTEGER
);

-- ----------------------------
-- Table structure for EvcTempReturnPrizeSDS
-- ----------------------------
DROP TABLE IF EXISTS "main"."EvcTempReturnPrizeSDS";
CREATE TABLE "EvcTempReturnPrizeSDS" (
"GoodsRef"  INTEGER,
"ReturnPrizeQty"  INTEGER
);

-- ----------------------------
-- Table structure for EVCTempSDS
-- ----------------------------
DROP TABLE IF EXISTS "main"."EVCTempSDS";
CREATE TABLE "EVCTempSDS" (
"_id"  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
"DiscountId"  INTEGER,
"DisGroup"  INTEGER,
"DisType"  INTEGER,
"EVCItemRefId"  INTEGER,
"RowOrder"  INTEGER,
"Priority"  INTEGER,
"ReqQty"  DECIMAL,
"ReqAmount"  DECIMAL,
"ReqRowCount"  INTEGER,
"MinQty"  DECIMAL,
"MaxQty"  DECIMAL,
"UnitCapasity"  DECIMAL,
"MinAmount"  DECIMAL,
"MaxAmount"  DECIMAL,
"EVCId"  NVARCHAR(2048),
"ApplyInGroup"  INTEGER,
"ReqWeight"  DECIMAL,
"MinWeight"  DECIMAL,
"MaxWeight"  DECIMAL,
"MinRowsCount"  INTEGER,
"MaxRowsCount"  INTEGER,
"DistItemRefId"  INTEGER,
"PrizeStep"  INTEGER,
"PrizeStepType"  INTEGER,
"TotalMinAmount"  INTEGER,
"TotalMaxAmount"  INTEGER,
"TotalMinRowCount"  INTEGER,
"TotalMaxRowCount"  INTEGER
);

-- ----------------------------
-- Table structure for EVCTempSummary3SDS
-- ----------------------------
DROP TABLE IF EXISTS "main"."EVCTempSummary3SDS";
CREATE TABLE "EVCTempSummary3SDS" (
"EVCItemRef"  INTEGER,
"RowOrder"  INTEGER,
"DisRef"  INTEGER,
"DisGroup"  INTEGER,
"EVCId"  NVARCHAR(100)
);

-- ----------------------------
-- Table structure for EVCTempSummaryFinalSDS
-- ----------------------------
DROP TABLE IF EXISTS "main"."EVCTempSummaryFinalSDS";
CREATE TABLE [EVCTempSummaryFinalSDS]([_id]  INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL ,[DisId]  INTEGER  NULL ,[DisGroup]  INTEGER  NULL ,[DisType]  INTEGER  NULL ,[EVCItemRef]  INTEGER  NULL ,[RowOrder]  INTEGER  NULL ,[Priority]  INTEGER  NULL ,[ReqQty]  DECIMAL  NULL ,[ReqRowCount]  DECIMAL  NULL ,[ReqAmount]  DECIMAL  NULL ,[ReqWeight]  DECIMAL  NULL ,[EVCId]  NVARCHAR(2048)  NULL );

-- ----------------------------
-- Table structure for EVCTempSummaryFinalVnLite
-- ----------------------------
DROP TABLE IF EXISTS "main"."EVCTempSummaryFinalVnLite";
CREATE TABLE "EVCTempSummaryFinalVnLite" (
"_id"  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
"ProductId"  INTEGER,
"EVCId"  TEXT,
"Qty"  REAL,
"DisId"  INTEGER,
"UnitPrice"  INTEGER,
"PriceId"  INTEGER,
"ReduceOfQty"  INTEGER
);

-- ----------------------------
-- Table structure for EVCTempSummarySDS
-- ----------------------------
DROP TABLE IF EXISTS "main"."EVCTempSummarySDS";
CREATE TABLE [EVCTempSummarySDS]([_id]  INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL ,[DisId]  INTEGER  NULL ,[DisGroup]  INTEGER  NULL ,[DisType]  INTEGER  NULL ,[EVCItemRef]  INTEGER  NULL ,[RowOrder]  INTEGER  NULL ,[Priority]  INTEGER  NULL ,[ReqQty]  DECIMAL  NULL ,[ReqRowCount]  DECIMAL  NULL ,[ReqAmount]  DECIMAL  NULL ,[ReqWeight]  DECIMAL  NULL ,[EVCId]  NVARCHAR(2048)  NULL );

-- ----------------------------
-- Table structure for EVCTempSummaryVnLite
-- ----------------------------
DROP TABLE IF EXISTS "main"."EVCTempSummaryVnLite";
CREATE TABLE "EVCTempSummaryVnLite" (
"_id"  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
"DisId"  INTEGER,
"DisGroup"  INTEGER,
"Priority"  INTEGER,
"ReqQty"  REAL,
"ReqAmount"  REAL,
"ReqWeight"  REAL,
"MainProductId"  INTEGER,
"EVCId"  TEXT,
"ProductId"  INTEGER
);

-- ----------------------------
-- Table structure for EVCTempVnLite
-- ----------------------------
DROP TABLE IF EXISTS "main"."EVCTempVnLite";
CREATE TABLE "EVCTempVnLite" (
"_id"  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
"DiscountId"  INTEGER,
"DisGroup"  INTEGER,
"Priority"  INTEGER,
"ReqQty"  REAL,
"ReqAmount"  REAL,
"MinQty"  REAL,
"MaxQty"  REAL,
"MinAmount"  REAL,
"MaxAmount"  REAL,
"ReqWeight"  REAL,
"MinWeight"  REAL,
"MaxWeight"  REAL,
"ProductId"  INTEGER,
"MainProductId"  INTEGER,
"ReqRowCount"  INTEGER,
"MinRowsCount"  INTEGER,
"MaxRowsCount"  INTEGER,
"PrizeRefId"  INTEGER,
"EVCId"  TEXT
);

-- ----------------------------
-- Indexes structure for table DiscountConditionSDS4_19
-- ----------------------------
CREATE INDEX "main"."CustRefIndex"
ON "DiscountConditionSDS4_19" ("CustRef" ASC);
CREATE INDEX "main"."DiscountRefIndex"
ON "DiscountConditionSDS4_19" ("DiscountRef" ASC);
CREATE INDEX "main"."OrderTypeIndex"
ON "DiscountConditionSDS4_19" ("OrderType" ASC);

-- ----------------------------
-- Indexes structure for table DiscountSDS4_19
-- ----------------------------
CREATE INDEX "main"."DisGroupIndex"
ON "DiscountSDS4_19" ("DisGroup" ASC);

-- ----------------------------
-- Indexes structure for table EVCHeaderSDS
-- ----------------------------
CREATE INDEX "main"."EVCIdIndex"
ON "EVCHeaderSDS" ("EVCId" ASC);

-- ----------------------------
-- Indexes structure for table EVCItemSDS
-- ----------------------------
CREATE INDEX "main"."EVCRefIndex"
ON "EVCItemSDS" ("EVCRef" ASC);
CREATE INDEX "main"."FreeReasonIdIndex"
ON "EVCItemSDS" ("FreeReasonId" ASC);

-- ----------------------------
-- Indexes structure for table EVCItemStatutesSDS
-- ----------------------------
CREATE INDEX "main"."EvcIdStatutesIndex"
ON "EVCItemStatutesSDS" ("EvcId" ASC);

-- ----------------------------
-- Indexes structure for table EVCTempSDS
-- ----------------------------
CREATE INDEX "main"."DiscountIdIndex"
ON "EVCTempSDS" ("DiscountId" ASC);
CREATE INDEX "main"."EVCItemRefIdIndex"
ON "EVCTempSDS" ("EVCItemRefId" ASC);

-- ----------------------------
-- Indexes structure for table EVCTempSummaryFinalSDS
-- ----------------------------
CREATE INDEX "main"."EVCItemRefIndex"
ON "EVCTempSummaryFinalSDS" ("EVCItemRef" ASC);
CREATE INDEX "main"."TempSummaryFinalEVCIdIndex"
ON "EVCTempSummaryFinalSDS" ("EVCId" ASC);

-- ----------------------------
-- Indexes structure for table EVCTempSummarySDS
-- ----------------------------
CREATE INDEX "main"."DisIdIndex"
ON "EVCTempSummarySDS" ("DisId" ASC);
CREATE INDEX "main"."TempSummaryEVCIdIndex"
ON "EVCTempSummarySDS" ("EVCId" ASC);
