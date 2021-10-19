-- ----------------------------
-- Table structure for DiscountGoodsNosale
-- ----------------------------
DROP TABLE IF EXISTS "main"."DiscountGoodsNosale";
CREATE TABLE "DiscountGoodsNosale" (
"uniqueId"  TEXT NOT NULL,
"BackOfficeId"  INTEGER,
"GoodsRef"  INTEGER,
"Status"  INTEGER,
"StartDate"  TEXT,
"EndDate"  TEXT,
"CustRef"  INTEGER,
"DCRef"  INTEGER,
"CustActRef"  INTEGER,
"CustCtgrRef"  INTEGER,
"CustLevelRef"  INTEGER,
"StateRef"  INTEGER,
"AreaRef"  INTEGER,
"CountyRef"  INTEGER,
PRIMARY KEY ("uniqueId")
);

-- ----------------------------
-- Table structure for DiscountGoodsFixUnit
-- ----------------------------
DROP TABLE IF EXISTS "main"."DiscountGoodsFixUnit";
CREATE TABLE "DiscountGoodsFixUnit" (
"uniqueId"  TEXT NOT NULL,
"GoodsRef"  INTEGER,
"UnitRef"  INTEGER,
"UnitName"  TEXT,
"Qty"  INTEGER,
PRIMARY KEY ("uniqueId")
);

-- ----------------------------
-- Table structure for DiscountDiscountGood
-- ----------------------------
DROP TABLE IF EXISTS "main"."DiscountDiscountGood";
CREATE TABLE "DiscountDiscountGood" (
"uniqueId"  TEXT NOT NULL,
"BackOfficeId"  INTEGER,
"DiscountRef"  TEXT,
"GoodsRef"  TEXT,
PRIMARY KEY ("uniqueId")
);

-- ----------------------------
-- Table structure for DiscountPaymentUsances
-- ----------------------------
DROP TABLE IF EXISTS "main"."DiscountPaymentUsances";
CREATE TABLE "DiscountPaymentUsances" (
"uniqueId"  TEXT NOT NULL,
"BackOfficeId"  INTEGER,
"Title"  TEXT,
"BuyTypeId"  INTEGER,
"DeferTo"  INTEGER,
"ClearTo"  INTEGER,
"Status"  INTEGER,
"IsCash"  INTEGER,
"ModifiedDateBeforeSend"  TEXT,
"UserRefBeforeSend"  INTEGER,
"AdvanceControl"  INTEGER,
PRIMARY KEY ("uniqueId")
);

-- ----------------------------
-- Table structure for DiscountRetSaleHdr
-- ----------------------------
DROP TABLE IF EXISTS "main"."DiscountRetSaleHdr";
CREATE TABLE "DiscountRetSaleHdr" (
"uniqueId"  TEXT NOT NULL,
"BackOfficeId"  INTEGER,
"RetSaleNo"  INTEGER,
"RetSaleDate"  TEXT,
"SaleDate"  TEXT,
"SaleRef"  INTEGER,
"VocherFlag"  INTEGER,
"HealthCode"  INTEGER,
"RetTypeCode"  INTEGER,
"RetCauseRef"  INTEGER,
"Dis1"  REAL,
"Dis2"  REAL,
"Dis3"  REAL,
"Add1"  REAL,
"Add2"  REAL,
"CancelFlag"  INTEGER,
"TotalAmount"  REAL,
"UserRef"  INTEGER,
"ChangeDate"  TEXT,
"BuyType"  INTEGER,
"AccYear"  INTEGER,
"DCRef"  INTEGER,
"StockDCRef"  INTEGER,
"CustRef"  INTEGER,
"DealerRef"  INTEGER,
"DCSaleOfficeRef"  INTEGER,
"RetOrderRef"  INTEGER,
"DistRef"  INTEGER,
"TSaleRef"  INTEGER,
"Comment"  TEXT,
"Tax"  REAL,
"Charge"  REAL,
"SupervisorRef"  INTEGER,
"OtherDiscount"  REAL,
"OtherAddition"  REAL,
PRIMARY KEY ("uniqueId")
);

-- ----------------------------
-- Table structure for DiscountRetSaleItem
-- ----------------------------
DROP TABLE IF EXISTS "main"."DiscountRetSaleItem";
CREATE TABLE "DiscountRetSaleItem" (
"uniqueId"  TEXT NOT NULL,
"BackOfficeId"  INTEGER,
"HdrRef"  INTEGER,
"RowOrder"  REAL,
"RetCauseRef"  INTEGER,
"GoodsRef"  INTEGER,
"UnitRef"  INTEGER,
"UnitCapasity"  INTEGER,
"UnitQty"  REAL,
"TotalQty"  REAL,
"UnitPrice"  REAL,
"AmountNut"  REAL,
"Discount"  REAL,
"Amount"  REAL,
"AccYear"  INTEGER,
"PrizeType"  INTEGER,
"SaleRef"  INTEGER,
"SupAmount"  REAL,
"AddAmount"  REAL,
"Dis1"  REAL,
"Dis2"  REAL,
"Dis3"  REAL,
"Add1"  REAL,
"Add2"  REAL,
"Tax"  REAL,
"Charge"  REAL,
"FreeReasonId"  INTEGER,
"OtherDiscount"  REAL,
"OtherAddition"  REAL,
PRIMARY KEY ("uniqueId")
);

-- ----------------------------
-- Add FreeReasonId, IsDeleted to DiscountCustomerOldInvoiceDetail
-- ----------------------------
alter table DiscountCustomerOldInvoiceDetail add column FreeReasonId INTEGER;
alter table DiscountCustomerOldInvoiceDetail add column IsDeleted INTEGER;

-- ----------------------------
-- Add UniqueId, StockDcRef to DiscountTourProduct
-- ----------------------------
alter table DiscountTourProduct add column UniqueId TEXT;
alter table DiscountTourProduct add column StockDcRef INTEGER;

-- ----------------------------
-- Add PackUnitRef, UnitRef to DiscountProduct
-- ----------------------------
alter table DiscountProduct add column PackUnitRef INTEGER;
alter table DiscountProduct add column UnitRef INTEGER;

-- ----------------------------
-- Add some of fields to DiscountSDS4_19
-- ----------------------------
alter table DiscountSDS4_19 add column CustRefList TEXT;
alter table DiscountSDS4_19 add column DiscountAreaRefList TEXT;
alter table DiscountSDS4_19 add column DiscountCustActRefList TEXT;
alter table DiscountSDS4_19 add column DiscountCustCtgrRefList TEXT;
alter table DiscountSDS4_19 add column DiscountCustGroupRefList TEXT;
alter table DiscountSDS4_19 add column DiscountCustLevelRefList TEXT;
alter table DiscountSDS4_19 add column DiscountDcRefList TEXT;
alter table DiscountSDS4_19 add column DiscountGoodRefList TEXT;
alter table DiscountSDS4_19 add column DiscountMainCustTypeRefList TEXT;
alter table DiscountSDS4_19 add column DiscountOrderNoList TEXT;
alter table DiscountSDS4_19 add column DiscountOrderRefList TEXT;
alter table DiscountSDS4_19 add column DiscountOrderTypeList TEXT;
alter table DiscountSDS4_19 add column DiscountPaymentUsanceRefList TEXT;
alter table DiscountSDS4_19 add column DiscountPayTypeList TEXT;
alter table DiscountSDS4_19 add column DiscountSaleOfficeRefList TEXT;
alter table DiscountSDS4_19 add column DiscountSaleZoneRefList TEXT;
alter table DiscountSDS4_19 add column DiscountStateRefList TEXT;
alter table DiscountSDS4_19 add column DiscountSubCustTypeRefList TEXT;
alter table DiscountSDS4_19 add column IsComplexCondition INTEGER;
alter table DiscountSDS4_19 add column PrizeSelectionList TEXT;
alter table DiscountSDS4_19 add column IsSelfPrize INTEGER;


-- ----------------------------
-- Add DiscountRef to DiscountGoodsPackageItem
-- ----------------------------
alter table DiscountGoodsPackageItem add column DiscountRef INTEGER;

-- ----------------------------
-- Add UniqueId to DiscountDisAcc
-- ----------------------------
alter table DiscountDisAcc add column UniqueId TEXT;
alter table DiscountDisAcc add column Code TEXT;
alter table DiscountDisAcc add column BackOfficeId INTEGER;
alter table DiscountDisAcc add column Name TEXT;