/*
Date: 2018-05-07 21:25:01
*/

PRAGMA foreign_keys = OFF;

-- ----------------------------
-- Table structure for SysConfig
-- ----------------------------
CREATE TABLE IF NOT EXISTS "SysConfig" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"Scope"  TEXT DEFAULT 0,
"Name"  TEXT,
"Value"  TEXT,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for User
-- ----------------------------
CREATE TABLE IF NOT EXISTS "User" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"BackOfficeId"  TEXT,
"UserName"  TEXT,
"Status"  INTEGER,
"IsSalesman"  INTEGER,
"IsDistributer"  INTEGER,
"IsCollector"  INTEGER,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for CustomerBoGroup
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerBoGroup";
CREATE TABLE "CustomerBoGroup" (
"Id"  INTEGER NOT NULL,
"ParentRef"  INTEGER,
"NLeft"  INTEGER,
"NRight"  INTEGER,
"NLevel"  INTEGER,
PRIMARY KEY ("Id")
);

-- ----------------------------
-- Table structure for DisSaleSDS
-- ----------------------------
DROP TABLE IF EXISTS "main"."DisSaleSDS";
CREATE TABLE "DisSaleSDS" (
"UniqueId"  TEXT NOT NULL,
"HdrRef"  INTEGER,
"ItemRef"  INTEGER,
"RowNo"  INTEGER,
"ItemType"  INTEGER,
"DisRef"  INTEGER,
"DisGroup"  INTEGER,
CPriceRef INTEGER,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for DisAcc
-- ----------------------------
DROP TABLE IF EXISTS "main"."DisAcc";
CREATE TABLE "DisAcc" (
"Id"  INTEGER NOT NULL,
"IsDiscount"  INTEGER,
"IsDefault"  INTEGER,
PRIMARY KEY ("Id")
);


-- ----------------------------
-- Table structure for ActionState
-- ----------------------------
DROP TABLE IF EXISTS "main"."ActionState";
CREATE TABLE "ActionState" (
"UniqueId"  TEXT NOT NULL,
"ActionId"  TEXT,
"SelectionId"  TEXT,
"Status"  INTEGER,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for Bank
-- ----------------------------
DROP TABLE IF EXISTS "main"."Bank";
CREATE TABLE "Bank" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"BankName"  TEXT NOT NULL,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for CancelInvoice
-- ----------------------------
DROP TABLE IF EXISTS "main"."CancelInvoice";
CREATE TABLE "CancelInvoice" (
  "TourUniqueId" TEXT,
  "CustomerUniqueId" TEXT,
  "Comment" TEXT,
  "Amount" TEXT,
  "UniqueId" text NOT NULL,
  PRIMARY KEY ("UniqueId")
);

-- ----------------------------
-- Table structure for Catalog
-- ----------------------------
DROP TABLE IF EXISTS "main"."Catalog";
CREATE TABLE "Catalog" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"CatalogName"  TEXT,
"ProductId"  TEXT COLLATE NOCASE ,
"CatalogId"  TEXT COLLATE NOCASE ,
"OrderOf"  INTEGER,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for City
-- ----------------------------
DROP TABLE IF EXISTS "main"."City";
CREATE TABLE "City" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"CityName"  TEXT,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for ContractPriceNestle
-- ----------------------------
DROP TABLE IF EXISTS "main"."ContractPriceNestle";
CREATE TABLE ContractPriceNestle
(
    UniqueId TEXT NOT NULL,
    ConditionType TEXT,
    CustomerHierarchyNumber TEXT,
    MaterialNumber TEXT,
    ConditionAmount REAL,
    ConditionUnit TEXT,
    ConditionPricingUnit TEXT,
    ConditionUnitofMeasure TEXT,
    ConditionValidOn INTEGER,
    ConditionValidTo INTEGER,
    PRIMARY KEY (UniqueId ASC)
);

-- ----------------------------
-- Table structure for ContractPriceSDS
-- ----------------------------
DROP TABLE IF EXISTS "main"."ContractPriceSDS";
CREATE TABLE "ContractPriceSDS" (
"UniqueId"  TEXT NOT NULL,
"CPriceType"  INTEGER,
"Code"  INTEGER,
"GoodsRef"  INTEGER,
"UnitRef"  INTEGER,
"CustRef"  TEXT,
"CustCtgrRef"  INTEGER,
"DCRef"  INTEGER,
"CustActRef"  INTEGER,
"MinQty"  REAL,
"MaxQty"  REAL,
"SalePrice"  REAL,
"StartDate"  INTEGER,
"EndDate"  INTEGER,
"BuyTypeRef"  INTEGER,
"UsanceDay"  INTEGER,
"StateRef"  INTEGER,
"CountyRef"  INTEGER,
"AreaRef"  INTEGER,
"Priority"  REAL,
"OrderTypeRef"  INTEGER,
"GoodsGroupRef"  INTEGER,
"MainTypeRef"  INTEGER,
"SubTypeRef"  INTEGER,
"CustLevelRef"  INTEGER,
"UserRef"  INTEGER,
"ModifiedDate"  INTEGER,
"DealerCtgrRef"  INTEGER,
"ModifiedDateBeforeSend"  INTEGER,
"UserRefBeforeSend"  INTEGER,
"BatchNoRef"  INTEGER,
"BatchNo"  TEXT(2048),
"BackOfficeId"  INTEGER,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for ContractPriceVnLite
-- ----------------------------
DROP TABLE IF EXISTS "main"."ContractPriceVnLite";
CREATE TABLE "ContractPriceVnLite" (
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
-- Table structure for Customer
-- ----------------------------
DROP TABLE IF EXISTS "main"."Customer";
CREATE TABLE "Customer" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"BackOfficeId"  TEXT,
"CustomerName"  TEXT,
"CustomerCode"  TEXT,
"Address"  TEXT,
"Phone"  TEXT,
"StoreName"  TEXT,
"Mobile"  TEXT,
"Longitude"  REAL,
"Latitude"  REAL,
"NationalCode"  TEXT(2048),
"IsActive"  INTEGER,
"CountyId"  TEXT COLLATE NOCASE ,
"CityRef"  INTEGER,
"CityId"  TEXT COLLATE NOCASE ,
"StateId"  TEXT COLLATE NOCASE ,
"StateRef"  INTEGER,
"CustomerLevelId"  TEXT COLLATE NOCASE ,
"CustomerActivityId"  TEXT COLLATE NOCASE ,
"CustomerCategoryId"  TEXT COLLATE NOCASE ,
"CustomerLevelRef"  INTEGER,
"CustomerActivityRef"  INTEGER,
"CustomerCategoryRef"  INTEGER,
"RemainDebit"  REAL,
"RemainCredit"  REAL,
"CustRemAmountForSaleOffice"  REAL,
"CustRemAmountAll"  REAL,
"CustomerRemain"  REAL,
"InitCredit"  REAL,
"InitDebit"  REAL,
"OpenInvoiceCount"  INTEGER,
"OpenInvoiceAmount"  REAL,
"OpenChequeCount"  INTEGER,
"OpenChequeAmount"  REAL,
"ReturnChequeCount"  INTEGER,
"ReturnChequeAmount"  REAL,
"checkCredit"  INTEGER,
"checkDebit"  INTEGER,
"rowIndex"  INTEGER,
"Alarm"  TEXT,
"EconomicCode"  TEXT(2048),
"IsNewCustomer"  INTEGER,
"SalePathRef"  INTEGER,
"SalePathNo"  INTEGER,
"SaleAreaRef"  INTEGER,
"SaleAreaNo"  INTEGER,
"SaleZoneRef"  INTEGER,
"SaleZoneNo"  INTEGER,
"DistPathRef"  INTEGER,
"DistPathNo"  INTEGER,
"DistAreaRef"  INTEGER,
"DistAreaNo"  INTEGER,
"DistZoneRef"  INTEGER,
"DistZoneNo"  INTEGER,
"CityCode"  INTEGER,
"CountyCode"  TEXT(2048),
"CountyRef"  INTEGER,
"CustCtgrCode"  TEXT(2048),
"CustActCode"  TEXT(2048),
"CustLevelCode"  TEXT(2048),
"CityArea"  TEXT,
"OwnerTypeRef"  INTEGER,
"OwnerTypeCode"  TEXT(2048),
"StateCode"  TEXT(2048),
"CenterId"  TEXT COLLATE NOCASE ,
"ZoneId"  TEXT COLLATE NOCASE ,
"AreaId"  TEXT COLLATE NOCASE ,
"DcCode"  TEXT(2048),
"DCRef"  INTEGER,
"CustomerSubGroup2Id"  TEXT COLLATE NOCASE ,
"CustomerSubGroup1Id"  TEXT COLLATE NOCASE ,
"CountChq"  INTEGER,
"AmountChq"  REAL,
"ErrorType"  INTEGER,
"ErrorMessage"  TEXT,
"CityZone"  TEXT,
"HasCancelOrder"  INTEGER,
"DCName"  TEXT,
"RealName"  TEXT(2048),
"SaleOfficeId"  TEXT COLLATE NOCASE ,
"SaleOfficeRef"  INTEGER,
"Barcode"  TEXT, PayableTypes INTEGER, [CustChequeRetQty]  INTEGER  NULL, [CustGroupRef]  INTEGER  NULL,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for CustomerActionTime
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerActionTime";
CREATE TABLE "CustomerActionTime" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"CustomerId" TEXT NOT NULL,
"Action"  INTEGER,
"Date"  INTEGER,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for CustomerActivity
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerActivity";
CREATE TABLE "CustomerActivity" (
"UniqueId"  TEXT NOT NULL,
"BackOfficeId"  INTEGER,
"CustomerActivityName"  TEXT,
PRIMARY KEY ("UniqueId")
);

-- ----------------------------
-- Table structure for CustomerCall
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerCall";
CREATE TABLE "CustomerCall"
(
    UniqueId TEXT NOT NULL COLLATE NOCASE,
    CustomerId TEXT NOT NULL COLLATE NOCASE,
    CallType TEXT NOT NULL,
    CreatedTime TEXT NOT NULL,
    UpdatedTime TEXT NOT NULL,
    ExtraField1 TEXT COLLATE NOCASE,
	ExtraField2 TEXT COLLATE NOCASE,
	ExtraField3 TEXT COLLATE NOCASE,
    ConfirmStatus INTEGER DEFAULT 0,
    TaskUniqueId TEXT,
    PRIMARY KEY (UniqueId ASC)
);

-- ----------------------------
-- Table structure for CustomerCallExtraData
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerCallExtraData";
CREATE TABLE "CustomerCallExtraData" (
  "PrintCounts" TEXT,
  "UniqueId" text NOT NULL,
  "CustomerId" text,
  PRIMARY KEY ("UniqueId")
);

-- ----------------------------
-- Table structure for CustomerCallOrder
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerCallOrder";
CREATE TABLE "CustomerCallOrder" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"RowNo"  INTEGER NOT NULL DEFAULT 0,
"CustomerUniqueId"  TEXT COLLATE NOCASE ,
"DistBackOfficeId"  INTEGER,
"DisType"  INTEGER,
"Comment"  TEXT(2048),
"LocalPaperNo"  TEXT,
"BackOfficeOrderNo"  TEXT(2048),
"SaleDate"  INTEGER,
"BackOfficeOrderId"  TEXT,
"BackOfficeOrderTypeId"  TEXT,
"OrderPaymentTypeUniqueId"  TEXT(100),
"RoundOrderAmount"  REAL DEFAULT 0,
"RoundOrderOtherDiscount"  REAL,
"RoundOrderDis1"  REAL DEFAULT 0,
"RoundOrderDis2"  REAL DEFAULT 0,
"RoundOrderDis3"  REAL,
"RoundOrderTax"  REAL,
"RoundOrderCharge"  REAL,
"RoundOrderAdd1"  REAL,
"RoundOrderAdd2"  REAL,
"BackOfficeInvoiceId"  INTEGER,
"BackOfficeInvoiceNo"  TEXT(100),
"RoundInvoiceAmount"  REAL,
"RoundInvoiceOtherDiscount"  REAL,
"RoundInvoiceTax"  REAL,
"RoundInvoiceCharge"  REAL,
"RoundInvoiceDis1"  REAL,
"RoundInvoiceDis2"  REAL,
"RoundInvoiceDis3"  REAL,
"RoundInvoiceAdd1"  REAL,
"RoundInvoiceAdd2"  REAL,
"InvoicePaymentTypeUniqueId"  TEXT(100),
"IsPromotion"  INTEGER DEFAULT 0,
"PromotionUniqueId"  TEXT,
"StockDCCodeSDS"  TEXT(2048),
"SupervisorRefSDS"  INTEGER,
"SupervisorCodeSDS"  TEXT(2048),
"DcCodeSDS"  TEXT(2048),
"SaleIdSDS"  TEXT,
"SaleNoSDS"  INTEGER,
"DealerRefSDS"  INTEGER,
"DealerCodeSDS"  TEXT(2048),
"OrderNoSDS"  TEXT(2048),
"AccYearSDS"  INTEGER,
"DCRefSDS"  INTEGER,
"SaleOfficeRefSDS"  INTEGER,
"StockDCRefSDS"  INTEGER,
"CallActionStatusUniqueId"  TEXT,
"SubSystemTypeUniqueId"  TEXT,
"OrderTypeUniqueId"  TEXT, PriceClassId TEXT, DeliveryDate INTEGER,
PRIMARY KEY ("UniqueId" ASC),
CONSTRAINT "fkey0" FOREIGN KEY ("CustomerUniqueId") REFERENCES "Customer" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT "fkey1" FOREIGN KEY ("PromotionUniqueId") REFERENCES "CustomerCallOrder" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for CustomerCallOrderLines
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerCallOrderLines";
CREATE TABLE "CustomerCallOrderLines"
(
    UniqueId TEXT NOT NULL COLLATE NOCASE,
    OrderUniqueId TEXT NOT NULL,
    ProductUniqueId TEXT NOT NULL,
    SortId INTEGER,
    IsRequestFreeItem INTEGER,
    RequestBulkQty REAL,
    RequestBulkQtyUnitUniqueId TEXT,
    RequestAdd1Amount REAL,
    RequestAdd2Amount REAL,
    RequestTaxAmount REAL,
    RequestChargeAmount REAL,
    RequestDis1Amount REAL,
    RequestDis2Amount REAL,
    RequestDis3Amount REAL,
    RequestOtherDiscountAmount REAL,
    InvoiceBulkQty REAL,
    InvoiceBulkQtyUnitUniqueId TEXT,
    InvoiceAmount REAL,
    InvoiceAdd1Amount REAL,
    InvoiceAdd2Amount REAL,
    InvoiceTaxAmount REAL,
    InvoiceChargeAmount REAL,
    InvoiceOtherDiscountAmount REAL,
    InvoiceDis1Amount REAL,
    InvoiceDis2Amount REAL,
    InvoiceDis3Amount REAL,
    EVCId TEXT(2048),
    FreeReasonId TEXT,
    DiscountRef INTEGER,
    DiscountId TEXT COLLATE NOCASE,
    IsPromoLine INTEGER,
    PRIMARY KEY (UniqueId ASC),
    CONSTRAINT fkey0 FOREIGN KEY (OrderUniqueId) REFERENCES CustomerCallOrder(UniqueId) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fkey1 FOREIGN KEY (ProductUniqueId) REFERENCES Product(UniqueId) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fkey2 FOREIGN KEY (RequestBulkQtyUnitUniqueId) REFERENCES ProductUnit(UniqueId) ON DELETE SET NULL ON UPDATE SET NULL,
    CONSTRAINT fkey3 FOREIGN KEY (InvoiceBulkQtyUnitUniqueId) REFERENCES ProductUnit(UniqueId) ON DELETE SET NULL ON UPDATE SET NULL,
    CONSTRAINT fkey4 FOREIGN KEY (FreeReasonId) REFERENCES FreeReason(UniqueId) ON DELETE SET NULL ON UPDATE SET NULL
);

-- ----------------------------
-- Table structure for CustomerCallOrderLinesInvoiceQtyDetail
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerCallOrderLinesInvoiceQtyDetail";
CREATE TABLE "CustomerCallOrderLinesInvoiceQtyDetail" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"OrderLineUniqueId"  TEXT,
"ProductUnitId"  TEXT,
"Qty"  REAL,
PRIMARY KEY ("UniqueId" ASC),
CONSTRAINT "fkey1" FOREIGN KEY ("OrderLineUniqueId") REFERENCES "CustomerCallOrderLines" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT "fkey2" FOREIGN KEY ("ProductUnitId") REFERENCES "ProductUnit" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for CustomerCallOrderLinesItemStatutes
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerCallOrderLinesItemStatutes";
CREATE TABLE "CustomerCallOrderLinesItemStatutes" (
"UniqueId"  TEXT,
"ProductId"  TEXT,
"RowOrder"  INTEGER,
"DisRef"  TEXT,
"DisGroup"  INTEGER,
"AddAmount"  REAL,
"SupAmount"  REAL,
"Discount"  REAL,
"EvcId"  TEXT(2048),
CONSTRAINT "fkey0" FOREIGN KEY ("ProductId") REFERENCES "Product" ("UniqueId")
);

-- ----------------------------
-- Table structure for CustomerCallOrderLinesOrderQtyDetail
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerCallOrderLinesOrderQtyDetail";
CREATE TABLE "CustomerCallOrderLinesOrderQtyDetail" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"OrderLineUniqueId"  TEXT,
"ProductUnitId"  TEXT,
"Qty"  REAL,
PRIMARY KEY ("UniqueId" ASC),
CONSTRAINT "fkey1" FOREIGN KEY ("ProductUnitId") REFERENCES "ProductUnit" ("UniqueId"),
CONSTRAINT "fkey2" FOREIGN KEY ("OrderLineUniqueId") REFERENCES "CustomerCallOrderLines" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for CustomerCallReturn
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerCallReturn";
CREATE TABLE "CustomerCallReturn" (
"UniqueId"  TEXT(100) NOT NULL COLLATE NOCASE ,
"CustomerUniqueId"  TEXT(100),
"ReturnTypeUniqueId"  TEXT,
"PersonnelUniqueId"  TEXT,
"LocalPaperNo"  TEXT(20),
"BackOfficeDistId"  TEXT(100),
"BackOfficeInvoiceId"  TEXT,
"BackOfficeInvoiceNo"  INTEGER,
"BackOfficeInvoiceDate"  INTEGER,
"ReturnRequestBackOfficeId"  INTEGER,
"ReturnRequestBackOfficeDate"  INTEGER,
"ReturnRequestBackOfficeNo"  TEXT(20),
"ReturnReasonUniqueId"  TEXT,
"CallActionStatusUniqueId"  TEXT(100),
"ReturnRequestRejectReasonUniqueId"  TEXT(100),
"TotalRequestAmount"  REAL,
"TotalRequestTax"  REAL,
"TotalRequestCharge"  REAL,
"TotalRequestDiscount"  REAL,
"TotalReturnAmount"  REAL,
"TotalReturnOtherDiscount"  REAL,
"TotalReturnDis1"  REAL,
"TotalReturnDis2"  REAL,
"TotalReturnDis3"  REAL,
"TotalReturnCharge"  REAL,
"TotalReturnTax"  REAL,
"TotalReturnAdd1"  REAL,
"TotalReturnAdd2"  REAL,
"Comment"  TEXT(2048),
"DCRefSDS"  INTEGER,
"SaleOfficeRefSDS"  INTEGER,
PRIMARY KEY ("UniqueId" ASC),
CONSTRAINT "fkey1" FOREIGN KEY ("CustomerUniqueId") REFERENCES "Customer" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for CustomerCallReturnLines
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerCallReturnLines";
CREATE TABLE "CustomerCallReturnLines" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"ReturnUniqueId"  TEXT(2048) COLLATE NOCASE ,
"ProductUniqueId"  TEXT COLLATE NOCASE ,
"IsFreeItem"  INTEGER,
"RequestUnitPrice"  REAL,
"TotalRequestAdd1Amount"  REAL,
"TotalRequestAdd2Amount"  REAL,
"TotalRequestDiscount"  REAL,
"TotalRequestTax"  REAL,
"TotalRequestCharge"  REAL,
"TotalRequestNetAmount"  REAL,
"SortId"  INTEGER,
"IndexInfo"  INTEGER,
"Weight"  REAL,
"TotalReturnAmount"  REAL,
"TotalReturnAdd1Amount"  REAL,
"TotalReturnAdd2Amount"  REAL,
"TotalReturnDiscount"  REAL,
"TotalReturnTax"  REAL,
"TotalReturnCharge"  REAL,
"TotalReturnNetAmount"  REAL,
"ReturnProductTypeId"  TEXT,
"ReferenceId"  TEXT,
"ReferenceNo"  INTEGER,
"ReturnReasonId"  TEXT,
"ReferenceQty"  REAL,
"TotalReturnAddAmount"  REAL,
"TotalReturnSupAmount"  REAL,
"TotalReturnDis1"  REAL,
"TotalReturnDis2"  REAL,
"TotalReturnDis3"  REAL,
"ReferenceDate"  INTEGER,
"CurrentType"  TEXT,
"CurrentQty"  REAL,
"RequestBulkQty"  REAL,
"ReturnBulkQty"  REAL,
"RequestBulkUnitId"  TEXT,
"ReturnBulkUnitId"  TEXT,
IsPromoLine INTEGER,
StockId TEXT,
PRIMARY KEY ("UniqueId" ASC),
CONSTRAINT "fkey0" FOREIGN KEY ("RequestBulkUnitId") REFERENCES "ProductUnit" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT "fkey1" FOREIGN KEY ("ReturnBulkUnitId") REFERENCES "ProductUnit" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT "fkey3" FOREIGN KEY ("ProductUniqueId") REFERENCES "Product" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT "fkey4" FOREIGN KEY ("ReturnUniqueId") REFERENCES "CustomerCallReturn" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for CustomerCallReturnLinesQtyDetail
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerCallReturnLinesQtyDetail";
CREATE TABLE "CustomerCallReturnLinesQtyDetail" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"ReturnLineUniqueId"  TEXT COLLATE NOCASE ,
"ProductUnitId"  TEXT COLLATE NOCASE ,
"Qty"  REAL,
PRIMARY KEY ("UniqueId" ASC),
CONSTRAINT "fkey1" FOREIGN KEY ("ProductUnitId") REFERENCES "ProductUnit" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT "fkey2" FOREIGN KEY ("ReturnLineUniqueId") REFERENCES "CustomerCallReturnLines" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for CustomerCallReturnLinesWithPromo
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerCallReturnLinesWithPromo";
CREATE TABLE "CustomerCallReturnLinesWithPromo"
(
    UniqueId TEXT NOT NULL,
    ProductUniqueId TEXT,
    IsFreeItem INTEGER,
    RequestAmount REAL,
    RequestAdd1Amount REAL,
    RequestAdd2Amount REAL,
    RequestOtherDiscount REAL,
    RequestTax REAL,
    RequestCharge REAL,
    SortId INTEGER,
    PriceId TEXT,
    UnitPrice REAL,
    ReturnQty REAL,
    RequestBulkQty REAL,
    RequestBulkUnitUniqueId TEXT,
    ReturnBulkQty REAL,
    ReturnBulkUnitUniqueId TEXT,
    ReturnAmount REAL,
    ReturnAdd1Amount REAL,
    ReturnAdd2Amount REAL,
    ReturnDiscount REAL,
    ReturnTax REAL,
    ReturnCharge REAL,
    Comment TEXT(2048),
    ReturnProductTypeId TEXT,
    ReferenceId TEXT,
    ReferenceNo INTEGER,
    ReturnReasonUniqueId TEXT,
    ReferenceQty REAL,
    ReturnDis1Amount REAL,
    ReturnDis2Amount REAL,
    ReturnDis3Amount REAL,
    TotalReturnAddAmount REAL,
    TotalReturnSupAmount REAL,
    ReferenceDate INTEGER,
    PRIMARY KEY (UniqueId ASC),
    CONSTRAINT fkey0 FOREIGN KEY (RequestBulkUnitUniqueId) REFERENCES ProductUnit(UniqueId) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fkey1 FOREIGN KEY (ReturnBulkUnitUniqueId) REFERENCES ProductUnit(UniqueId) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fkey2 FOREIGN KEY (ReturnReasonUniqueId) REFERENCES ReturnReason(UniqueId) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fkey4 FOREIGN KEY (ProductUniqueId) REFERENCES Product(UniqueId) ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for CustomerCardex
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerCardex";
CREATE TABLE "CustomerCardex" (
"UniqueId"  TEXT NOT NULL,
"CustomerUniqueId" TEXT,
"RowId" INTEGER,
"Type"  TEXT(2048),
"VoucherNo"  TEXT(2048),
"Date"  INTEGER,
"BedAmount"  REAL,
"BesAmount"  REAL,
"RemainAmount"  REAL,
"VoucherDate"  TEXT,
"VoucherTypeName"  TEXT,
"NotDueDate" TEXT,
"NotDueDateMiladi" INTEGER,
PRIMARY KEY ("UniqueId" ASC),
CONSTRAINT "fkey0" FOREIGN KEY ("CustomerUniqueId") REFERENCES "Customer" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for CustomerCategory
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerCategory";
CREATE TABLE "CustomerCategory" (
"UniqueId"  TEXT NOT NULL,
"BackOfficeId"  INTEGER,
"CustomerCategoryName"  TEXT,
PRIMARY KEY ("UniqueId")
);

-- ----------------------------
-- Table structure for CustomerEmphaticProduct
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerEmphaticProduct";
CREATE TABLE "CustomerEmphaticProduct" (
"ProductId"  TEXT COLLATE NOCASE ,
"ProductCount"  INTEGER,
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"Type"  INTEGER,
"CustomerId"  TEXT COLLATE NOCASE ,
"WarningDate"  INTEGER,
"DangerDate"  INTEGER,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for CustomerInventory
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerInventory";
CREATE TABLE "CustomerInventory" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"ProductId"  TEXT COLLATE NOCASE ,
"CustomerId"  TEXT COLLATE NOCASE ,
"IsAvailable"  INTEGER,
"IsSold"  INTEGER,
PRIMARY KEY ("UniqueId" ASC),
CONSTRAINT "fkey0" FOREIGN KEY ("ProductId") REFERENCES "Product" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT "fkey1" FOREIGN KEY ("CustomerId") REFERENCES "Customer" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for CustomerInventoryQty
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerInventoryQty";
CREATE TABLE "CustomerInventoryQty" (
"UniqueId"  TEXT COLLATE NOCASE ,
"CustomerInventoryId"  TEXT COLLATE NOCASE ,
"Qty"  REAL,
"ProductUnitId"  TEXT COLLATE NOCASE ,
CONSTRAINT "fkey0" FOREIGN KEY ("CustomerInventoryId") REFERENCES "CustomerInventory" ("UniqueId") ON DELETE SET NULL ON UPDATE SET NULL,
CONSTRAINT "fkey1" FOREIGN KEY ("ProductUnitId") REFERENCES "ProductUnit" ("UniqueId") ON DELETE SET NULL ON UPDATE SET NULL
);

-- ----------------------------
-- Table structure for CustomerInvoicePayment
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerInvoicePayment";
CREATE TABLE CustomerInvoicePayment
(
    UniqueId TEXT NOT NULL,
    CustomerId TEXT,
    InvoiceId TEXT,
    PRIMARY KEY (UniqueId),
    FOREIGN KEY (CustomerId) REFERENCES Customer(UniqueId) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (InvoiceId) REFERENCES CustomerOldInvoiceHeader(UniqueId) ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for CustomerLevel
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerLevel";
CREATE TABLE "CustomerLevel" (
"UniqueId"  TEXT NOT NULL,
"BackOfficeId"  INTEGER,
"CustomerLevelName"  TEXT,
PRIMARY KEY ("UniqueId")
);

-- ----------------------------
-- Table structure for CustomerMainSubType
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerMainSubType";
CREATE TABLE "CustomerMainSubType" (
"UniqueId"  TEXT NOT NULL,
"Id"  INTEGER NOT NULL,
"CustRef"  INTEGER,
"MainTypeRef"  INTEGER,
"SubTypeRef"  INTEGER,
PRIMARY KEY ("UniqueId")
);

-- ----------------------------
-- Table structure for CustomerOldInvoiceDetail
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerOldInvoiceDetail";
CREATE TABLE "CustomerOldInvoiceDetail" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"SaleId"  TEXT,
"ProductId"  TEXT,
"UnitCapasity"  INTEGER,
"UnitRef"  INTEGER,
"UnitQty"  REAL,
"TotalQty"  REAL,
"UnitName"  TEXT(2048),
"UnitPrice"  REAL,
"PriceId"  TEXT,
"CPriceRef"  INTEGER,
"Amount"  REAL,
"AmountNut"  REAL,
"Discount"  REAL,
"PrizeType"  INTEGER,
"SupAmount"  REAL,
"AddAmount"  REAL,
"CustPrice"  REAL,
"UserPrice"  REAL,
"Charge"  REAL,
"Tax"  REAL,
"RowOrder"  INTEGER,
"ProductCtgrId"  TEXT,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for CustomerOldInvoiceDisSale
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerOldInvoiceDisSale";
CREATE TABLE CustomerOldInvoiceDisSale
(
    UniqueId TEXT NOT NULL,
    BackOfficeId INTEGER,
    HdrRef INTEGER,
    ItemRef INTEGER,
    RowNo REAL,
    ItemType INTEGER,
    DisRef INTEGER,
    DisGroup INTEGER,
    Discount REAL,
    AddAmount REAL,
    PRIMARY KEY (UniqueId)
);

-- ----------------------------
-- Table structure for CustomerOldInvoiceHeader
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerOldInvoiceHeader";
CREATE TABLE "CustomerOldInvoiceHeader" (
	"UniqueId" TEXT NOT NULL COLLATE NOCASE,
	"CustomerId" NVARCHAR (100),
	"PersonnelId" NVARCHAR (100),
	"StockBackOfficeId" INTEGER,
	"Amount" REAL,
	"Dis1Amount" REAL,
	"Dis2Amount" REAL,
	"Dis3Amount" REAL,
	"Add1Amount" REAL,
	"Add2Amount" REAL,
	"ChargeAmount" REAL,
	"TaxAmount" REAL,
	"OrderBackOfficeTypeId" INTEGER,
	"PaymentTypeOrderUniqueId" NVARCHAR (100),
	"PaymentUsanceRef" INTEGER,
	"OrderBackOfficeId" INTEGER,
	"PayAmount" REAL,
	"TotalAmount" REAL,
	"SalePDate" NVARCHAR (20),
	"SaleDate" NVARCHAR (40),
	"SaleNo" NVARCHAR (50),
	"SaleRef" NVARCHAR,
	"SaleVocherNo" NVARCHAR (50),
	"GoodsGroupTreeXML" NVARCHAR (2048),
	"GoodsDetailXML" NVARCHAR (2048),
	"GoodsMainSubTypeDetailXML" NVARCHAR (2048),
	"CustCtgrRef" INTEGER,
	"CustActRef" INTEGER,
	"CustLevelRef" INTEGER,
	"SaleOfficeRef" INTEGER,
	"OrderType" INTEGER,
	"BuyTypeId" INTEGER,
	"DCRef" INTEGER,
	"DisType" INTEGER,
	"AccYear" INTEGER,
	"DCSaleOfficeRef" INTEGER,
	"StockDCCode" NVARCHAR (20),
	"DealerCode" NVARCHAR (20),
	"SupervisorCode" NVARCHAR (20),
	"OrderId" INTEGER,
	"OrderNo" NVARCHAR (50),
	"DealerRef" INTEGER,
	"StockId" TEXT COLLATE NOCASE,
	"CustRef" INTEGER,
	"DealerId" TEXT COLLATE NOCASE,
	"CashAmount" REAL,
	"ChequeAmount" REAL,
	"BuyTypeRef" INTEGER,
	"OrderRef" INTEGER, DealerName Text,
	PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for CustomerOrderType
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerOrderType";
CREATE TABLE "CustomerOrderType" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"BackOfficeId"  INTEGER,
"OrderTypeName"  TEXT,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for CustomerPrice
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerPrice";
CREATE TABLE "CustomerPrice" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"CustomerUniqueId"  TEXT COLLATE NOCASE ,
"ProductUniqueId"  TEXT COLLATE NOCASE ,
"UserPrice"  REAL,
"PriceId"  INTEGER,
"Price"  REAL,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for CustomerProductOrderQtyHistory
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerProductOrderQtyHistory";
CREATE TABLE "CustomerProductOrderQtyHistory" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"ProductId"  TEXT NOT NULL COLLATE NOCASE ,
"DangerQty"  INTEGER,
"WarningQty"  INTEGER,
"CustomerId"  TEXT COLLATE NOCASE ,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for CustomerProductPrize
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerProductPrize";
CREATE TABLE "CustomerProductPrize" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"ProductId"  TEXT COLLATE NOCASE ,
"Comment"  TEXT,
"CustomerId"  TEXT COLLATE NOCASE ,
PRIMARY KEY ("UniqueId" ASC),
CONSTRAINT "fkey0" FOREIGN KEY ("ProductId") REFERENCES "Product" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT "fkey1" FOREIGN KEY ("CustomerId") REFERENCES "Customer" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for CustomerTotalProductSale
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerTotalProductSale";
CREATE TABLE "CustomerTotalProductSale" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"TotalQty"  REAL,
"ProductId"  TEXT COLLATE NOCASE ,
"CustomerId"  TEXT COLLATE NOCASE ,
"InvoiceCount"  INTEGER,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for DealerPaymentType
-- ----------------------------
DROP TABLE IF EXISTS "main"."DealerPaymentType";
CREATE TABLE "DealerPaymentType" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"paymentTypeUniqueId"  TEXT
);

-- ----------------------------
-- Table structure for DiscountCondition
-- ----------------------------
DROP TABLE IF EXISTS "main"."DiscountCondition";
CREATE TABLE "DiscountCondition" (
"Id"  INTEGER,
"DiscountRef"  INTEGER,
"DCRef"  INTEGER,
"CustCtgrRef"  INTEGER,
"CustActRef"  INTEGER,
"CustLevelRef"  INTEGER,
"PayType"  INTEGER,
"PaymentUsanceRef"  INTEGER,
"OrderType"  INTEGER,
"SaleOfficeRef"  INTEGER,
"CustGroupRef"  INTEGER,
"CustRef"  INTEGER,
"OrderNo"  INTEGER,
"StateRef"  INTEGER,
"AreaRef"  INTEGER,
"SaleZoneRef"  INTEGER,
"MainCustTypeRef"  INTEGER,
"SubCustTypeRef"  INTEGER,
"UniqueId"  TEXT,
PRIMARY KEY ("UniqueId")
);

-- ----------------------------
-- Table structure for DiscountItemCount
-- ----------------------------
DROP TABLE IF EXISTS "main"."DiscountItemCount";
CREATE TABLE "DiscountItemCount" (
"GoodsRef"  INTEGER,
"DisRef"  INTEGER,
"UniqueId"  TEXT NOT NULL,
PRIMARY KEY ("UniqueId")
);

-- ----------------------------
-- Table structure for DiscountSDS
-- ----------------------------
DROP TABLE IF EXISTS "main"."DiscountSDS";
CREATE TABLE "DiscountSDS" (
"UniqueId"  TEXT NOT NULL,
"DisGroup"  INTEGER,
"Priority"  INTEGER,
"Code"  INTEGER,
"DisType"  INTEGER,
"PrizeType"  INTEGER,
"StartDate"  NVARCHAR(20),
"EndDate"  NVARCHAR(20),
"MinQty"  REAL,
"MaxQty"  REAL,
"QtyUnit"  INTEGER,
"MinAmount"  REAL,
"MaxAmount"  REAL,
"PrizeQty"  REAL,
"PrizeRef"  INTEGER,
"PrizeStep"  INTEGER,
"PrizeUnit"  REAL,
"DisPerc"  REAL,
"DisPrice"  REAL,
"GoodsRef"  INTEGER,
"DCRef"  INTEGER,
"CustActRef"  INTEGER,
"CustCtgrRef"  INTEGER,
"StateRef"  INTEGER,
"AreaRef"  INTEGER,
"GoodsCtgrRef"  INTEGER,
"CustRef"  INTEGER,
"DisAccRef"  INTEGER,
"PayType"  INTEGER,
"OrderType"  INTEGER,
"SupPerc"  REAL,
"AddPerc"  REAL,
"SaleOfficeRef"  INTEGER,
"Comment"  TEXT(2048),
"ApplyInGroup"  INTEGER,
"CalcPriority"  INTEGER,
"CalcMethod"  INTEGER,
"CustLevelRef"  INTEGER,
"GoodsGroupRef"  INTEGER,
"ManufacturerRef"  INTEGER,
"SaleZoneRef"  INTEGER,
"MainTypeRef"  INTEGER,
"SubTypeRef"  INTEGER,
"BrandRef"  INTEGER,
"MinWeight"  REAL,
"MaxWeight"  REAL,
"UserRef"  INTEGER,
"ModifiedDate"  INTEGER,
"PrizeIncluded"  INTEGER,
"ModifiedDateBeforeSend"  INTEGER,
"UserRefBeforeSend"  INTEGER,
"MinRowsCount"  INTEGER,
"MinCustChequeRetQty"  REAL,
"MaxCustChequeRetQty"  REAL,
"MinCustRemAmount"  REAL,
"MaxCustRemAmount"  REAL,
"MaxRowsCount"  INTEGER,
"IsActive"  INTEGER,
"OrderNo"  INTEGER,
"PrizeStepType"  INTEGER,
"IsPrize"  INTEGER,
"SqlCondition"  TEXT(2048),
"PrizePackageRef"  INTEGER,
"CustomerSubGroup1IdVnLite"  INTEGER,
"CustomerSubGroup2IdVnLite"  INTEGER,
"ProductSubGroup1IdVnLite"  INTEGER,
"ProductSubGroup2IdVnLite"  INTEGER,
"DetailIsActive"  INTEGER,
"DetailPriority"  INTEGER,
"PromotionDetailCustomerGroupId"  INTEGER,
"PromotionDetailId"  INTEGER,
"PromotionDetailCustomerId"  INTEGER,
"ReduceOfQty"  REAL,
"HasAdvanceCondition"  INTEGER,
"BackOfficeId"  INTEGER,
"TotalMinAmount"  DECIMAL,
"TotalMaxAmount"  DECIMAL,
"TotalMinRowCount"  INTEGER,
"TotalMaxRowCount"  INTEGER,
"MixCondition"  INTEGER,
"PrizeStepUnit"  INTEGER,
"TotalDiscount"  DECIMAL,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for DiscountVnLt
-- ----------------------------
DROP TABLE IF EXISTS "main"."DiscountVnLt";
CREATE TABLE "DiscountVnLt" (
"UniqueId"  TEXT NOT NULL,
"DisGroup"  INTEGER,
"PromotionId"  INTEGER,
"BackOfficeId"  NVARCHAR(50),
"Priority"  INTEGER,
"StartDate"  NVARCHAR(20),
"EndDate"  NVARCHAR(20),
"Comment"  TEXT(2048),
"MinQty"  REAL,
"MaxQty"  REAL,
"MinAmount"  REAL,
"MaxAmount"  REAL,
"MinRowCount"  INTEGER,
"MaxRowCount"  INTEGER,
"MinWeight"  REAL,
"MaxWeight"  REAL,
"PrizeQty"  REAL,
"AddPerc"  REAL,
"DiscountPerc"  REAL,
"IsPrize"  INTEGER,
"PrizeRef"  INTEGER,
"PrizeStep"  INTEGER,
"PromotionTypeId"  INTEGER,
"ManufacturerId"  INTEGER,
"ProductSubGroup1Id"  INTEGER,
"ProductSubGroup2Id"  INTEGER,
"CustomerSubGroup1Id"  INTEGER,
"CustomerSubGroup2Id"  INTEGER,
"ReduceOfQty"  REAL,
"ProductId"  INTEGER,
"DetailIsActive"  INTEGER,
"DetailPriority"  INTEGER,
"ProductGroupId"  INTEGER,
"PrizeProductId"  INTEGER,
"CustomerId"  INTEGER,
"CustomerGroupId"  INTEGER,
"PromotionDetailCustomerGroupId"  INTEGER,
"PromotionDetailId"  INTEGER,
"PromotionDetailCustomerId"  INTEGER,
"CenterId"  INTEGER,
"CalcPriority"  INTEGER,
"PayTypeId"  INTEGER,
"PromotionCalcBaseId"  INTEGER,
"DiscountAmount"  REAL,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for DisSalePrizePackageSDS
-- ----------------------------
DROP TABLE IF EXISTS "main"."DisSalePrizePackageSDS";
CREATE TABLE "DisSalePrizePackageSDS" (
"UniqueId"  TEXT NOT NULL,
"SaleRef"  INTEGER,
"DiscountRef"  INTEGER,
"MainGoodsPackageItemRef"  INTEGER,
"ReplaceGoodsPackageItemRef"  INTEGER,
"PrizeCount"  INTEGER,
"PrizeQty"  INTEGER,
"BackOfficeId"  INTEGER,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for DistributionCustomerCall
-- ----------------------------
DROP TABLE IF EXISTS "main"."DistributionCustomerCall";
CREATE TABLE "DistributionCustomerCall" (
"UniqueId"  TEXT NOT NULL,
"CustomerUniqueId"  TEXT,
"DistributionUniqueId"  TEXT,
"DistributionRef"  TEXT,
"DistributionNo"  TEXT,
"DistributionPDate"  TEXT,
"DistributionDate"  INTEGER,
PRIMARY KEY ("UniqueId")
);

-- ----------------------------
-- Table structure for EmphaticProduct
-- ----------------------------
DROP TABLE IF EXISTS "main"."EmphaticProduct";
CREATE TABLE "EmphaticProduct" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"FromDate"  TEXT,
"ToDate"  TEXT,
"EmphasisProductErrorTypeId"  TEXT,
"DcId"  TEXT,
"SaleZoneId"  TEXT COLLATE NOCASE ,
"StateId"  TEXT COLLATE NOCASE ,
"CityId"  TEXT COLLATE NOCASE ,
"CustomerActivityId"  TEXT COLLATE NOCASE ,
"CustomerCategoryId"  TEXT COLLATE NOCASE ,
"CustomerLevelId"  TEXT COLLATE NOCASE ,
"SaleOfficeId"  TEXT COLLATE NOCASE ,
"ManufacturerId"  TEXT COLLATE NOCASE ,
"WarningDay"  INTEGER DEFAULT 0,
"DangerDay"  INTEGER DEFAULT 0,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for EmphaticProductCount
-- ----------------------------
DROP TABLE IF EXISTS "main"."EmphaticProductCount";
CREATE TABLE "EmphaticProductCount" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"ProductId"  TEXT COLLATE NOCASE ,
"ProductCount"  INTEGER,
"RuleId"  TEXT NOT NULL,
"ProductName"  TEXT,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for EvcStatuteProduct
-- ----------------------------
DROP TABLE IF EXISTS "main"."EvcStatuteProduct";
CREATE TABLE "EvcStatuteProduct" (
"UniqueId"  TEXT NOT NULL,
"TemplateId"  TEXT,
"ProductUniqueId"  TEXT,
"Description"  TEXT,
PRIMARY KEY ("UniqueId" ASC),
CONSTRAINT "fkey0" FOREIGN KEY ("TemplateId") REFERENCES "EvcStatuteTemplate" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY ("ProductUniqueId") REFERENCES "Product" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for EvcStatuteProductGroup
-- ----------------------------
DROP TABLE IF EXISTS "main"."EvcStatuteProductGroup";
CREATE TABLE "EvcStatuteProductGroup" (
"UniqueId"  TEXT NOT NULL,
"TemplateId"  TEXT,
"ProductGroupUniqueId"  TEXT,
"Description"  TEXT,
PRIMARY KEY ("UniqueId"),
FOREIGN KEY ("TemplateId") REFERENCES "EvcStatuteTemplate" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY ("ProductGroupUniqueId") REFERENCES "ProductGroup" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for EvcStatuteTemplate
-- ----------------------------
DROP TABLE IF EXISTS "main"."EvcStatuteTemplate";
CREATE TABLE "EvcStatuteTemplate" (
"UniqueId"  TEXT NOT NULL,
"Title"  TEXT,
"Description"  TEXT,
"FromDate"  INTEGER,
"ToDate"  INTEGER,
"CenterUniqueIds"  TEXT,
"SaleZoneUniqueIds"  TEXT,
"StateUniqueIds"  TEXT,
"CityUniqueIds"  TEXT,
"CustomerActivityUniqueIds"  TEXT,
"CustomerCategoryUniqueIds"  TEXT,
"CustomerLevelUniqueIds"  TEXT,
"SaleOfficeUniqueIds"  TEXT,
""  TEXT,
PRIMARY KEY ("UniqueId")
);

-- ----------------------------
-- Table structure for FreeReason
-- ----------------------------
DROP TABLE IF EXISTS "main"."FreeReason";
CREATE TABLE "FreeReason" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"FreeReasonName"  TEXT(2048),
"FreeReasonCode"  TEXT(2048),
"PrintTitle"  TEXT(2048),
"CalcPriceType"  INTEGER,
"DisAccTypeid"  INTEGER,
"BackOfficeId"  INTEGER,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for GoodsPackage
-- ----------------------------
DROP TABLE IF EXISTS "main"."GoodsPackage";
CREATE TABLE "GoodsPackage" (
"UniqueId"  TEXT NOT NULL,
"DiscountRef"  INTEGER,
"BackofficeId"  INTEGER,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for GoodsPackageItem
-- ----------------------------
DROP TABLE IF EXISTS "main"."GoodsPackageItem";
CREATE TABLE "GoodsPackageItem" (
"UniqueId"  TEXT NOT NULL,
"GoodsPackageRef"  INTEGER,
"GoodsRef"  INTEGER,
"UnitQty"  REAL,
"UnitRef"  INTEGER,
"TotalQty"  REAL,
"ReplaceGoodsRef"  INTEGER,
"PrizePriority"  REAL,
"BackofficeId"  INTEGER,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for Location
-- ----------------------------
DROP TABLE IF EXISTS "main"."Location";
CREATE TABLE "Location" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"Longitude"  REAL,
"Latitude"  REAL,
"Accuracy"  REAL,
"Speed"  REAL,
"Date"  INTEGER,
"EventType"  TEXT,
"Event"  TEXT,
"IsSend"  INTEGER,
"LastRetryTime"  INTEGER,
"Tracking"  INTEGER,
"Address"  TEXT, ActivityType INTEGER, IsImportant INTEGER, TourId TEXT, TourRef INTEGER, CompanyPersonnelId TEXT, CompanyPersonnelName TEXT,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for NoSaleReason
-- ----------------------------
DROP TABLE IF EXISTS "main"."NoSaleReason";
CREATE TABLE "NoSaleReason" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"NoSaleReasonName"  TEXT(2048),
"NoSaleReasonTypeId"  INTEGER,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for OnHandQty
-- ----------------------------
DROP TABLE IF EXISTS "main"."OnHandQty";
CREATE TABLE "OnHandQty" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"ProductId"  TEXT COLLATE NOCASE ,
"OnHandQty"  REAL,
"StockId"  TEXT,
"RenewQty"  REAL,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for OrderPrize
-- ----------------------------
DROP TABLE IF EXISTS "main"."OrderPrize";
CREATE TABLE "OrderPrize" (
"UniqueId"  TEXT NOT NULL,
"ProductId"  TEXT,
"CustomerId"  TEXT,
"TotalQty"  REAL,
"DiscountId"  TEXT,
"DisRef"  INTEGER,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for OrderPrizeHistory
-- ----------------------------
DROP TABLE IF EXISTS "main"."OrderPrizeHistory";
CREATE TABLE "OrderPrizeHistory" (
"Id"  INTEGER NOT NULL,
"OrderRef"  INTEGER,
"DiscountRef"  INTEGER,
"GoodsRef"  INTEGER,
"Qty"  INTEGER,
"SaleQty"  INTEGER,
"IsAutomatic"  INTEGER,
PRIMARY KEY ("Id")
);

-- ----------------------------
-- Table structure for Payment
-- ----------------------------
DROP TABLE IF EXISTS "main"."Payment";
CREATE TABLE "Payment" (
"UniqueId"  TEXT NOT NULL,
"CustomerId"  TEXT,
"Date"  INTEGER,
"ChqDate" INTEGER,
"Amount"  REAL,
"CityId"  TEXT,
"BankId"  TEXT,
"CheckAccountNumber"  TEXT,
"CheckNumber"  TEXT,
"Ref"  TEXT,
"PaymentType"  TEXT,
"RowNo"  INTEGER,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for PaymentTypeOrder
-- ----------------------------
DROP TABLE IF EXISTS "main"."PaymentTypeOrder";
CREATE TABLE "PaymentTypeOrder" (
"UniqueId"  TEXT NOT NULL,
"BackOfficeId"  TEXT(100),
"MiddlewareId"  TEXT(100),
"PaymentTypeOrderName"  TEXT(500),
"CheckCredit"  INTEGER,
"CheckDebit"  INTEGER,
"PaymentDeadLine"  INTEGER,
"PaymentTime"  INTEGER,
"ForceCash"  INTEGER,
"AllowReceipt"  INTEGER,
"PaymentTypeOrderGroupUniqueId"  TEXT(100),
"PaymentTypeOrderGroupName"  TEXT, GroupBackOfficeId INTEGER, Code INTEGER,
PRIMARY KEY ("UniqueId" ASC)
);


-- ----------------------------
-- Table structure for PictureCustomer
-- ----------------------------
DROP TABLE IF EXISTS "main"."PictureCustomer";
CREATE TABLE "PictureCustomer" (
"UniqueId"  TEXT NOT NULL,
"PictureSubjectId"  TEXT NOT NULL,
"CustomerId"  TEXT NOT NULL,
"DemandTypeUniqueId"  TEXT,
"DemandType"  INTEGER,
"NoPictureReason"  TEXT DEFAULT NULL,
PRIMARY KEY ("UniqueId" ASC),
CONSTRAINT "fkey0" FOREIGN KEY ("PictureSubjectId") REFERENCES "PictureSubject" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT "fkey1" FOREIGN KEY ("CustomerId") REFERENCES "Customer" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for PictureCustomerHistory
-- ----------------------------
DROP TABLE IF EXISTS "main"."PictureCustomerHistory";
CREATE TABLE "PictureCustomerHistory" (
"UniqueId"  TEXT NOT NULL,
"PictureSubjectId"  TEXT,
"CustomerId"  TEXT,
PRIMARY KEY ("UniqueId" ASC),
CONSTRAINT "picsub" FOREIGN KEY ("PictureSubjectId") REFERENCES "PictureSubject" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for PictureFile
-- ----------------------------
DROP TABLE IF EXISTS "main"."PictureFile";
CREATE TABLE "PictureFile" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"PictureSubjectId"  TEXT COLLATE NOCASE ,
"CustomerId"  TEXT COLLATE NOCASE ,
"FileId"  TEXT COLLATE NOCASE ,
"Width"  INTEGER,
"Height"  INTEGER,
"IsPortrait"  INTEGER,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for PictureSubject
-- ----------------------------
DROP TABLE IF EXISTS "main"."PictureSubject";
CREATE TABLE "PictureSubject" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"Title"  TEXT,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for PictureTemplateDetail
-- ----------------------------
DROP TABLE IF EXISTS "main"."PictureTemplateDetail";
CREATE TABLE "PictureTemplateDetail" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"PictureTemplateUniqueId"  TEXT COLLATE NOCASE ,
"PictureSubjectUniqueId"  TEXT COLLATE NOCASE ,
"DemandTypeUniqueId"  TEXT COLLATE NOCASE ,
"DemandType"  INTEGER,
PRIMARY KEY ("UniqueId" ASC),
CONSTRAINT "pictopic" FOREIGN KEY ("PictureTemplateUniqueId") REFERENCES "PictureTemplateHeader" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for PictureTemplateHeader
-- ----------------------------
DROP TABLE IF EXISTS "main"."PictureTemplateHeader";
CREATE TABLE "PictureTemplateHeader" (
"UniqueId"  TEXT NOT NULL,
"FromDate"  INTEGER,
"ToDate"  INTEGER,
"CenterUniqueIds"  TEXT COLLATE NOCASE ,
"SaleZoneUniqueIds"  TEXT COLLATE NOCASE ,
"StateUniqueIds"  TEXT COLLATE NOCASE ,
"CityUniqueIds"  TEXT COLLATE NOCASE ,
"CustomerActivityUniqueIds"  TEXT COLLATE NOCASE ,
"CustomerCategoryUniqueIds"  TEXT COLLATE NOCASE ,
"CustomerLevelUniqueIds"  TEXT COLLATE NOCASE ,
"SaleOfficeUniqueIds"  TEXT COLLATE NOCASE ,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for PriceClassVnLite
-- ----------------------------
DROP TABLE IF EXISTS "main"."PriceClassVnLite";
CREATE TABLE "PriceClassVnLite" (
"PriceClassRef"  INTEGER,
"PriceClassName"  TEXT,
"UniqueId"  TEXT NOT NULL COLLATE NOCASE,
PRIMARY KEY ("UniqueId")
);

-- ----------------------------
-- Table structure for PriceHistory
-- ----------------------------
DROP TABLE IF EXISTS "main"."PriceHistory";
CREATE TABLE "PriceHistory" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"GoodsRef"  INTEGER,
"DCRef"  INTEGER,
"SalePrice"  REAL,
"UserPrice"  REAL,
"GoodsCtgrRef"  INTEGER,
"StartDate"  INTEGER,
"EndDate"  INTEGER,
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
"ModifiedDate"  INTEGER,
"ModifiedDateBeforeSend"  INTEGER,
"UserRefBeforeSend"  INTEGER,
"BackOfficeId"  INTEGER,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for Printer
-- ----------------------------
DROP TABLE IF EXISTS "main"."Printer";
CREATE TABLE "Printer" (
"UniqueId"	TEXT NOT NULL COLLATE NOCASE,
"PrinterName"	TEXT,
"IsDefault" INTEGER,
"IsFound" INTEGER,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for Product
-- ----------------------------
DROP TABLE IF EXISTS "main"."Product";
CREATE TABLE "Product"
(
    UniqueId TEXT NOT NULL COLLATE NOCASE,
    BackOfficeId INTEGER,
    ProductTypeId TEXT COLLATE NOCASE,
    ProductName TEXT(2048),
    ProductCode TEXT(2048),
    Description TEXT(2048),
    ManufacturerId TEXT,
    ProductBoGroupId TEXT,
    BrandRef INTEGER,
    ProductCtgrRef TEXT,
    TaxPercent REAL,
    ChargePercent REAL,
    CartonType INTEGER,
    HasBatch INTEGER,
    ProductSubGroup1IdVnLite TEXT,
    ProductSubGroup2IdVnLite TEXT,
    CartonPrizeQty INTEGER,
    GoodsVolume INTEGER,
    ShipTypeId TEXT,
    HasImage INTEGER,
    IsFreeItem INTEGER,
    IsForSale INTEGER,
    IsForReturnWithRef INTEGER,
    IsForReturnWithOutRef INTEGER,
    IsForCount INTEGER,
    ShipTypeRef INTEGER,
    ProductGroupId INTEGER,
    StockUniqueId TEXT,
    ManufacturerCode NVARCHAR(20),
    ManufacturerRef INTEGER,
    OrderPoint REAL, IsForRequest INTEGER,
    PRIMARY KEY (UniqueId ASC),
    CONSTRAINT fkey0 FOREIGN KEY (ProductGroupId) REFERENCES ProductGroup(UniqueId) ON DELETE SET NULL ON UPDATE SET NULL,
    CONSTRAINT fkey1 FOREIGN KEY (ProductBoGroupId) REFERENCES ProductBoGroup(UniqueId) ON DELETE SET NULL ON UPDATE SET NULL
);

-- ----------------------------
-- Table structure for ProductBoGroup
-- ----------------------------
DROP TABLE IF EXISTS "main"."ProductBoGroup";
CREATE TABLE "ProductBoGroup" (
"UniqueId"  TEXT NOT NULL,
"ParentRef"  INTEGER,
"GoodsGroupName"  TEXT(2048),
"BarCode"  TEXT(2048),
"DLCode"  TEXT(2048),
"NLeft"  INTEGER,
"NRight"  INTEGER,
"NLevel"  INTEGER,
"BackOfficeId"  INTEGER,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for ProductGroup
-- ----------------------------
DROP TABLE IF EXISTS "main"."ProductGroup";
CREATE TABLE "ProductGroup" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"ProductGroupParentId"  TEXT COLLATE NOCASE ,
"ProductGroupName"  TEXT(2048),
"LastUpdate"  INTEGER,
"RowIndex"  INTEGER,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for ProductGroupCatalog
-- ----------------------------
DROP TABLE IF EXISTS "main"."ProductGroupCatalog";
CREATE TABLE "ProductGroupCatalog" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"ProductMainGroupId"  TEXT,
"CatalogName"  TEXT,
"RowIndex"  INTEGER,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for ProductMainSubType
-- ----------------------------
DROP TABLE IF EXISTS "main"."ProductMainSubType";
CREATE TABLE "ProductMainSubType"
(
    UniqueId TEXT NOT NULL,
    GoodsRef INTEGER,
    MainTypeRef INTEGER,
    SubTypeRef INTEGER,
    BackOfficeId INTEGER,
    PRIMARY KEY (UniqueId ASC)
);

-- ----------------------------
-- Table structure for ProductTaxInfo
-- ----------------------------
DROP TABLE IF EXISTS "main"."ProductTaxInfo";
CREATE TABLE "ProductTaxInfo"(
"UniqueId"  TEXT NOT NULL,
"ProductId"  NVARCHAR(50)  NULL ,
"ProductRef"  INTEGER  NULL ,
"TaxRate"  DECIMAL  NULL ,
"ChargeRate"  DECIMAL  NULL,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for ProductUnit
-- ----------------------------
DROP TABLE IF EXISTS "main"."ProductUnit";
CREATE TABLE "ProductUnit"
(
    UniqueId TEXT NOT NULL,
    ProductId TEXT,
    UnitId TEXT,
    ConvertFactor REAL,
    IsForSale INTEGER DEFAULT 0,
    IsForReturn INTEGER DEFAULT 0,
    IsDefault INTEGER,
    UnitStatusId TEXT COLLATE NOCASE,
    PRIMARY KEY (UniqueId ASC)
);

-- ----------------------------
-- Table structure for QuestionnaireAnswer
-- ----------------------------
DROP TABLE IF EXISTS "main"."QuestionnaireAnswer";
CREATE TABLE "QuestionnaireAnswer" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"CustomerId"  TEXT NOT NULL COLLATE NOCASE ,
"QuestionnaireId"  TEXT COLLATE NOCASE ,
"QuestionnaireLineId"  TEXT COLLATE NOCASE ,
"Value"  TEXT,
"AttachmentId"  TEXT COLLATE NOCASE ,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for QuestionnaireCustomer
-- ----------------------------
DROP TABLE IF EXISTS "main"."QuestionnaireCustomer";
CREATE TABLE "QuestionnaireCustomer" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"QuestionnaireId"  TEXT COLLATE NOCASE ,
"CustomerId"  TEXT COLLATE NOCASE ,
"DemandTypeId"  TEXT COLLATE NOCASE ,
"DemandType"  INTEGER,
"NoAnswerReason"  TEXT,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for QuestionnaireHeader
-- ----------------------------
DROP TABLE IF EXISTS "main"."QuestionnaireHeader";
CREATE TABLE "QuestionnaireHeader" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"Title"  TEXT,
"FromDate"  INTEGER,
"ToDate"  INTEGER,
"DemandTypeUniqueId"  TEXT COLLATE NOCASE ,
"CenterUniqueIds"  TEXT COLLATE NOCASE ,
"SaleZoneUniqueIds"  TEXT COLLATE NOCASE ,
"StateUniqueIds"  TEXT COLLATE NOCASE ,
"CityUniqueIds"  TEXT COLLATE NOCASE ,
"CustomerActivityUniqueIds"  TEXT COLLATE NOCASE ,
"CustomerCategoryUniqueIds"  TEXT COLLATE NOCASE ,
"CustomerLevelUniqueIds"  TEXT COLLATE NOCASE ,
"SaleOfficeUniqueIds"  TEXT COLLATE NOCASE ,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for QuestionnaireHistory
-- ----------------------------
DROP TABLE IF EXISTS "main"."QuestionnaireHistory";
CREATE TABLE "QuestionnaireHistory" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"QuestionnaireId"  TEXT COLLATE NOCASE ,
"CustomerId"  TEXT COLLATE NOCASE ,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for QuestionnaireLine
-- ----------------------------
DROP TABLE IF EXISTS "main"."QuestionnaireLine";
CREATE TABLE "QuestionnaireLine" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"QuestionnaireUniqueId"  TEXT COLLATE NOCASE ,
"Title"  TEXT,
"QuestionnaireLineTypeUniqueId"  TEXT COLLATE NOCASE ,
"HasAttachment"  INTEGER,
"NumberOfOptions"  INTEGER,
"AttachmentTypeUniqueId"  TEXT COLLATE NOCASE ,
"QuestionGroupUniqueId"  TEXT COLLATE NOCASE ,
"RowIndex"  INTEGER,
"Validators"  TEXT,
PRIMARY KEY ("UniqueId" ASC),
CONSTRAINT "linetoquestinoiure" FOREIGN KEY ("QuestionnaireUniqueId") REFERENCES "QuestionnaireHeader" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for QuestionnaireLineOption
-- ----------------------------
DROP TABLE IF EXISTS "main"."QuestionnaireLineOption";
CREATE TABLE "QuestionnaireLineOption" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"QuestionnaireLineUniqueId"  TEXT COLLATE NOCASE ,
"Title"  TEXT,
"RowIndex"  INTEGER,
"QuestionGroupUniqueId"  TEXT COLLATE NOCASE ,
PRIMARY KEY ("UniqueId" ASC),
CONSTRAINT "optionetoline" FOREIGN KEY ("QuestionnaireLineUniqueId") REFERENCES "QuestionnaireLine" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for RegionAreaPoint
-- ----------------------------
DROP TABLE IF EXISTS "main"."RegionAreaPoint";
CREATE TABLE "RegionAreaPoint" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"Priority"  INTEGER,
"Latitude"  REAL,
"Longitude"  REAL,
"VisitPathTypeUniqueId"  TEXT COLLATE NOCASE ,
PRIMARY KEY ("UniqueId")
);

-- ----------------------------
-- Table structure for RequestLine
-- ----------------------------
DROP TABLE IF EXISTS "main"."RequestLine";
CREATE TABLE "RequestLine" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"ProductId"  TEXT NOT NULL COLLATE NOCASE ,
"UnitPrice"  REAL,
"RowIndex"  INTEGER,
"BulkQty"  REAL,
"BulkQtyUnitUniqueId"  TEXT COLLATE NOCASE ,
PRIMARY KEY ("UniqueId" ASC),
FOREIGN KEY ("ProductId") REFERENCES "Product" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY ("BulkQtyUnitUniqueId") REFERENCES "Unit" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for RequestLineQty
-- ----------------------------
DROP TABLE IF EXISTS "main"."RequestLineQty";
CREATE TABLE "RequestLineQty" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"ProductUnitId"  TEXT NOT NULL COLLATE NOCASE ,
"RequestLineUniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"Qty"  REAL,
PRIMARY KEY ("UniqueId" ASC),
FOREIGN KEY ("ProductUnitId") REFERENCES "ProductUnit" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY ("RequestLineUniqueId") REFERENCES "RequestLine" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for ReturnReason
-- ----------------------------
DROP TABLE IF EXISTS "main"."ReturnReason";
CREATE TABLE "ReturnReason" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"ReturnReasonName"  TEXT,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for TargetDetail
-- ----------------------------
DROP TABLE IF EXISTS "main"."TargetDetail";
CREATE TABLE "TargetDetail" (
"UniqueId"  TEXT NOT NULL,
"TargetMasterUniqueId"  TEXT,
"CustomerUniqueId"  TEXT,
"PersonnelUniqueId"  TEXT,
"CustomerCount"  INTEGER,
"VisitCount"  INTEGER,
"SuccessfulVisitCount"  INTEGER,
"OrderCount"  INTEGER,
"OrderItemCount"  INTEGER,
"OrderAmount"  REAL,
PRIMARY KEY ("UniqueId"),
CONSTRAINT "customer" FOREIGN KEY ("CustomerUniqueId") REFERENCES "Customer" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT "personal" FOREIGN KEY ("PersonnelUniqueId") REFERENCES "User" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT "master" FOREIGN KEY ("TargetMasterUniqueId") REFERENCES "TargetMaster" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for TargetMaster
-- ----------------------------
DROP TABLE IF EXISTS "main"."TargetMaster";
CREATE TABLE "TargetMaster" (
"UniqueId"  TEXT NOT NULL,
"TargetTypeUniqueId"  TEXT,
"TargetBaseUniqueId"  TEXT,
"AmountTypeUniqueId"  TEXT,
"SaleZoneUniqueIds"  TEXT,
"CityUniqueIds"  TEXT,
"CustomerActivityUniqueIds"  TEXT,
"CustomerCategoryUniqueIds"  TEXT,
"CustomerLevelUniqueIds"  TEXT,
"CenterUniqueIds"  TEXT,
"SaleOfficeUniqueIds"  TEXT,
"ManufacturerUniqueIds"  TEXT,
"ProductMainGroupUniqueIds"  TEXT,
"ProductSubGroupUniqueIds"  TEXT,
"Title"  TEXT,
"FromDate"  INTEGER,
"FromPDate"  TEXT,
"ToDate"  INTEGER,
"ToPDate"  TEXT,
"StateUniqueIds"  TEXT,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for TargetProduct
-- ----------------------------
DROP TABLE IF EXISTS "main"."TargetProduct";
CREATE TABLE "TargetProduct" (
"UniqueId"  TEXT NOT NULL,
"TargetDetailUniqueId"  TEXT,
"ProductUniqueId"  TEXT,
"ProductUnitUniqueId"  TEXT,
"Amount"  REAL,
"Qty"  REAL,
PRIMARY KEY ("UniqueId"),
CONSTRAINT "detail" FOREIGN KEY ("TargetDetailUniqueId") REFERENCES "TargetDetail" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT "product" FOREIGN KEY ("ProductUniqueId") REFERENCES "Product" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT "productunit" FOREIGN KEY ("ProductUnitUniqueId") REFERENCES "ProductUnit" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for TargetProductGroup
-- ----------------------------
DROP TABLE IF EXISTS "main"."TargetProductGroup";
CREATE TABLE "TargetProductGroup" (
"UniqueId"  TEXT NOT NULL,
"TargetDetailUniqueId"  TEXT,
"ProductBoGroupUniqueId"  TEXT,
"SmallProductUnitUniqueId"  TEXT,
"LargeProductUnitUniqueId"  TEXT,
"Amount"  REAL,
"SmallQty"  REAL,
"LargeQty"  REAL,
PRIMARY KEY ("UniqueId"),
CONSTRAINT "detail" FOREIGN KEY ("TargetDetailUniqueId") REFERENCES "TargetDetail" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT "bogroup" FOREIGN KEY ("ProductBoGroupUniqueId") REFERENCES "ProductBoGroup" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT "smallunit" FOREIGN KEY ("SmallProductUnitUniqueId") REFERENCES "Unit" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT "largeunit" FOREIGN KEY ("LargeProductUnitUniqueId") REFERENCES "Unit" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for TaskPriority
-- ----------------------------
DROP TABLE IF EXISTS "main"."TaskPriority";
CREATE TABLE "TaskPriority" (
"DeviceTaskUniqueId"  TEXT,
"Priority"  INTEGER,
"UniqueId"  TEXT NOT NULL,
"IsEnabled"  INTEGER,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for TourUpdateLog
-- ----------------------------
DROP TABLE IF EXISTS "main"."TourUpdateLog";
CREATE TABLE TourUpdateLog
(
    UniqueId TEXT,
    Name TEXT,
    GroupName TEXT,
    Error TEXT,
    StartDate INTEGER,
    FinishDate INTEGER,
    TourId TEXT,
    LocalTourId TEXT
);

-- ----------------------------
-- Table structure for Unit
-- ----------------------------
DROP TABLE IF EXISTS "main"."Unit";
CREATE TABLE "Unit"
(
    UniqueId TEXT NOT NULL,
    UnitName TEXT,
    BackOfficeId INTEGER,
    PRIMARY KEY (UniqueId ASC)
);

-- ----------------------------
-- Table structure for UpdateLog
-- ----------------------------
DROP TABLE IF EXISTS "main"."UpdateLog";
CREATE TABLE "UpdateLog" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"Name"  TEXT,
"Date"  INTEGER,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for ValidPayType
-- ----------------------------
DROP TABLE IF EXISTS "main"."ValidPayType";
CREATE TABLE "ValidPayType" (
"UniqueId"  TEXT NOT NULL,
"BackOfficeId"  INTEGER,
"PayTypeRef"  INTEGER,
"PayTypeId"  TEXT,
"BuyTypeRef"  INTEGER,
"BuyTypeId"  TEXT,
PRIMARY KEY ("UniqueId")
);

-- ----------------------------
-- Table structure for VisitDay
-- ----------------------------
DROP TABLE IF EXISTS "main"."VisitDay";
CREATE TABLE "VisitDay" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"RowIndex"  INTEGER,
"PathTitle"  TEXT(2048),
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for VisitTemplatePathCustomer
-- ----------------------------
DROP TABLE IF EXISTS "main"."VisitTemplatePathCustomer";
CREATE TABLE "VisitTemplatePathCustomer" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"VisitTemplatePathId"  TEXT,
"CustomerId"  TEXT,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- View structure for CustomerCallOrderInvoiceView
-- ----------------------------
DROP VIEW IF EXISTS "main"."CustomerCallOrderInvoiceView";
CREATE VIEW "CustomerCallOrderInvoiceView" AS
SELECT
CustomerCallOrderLines.UniqueId as UniqueId,
CustomerCallOrderLines.OrderUniqueId as OrderUniqueId,
Product.ProductName,
ProductUnit.ProductId as ProductId,
group_concat(Qty , ':') as Qty ,
group_concat(ConvertFactor , ':') as ConvertFactor,
group_concat(CustomerCallOrderLinesInvoiceQtyDetail.ProductUnitId , ':') as ProductUnitId ,
group_concat(UnitName,':') as UnitName,
sum(Qty * ConvertFactor ) as TotalQty
FROM CustomerCallOrderLines JOIN ProductUnit ON CustomerCallOrderLinesInvoiceQtyDetail.ProductUnitId = ProductUnit.UniqueId
JOIN CustomerCallOrderLinesInvoiceQtyDetail ON CustomerCallOrderLinesInvoiceQtyDetail.OrderLineUniqueId = CustomerCallOrderLines.UniqueId
JOIN Product ON Product.UniqueId = ProductUnit.ProductId
JOIN Unit ON Unit.UniqueId = ProductUnit.UnitId
GROUP BY OrderLineUniqueId;

-- ----------------------------
-- View structure for CustomerCallOrderLinesInvoiceView
-- ----------------------------
DROP VIEW IF EXISTS "main"."CustomerCallOrderLinesInvoiceView";
CREATE VIEW "CustomerCallOrderLinesInvoiceView" AS SELECT
CustomerCallOrderLines.OrderUniqueId as OrderUniqueId,
CustomerCallOrderLines.UniqueId as OrderLineUniqueId,
Product.ProductName as ProductName,
ProductUnit.ProductId as ProductId,
CustomerCallOrderLinesInvoiceQtyDetail.Qty as Qty,
ProductUnit.UnitId as ProductUnitId,
Unit.UnitName as UnitName
FROM CustomerCallOrderLines JOIN ProductUnit ON CustomerCallOrderLinesInvoiceQtyDetail.ProductUnitId = ProductUnit.UniqueId
JOIN CustomerCallOrderLinesInvoiceQtyDetail ON CustomerCallOrderLinesInvoiceQtyDetail.OrderLineUniqueId = CustomerCallOrderLines.UniqueId
JOIN Product ON Product.UniqueId = CustomerCallOrderLines.ProductUniqueId
JOIN Unit ON Unit.UniqueId = ProductUnit.UnitId;

-- ----------------------------
-- View structure for ProductUnitsView
-- ----------------------------
DROP VIEW IF EXISTS "main"."ProductUnitsView";
CREATE VIEW ProductUnitsView
AS
SELECT a.UniqueId AS UniqueId,group_concat(a.IsForReturn,':') AS IsForReturn,group_concat(a.IsForSale,':') AS IsForSale,group_concat(a.IsDefault,':') AS IsDefault,group_concat(a.ConvertFactor,':') AS ConvertFactor,group_concat(a.UnitName,':') AS UnitName,group_concat(a.ProductUnitId,':') AS ProductUnitId
    FROM  (SELECT Product.UniqueId AS UniqueId,Unit.BackOfficeId AS UnitRef,ProductUnit.IsForReturn AS IsForReturn,ProductUnit.IsForSale AS IsForSale,ProductUnit.IsDefault AS IsDefault,ProductUnit.UnitId AS ProductUnitId,ProductName,ProductId,Product.BackOfficeId,ProductCode,Unit.UniqueId AS UnitId,ConvertFactor,UnitName
    FROM  Product ,  ProductUnit ON Product.UniqueId = ProductUnit.ProductId ,  Unit ON Unit.UniqueId = ProductUnit.UnitId
    ORDER BY ProductId,ConvertFactor DESC)  AS a
    GROUP BY ProductId;

-- ----------------------------
-- View structure for TotalProductOrderQtyView
-- ----------------------------
DROP VIEW IF EXISTS "main"."TotalProductOrderQtyView";
CREATE VIEW "TotalProductOrderQtyView" AS
SELECT
	ProductUnit.ProductId,
	sum(Qty * ConvertFactor) as TotalQty
FROM
	CustomerCallOrderLinesOrderQtyDetail
INNER JOIN ProductUnit ON ProductUnit.UniqueId = ProductUnitId
GROUP BY
	ProductId;

-- ----------------------------
-- View structure for CustomerCallOrderOrderView
-- ----------------------------
DROP VIEW IF EXISTS "main"."CustomerCallOrderOrderView";
CREATE VIEW CustomerCallOrderOrderView AS
SELECT
 a.*,
 CASE
  WHEN a.RequestBulkQtyUnitUniqueId IS NULL THEN
   (a.TotalQty * a.UnitPrice)
  ELSE
  (a.RequestBulkQty * a.UnitPrice)
 END AS RequestAmount,
 FreeReason.FreeReasonName AS FreeReasonName,
 OnHandQty.OnHandQty AS OnHandQty,
 ProductUnitsView.ConvertFactor AS AllConvertFactors,
 ProductUnitsView.UnitName AS AllUnitNames,
 TotalProductOrderQtyView.TotalQty AS ProductTotalOrderedQty
FROM
	(
		SELECT
			CustomerCallOrderLines.UniqueId AS UniqueId,
			CustomerCallOrderLines.OrderUniqueId AS OrderUniqueId,
			CustomerCallOrder.CustomerUniqueId AS CustomerUniqueId,
			CustomerCallOrderLines.FreeReasonId AS FreeReasonId,
			CustomerCallOrderLines.RequestAdd1Amount AS RequestAdd1Amount,
			CustomerCallOrderLines.RequestAdd2Amount AS RequestAdd2Amount,
			CustomerCallOrderLines.RequestTaxAmount AS RequestTaxAmount,
			CustomerCallOrderLines.RequestChargeAmount AS RequestChargeAmount,
			CustomerCallOrderLines.RequestDis1Amount AS RequestDis1Amount,
			CustomerCallOrderLines.RequestDis2Amount AS RequestDis2Amount,
			CustomerCallOrderLines.RequestDis3Amount AS RequestDis3Amount,
			CustomerCallOrderLines.RequestOtherDiscountAmount AS RequestOtherDiscountAmount,
			CustomerCallOrderLines.InvoiceAmount AS InvoiceAmount,
			CustomerCallOrderLines.InvoiceAdd1Amount AS InvoiceAdd1Amount,
			CustomerCallOrderLines.InvoiceAdd2Amount AS InvoiceAdd2Amount,
			CustomerCallOrderLines.InvoiceTaxAmount AS InvoiceTaxAmount,
			CustomerCallOrderLines.InvoiceChargeAmount AS InvoiceChargeAmount,
			CustomerCallOrderLines.InvoiceOtherDiscountAmount AS InvoiceOtherDiscountAmount,
			CustomerCallOrderLines.InvoiceDis1Amount AS InvoiceDis1Amount,
			CustomerCallOrderLines.InvoiceDis2Amount AS InvoiceDis2Amount,
			CustomerCallOrderLines.InvoiceDis3Amount AS InvoiceDis3Amount,
			CustomerCallOrderLines.RequestBulkQty AS RequestBulkQty,
			CustomerCallOrderLines.SortId AS SortId,
			CustomerCallOrderLines.RequestBulkQtyUnitUniqueId AS RequestBulkQtyUnitUniqueId,
			CustomerCallOrderLines.IsPromoLine AS IsPromoLine,
			Product.ProductName AS ProductName,
			Product.ProductCode AS ProductCode,
			Product.IsFreeItem AS IsFreeItem,
			Product.OrderPoint AS OrderPoint,
			CustomerPrice.Price AS UnitPrice,
			CustomerPrice.UserPrice AS UserPrice,
			CustomerPrice.PriceId AS PriceId,
			ProductUnit.ProductId AS ProductId,
			group_concat(Qty, ':') AS Qty,
			group_concat(ConvertFactor, ':') AS ConvertFactor,
			group_concat(
				CustomerCallOrderLinesOrderQtyDetail.ProductUnitId,
				':'
			) AS ProductUnitId,
			group_concat(UnitName, ':') AS UnitName,
      CASE
       WHEN CustomerCallOrderLines.RequestBulkQtyUnitUniqueId IS NULL THEN
        sum(Qty * ConvertFactor)
       ELSE
        CustomerCallOrderLines.RequestBulkQty
      END AS TotalQty,
			CustomerCallOrderLines.IsRequestFreeItem,
			CustomerEmphaticProduct.Type AS EmphaticType,
			CustomerEmphaticProduct.ProductCount AS EmphaticProductCount,
			CustomerCallOrder.SaleDate AS SaleDate
		FROM
			CustomerCallOrderLines,
			ProductUnit ON CustomerCallOrderLinesOrderQtyDetail.ProductUnitId = ProductUnit.UniqueId,
			CustomerCallOrderLinesOrderQtyDetail ON CustomerCallOrderLinesOrderQtyDetail.OrderLineUniqueId = CustomerCallOrderLines.UniqueId,
			Product ON Product.UniqueId = CustomerCallOrderLines.ProductUniqueId,
			Unit ON Unit.UniqueId = ProductUnit.UnitId,
			CustomerCallOrder ON CustomerCallOrder.UniqueId = CustomerCallOrderLines.OrderUniqueId
		LEFT JOIN CustomerEmphaticProduct ON CustomerEmphaticProduct.ProductId = Product.UniqueId
		LEFT JOIN CustomerPrice ON (
			CustomerPrice.CustomerUniqueId = CustomerCallOrder.CustomerUniqueId
		)
		AND (
			CustomerPrice.ProductUniqueId = CustomerCallOrderLines.ProductUniqueId
		)
		GROUP BY
			OrderLineUniqueId
	) AS a
LEFT JOIN FreeReason ON FreeReason.UniqueId = a.FreeReasonId
LEFT JOIN OnHandQty ON OnHandQty.ProductId = a.ProductId
LEFT JOIN ProductUnitsView ON ProductUnitsView.UniqueId = a.ProductId
LEFT JOIN TotalProductOrderQtyView ON TotalProductOrderQtyView.ProductId = a.ProductId;

-- ----------------------------
-- View structure for CustomerCallOrderPreview
-- ----------------------------
DROP VIEW IF EXISTS "main"."CustomerCallOrderPreview";
CREATE VIEW "CustomerCallOrderPreview" AS
SELECT OrderUniqueId as UniqueId ,  CustomerCallOrderOrderView.CustomerUniqueId as CustomerUniqueId , SUM(TotalQty) as TotalQty , SUM(UnitPrice * TotalQty ) as TotalPrice, CustomerCallOrder.LocalPaperNo as LocalPaperNo , CustomerCallOrder.Comment as Comment FROM CustomerCallOrderOrderView
JOIN CustomerCallOrder ON CustomerCallOrder.UniqueId = CustomerCallOrderOrderView.OrderUniqueId
GROUP BY OrderUniqueId ORDER BY LocalPaperNo ASC;

-- ----------------------------
-- View structure for CustomerCallOrderView
-- ----------------------------
DROP VIEW IF EXISTS "main"."CustomerCallOrderView";
CREATE VIEW "CustomerCallOrderView" AS
SELECT
CustomerCallOrderOrderView.UniqueId as UniqueId,
CustomerCallOrderOrderView.OrderUniqueId as OrderUniqueId,
CustomerCallOrderOrderView.ProductId as ProductId,
CustomerCallOrderOrderView.ProductName as ProductName,
CustomerCallOrderOrderView.Qty as OrderQty,
CustomerCallOrderOrderView.UnitName as OrderUnitName,
CustomerCallOrderOrderView.ProductUnitId as OrderProductUnitId,
CustomerCallOrderOrderView.TotalQty as OrderTotalQty,
CustomerCallOrderInvoiceView.Qty as InvoiceQty,
CustomerCallOrderInvoiceView.UnitName as InvoiceUnitName,
CustomerCallOrderInvoiceView.ProductUnitId as InvoiceProductUnitId,
CustomerCallOrderInvoiceView.TotalQty as InvoiceTotalQty
FROM
CustomerCallOrderOrderView LEFT JOIN CustomerCallOrderInvoiceView
On CustomerCallOrderOrderView.UniqueId = CustomerCallOrderInvoiceView.UniqueId;

-- ----------------------------
-- View structure for CustomerCallReturnLinesView
-- ----------------------------
DROP VIEW IF EXISTS "main"."CustomerCallReturnLinesView";
CREATE VIEW CustomerCallReturnLinesView AS
SELECT
	CustomerCallReturnLines.*, CustomerCallReturn.CustomerUniqueId AS CustomerUniqueId,
	Product.ProductName AS ProductName,
	Product.ProductCode AS ProductCode,
	ProductUnit.ProductId AS ProductId,
	group_concat(Qty, ':') AS Qty,
	group_concat(ConvertFactor, ':') AS ConvertFactor,
	group_concat(
		CustomerCallReturnLinesQtyDetail.ProductUnitId,
		':'
	) AS ProductUnitId,
	group_concat(Unit.UnitName, ':') AS UnitName,
	sum(Qty * ConvertFactor) AS TotalReturnQty,
	CustomerCallReturnLines.RequestUnitPrice * sum(Qty * ConvertFactor) AS TotalRequestAmount,
	CustomerCallReturn.BackOfficeInvoiceId AS InvoiceId,
	CustomerOldInvoiceHeader.SaleNo AS SaleNo
FROM
	CustomerCallReturnLines
JOIN ProductUnit ON CustomerCallReturnLinesQtyDetail.ProductUnitId = ProductUnit.UniqueId
JOIN CustomerCallReturnLinesQtyDetail ON CustomerCallReturnLinesQtyDetail.ReturnLineUniqueId = CustomerCallReturnLines.UniqueId
JOIN Product ON Product.UniqueId = CustomerCallReturnLines.ProductUniqueId
JOIN Unit ON Unit.UniqueId = ProductUnit.UnitId
JOIN CustomerCallReturn ON CustomerCallReturn.UniqueId = CustomerCallReturnLines.ReturnUniqueId
JOIN ReturnReason ON ReturnReason.UniqueId = CustomerCallReturnLines.ReturnReasonId
LEFT JOIN CustomerOldInvoiceDetail ON CustomerOldInvoiceDetail.ProductId = CustomerCallReturnLines.ProductUniqueId
and CustomerOldInvoiceDetail.SaleId = CustomerCallReturn.BackOfficeInvoiceId
LEFT JOIN CustomerOldInvoiceHeader ON CustomerOldInvoiceHeader.UniqueId = CustomerOldInvoiceDetail.SaleId
AND CustomerOldInvoiceHeader.UniqueId = CustomerCallReturn.BackOfficeInvoiceId
GROUP BY
	ReturnLineUniqueId;

-- ----------------------------
-- View structure for CustomerCallReturnWithAmountView
-- ----------------------------
DROP VIEW IF EXISTS "main"."CustomerCallReturnWithAmountView";
CREATE VIEW "CustomerCallReturnWithAmountView" AS
SELECT
CustomerCallReturn.CustomerUniqueId,
CustomerCallReturn.UniqueId,
CustomerCallReturn.ReturnTypeUniqueId,
CustomerCallReturn.PersonnelUniqueId,
CustomerCallReturn.LocalPaperNo,
CustomerCallReturn.BackOfficeInvoiceId,
CustomerCallReturn.BackOfficeInvoiceNo,
CustomerCallReturn.BackOfficeInvoiceDate,
CustomerCallReturn.ReturnRequestBackOfficeId,
CustomerCallReturn.ReturnRequestBackOfficeDate,
CustomerCallReturn.ReturnRequestBackOfficeNo,
CustomerCallReturn.ReturnReasonUniqueId,
CustomerCallReturn.CallActionStatusUniqueId,
CustomerCallReturn.ReturnRequestRejectReasonUniqueId,
CustomerCallReturn.Comment,
CustomerCallReturn.BackOfficeDistId,
ifnull(CustomerCallReturn.TotalRequestAmount,0) as TotalRequestAmount,
ifnull(CustomerCallReturn.TotalRequestTax,0) as TotalRequestTax,
ifnull(CustomerCallReturn.TotalRequestCharge,0) as TotalRequestCharge,
ifnull(CustomerCallReturn.TotalRequestDiscount,0) as TotalRequestDiscount,
ifnull(CustomerCallReturn.TotalRequestAmount,0) + ifnull(CustomerCallReturn.TotalRequestTax,0) + ifnull(CustomerCallReturn.TotalRequestCharge,0) - ifnull(CustomerCallReturn.TotalRequestDiscount,0) as TotalRequestNetAmount,
ifnull(CustomerCallReturn.TotalReturnAmount,0) as TotalReturnAmount,
ifnull(CustomerCallReturn.TotalReturnOtherDiscount,0) as TotalReturnOtherDiscount,
ifnull(CustomerCallReturn.TotalReturnDis1,0) as TotalReturnDis1,
ifnull(CustomerCallReturn.TotalReturnDis2,0) as TotalReturnDis2,
ifnull(CustomerCallReturn.TotalReturnDis3,0) as TotalReturnDis3,
ifnull(CustomerCallReturn.TotalReturnCharge,0) as TotalReturnCharge,
ifnull(CustomerCallReturn.TotalReturnTax,0) as TotalReturnTax,
ifnull(CustomerCallReturn.TotalReturnAdd1,0) as TotalReturnAdd1,
ifnull(CustomerCallReturn.TotalReturnAdd2,0) as TotalReturnAdd2,
ifnull(CustomerCallReturn.TotalReturnOtherDiscount,0)+ ifnull(CustomerCallReturn.TotalReturnDis1,0) + ifnull(CustomerCallReturn.TotalReturnDis2,0) + ifnull(CustomerCallReturn.TotalReturnDis3,0) as TotalReturnDiscount,
ifnull(CustomerCallReturn.TotalReturnTax,0) +  ifnull(CustomerCallReturn.TotalReturnAdd1,0) as TotalReturnTaxAdd1,
ifnull(CustomerCallReturn.TotalReturnCharge,0) +  ifnull(CustomerCallReturn.TotalReturnAdd2,0) as TotalReturnChargeAdd2,
ifnull(CustomerCallReturn.TotalReturnTax,0) + ifnull(CustomerCallReturn.TotalReturnCharge,0) + ifnull(CustomerCallReturn.TotalReturnAdd1,0) +  ifnull(CustomerCallReturn.TotalReturnAdd2,0) as TotalReturnAdd,
ifnull(CustomerCallReturn.TotalReturnAmount,0) + ifnull(CustomerCallReturn.TotalReturnTax,0) + ifnull(CustomerCallReturn.TotalReturnCharge,0) + ifnull(CustomerCallReturn.TotalReturnAdd1,0) +  ifnull(CustomerCallReturn.TotalReturnAdd2,0) - (ifnull(CustomerCallReturn.TotalReturnOtherDiscount,0)+ ifnull(CustomerCallReturn.TotalReturnDis1,0) + ifnull(CustomerCallReturn.TotalReturnDis2,0) + ifnull(CustomerCallReturn.TotalReturnDis3,0)) as TotalReturnNetAmount
FROM
CustomerCallReturn;
-- ----------------------------
-- View structure for CustomerCallReturnSummaryView
-- ----------------------------
DROP VIEW IF EXISTS "main"."CustomerCallReturnSummaryView";
CREATE VIEW "CustomerCallReturnSummaryView" AS
SELECT
sum(CustomerCallReturnWithAmountView.TotalReturnAmount) as TotalReturnAmount,
sum(CustomerCallReturnWithAmountView.TotalReturnOtherDiscount) as TotalReturnOtherDiscount,
sum(CustomerCallReturnWithAmountView.TotalReturnDis1) as TotalReturnDis1,
sum(CustomerCallReturnWithAmountView.TotalReturnDis2) as TotalReturnDis2,
sum(CustomerCallReturnWithAmountView.TotalReturnDis3) as TotalReturnDis3,
sum(CustomerCallReturnWithAmountView.TotalReturnCharge) as TotalReturnCharge,
sum(CustomerCallReturnWithAmountView.TotalReturnTax) as TotalReturnTax,
sum(CustomerCallReturnWithAmountView.TotalReturnAdd1) as TotalReturnAdd1,
sum(CustomerCallReturnWithAmountView.TotalReturnAdd2) as TotalReturnAdd2,
sum(CustomerCallReturnWithAmountView.TotalReturnDiscount) as TotalReturnDiscount,
sum(CustomerCallReturnWithAmountView.TotalReturnTaxAdd1) as TotalReturnTaxAdd1,
sum(CustomerCallReturnWithAmountView.TotalReturnChargeAdd2) as TotalReturnChargeAdd2,
sum(CustomerCallReturnWithAmountView.TotalReturnAdd) as TotalReturnAdd,
sum(CustomerCallReturnWithAmountView.TotalReturnNetAmount) as TotalReturnNetAmount
FROM
CustomerCallReturnWithAmountView
GROUP BY
CustomerCallReturnWithAmountView.CustomerUniqueId;

-- ----------------------------
-- View structure for CustomerCallReturnView
-- ----------------------------
DROP VIEW IF EXISTS CustomerCallReturnView;
CREATE VIEW CustomerCallReturnView AS
SELECT
	CustomerCallReturnLinesView.UniqueId,
	CustomerCallReturnLinesView.StockId,
	CustomerCallReturnLinesView.CustomerUniqueId AS CustomerUniqueId,
	CustomerCallReturnLinesView.ReturnUniqueId AS ReturnUniqueId,
	CustomerCallReturnLinesView.InvoiceId AS InvoiceId,
	group_concat(ReturnProductTypeId, ':') AS ReturnProductTypeId,
	group_concat(ReturnReasonId, ':') AS ReturnReasonId,
	CustomerCallReturnLinesView.ProductName AS ProductName,
	CustomerCallReturnLinesView.ProductCode AS ProductCode,
	CustomerCallReturnLinesView.ProductId AS ProductId,
	group_concat(CustomerCallReturnLinesView.ConvertFactor,'|') AS ConvertFactor,
	group_concat(CustomerCallReturnLinesView.ProductUnitId,'|') AS ProductUnitId,
	group_concat(CustomerCallReturnLinesView.Qty,'|') AS Qty,
	group_concat(CustomerCallReturnLinesView.UnitName,'|') AS UnitName,
	sum(CustomerCallReturnLinesView.TotalReturnQty) AS TotalReturnQty,
	CustomerOldInvoiceDetail.TotalQty AS InvoiceQty,
	CustomerOldInvoiceHeader.SaleNo AS SaleNo,
	CustomerCallReturnLinesView.RequestUnitPrice,
	CustomerCallReturnLinesView.TotalRequestAmount AS TotalRequestAmount
FROM CustomerCallReturnLinesView
JOIN ReturnReason ON ReturnReason.UniqueId = CustomerCallReturnLinesView.ReturnReasonId
LEFT JOIN CustomerOldInvoiceDetail ON CustomerOldInvoiceDetail.ProductId = CustomerCallReturnLinesView.ProductId
and CustomerOldInvoiceDetail.SaleId = CustomerCallReturnLinesView.InvoiceId
LEFT JOIN CustomerOldInvoiceHeader ON CustomerOldInvoiceHeader.UniqueId = CustomerOldInvoiceDetail.SaleId
AND CustomerOldInvoiceDetail.SaleId = CustomerCallReturnLinesView.InvoiceId
GROUP BY CustomerCallReturnLinesView.ProductId, ReturnUniqueId;

-- ----------------------------
-- View structure for CustomerCallView
-- ----------------------------
DROP VIEW IF EXISTS "main"."CustomerCallView";
CREATE VIEW CustomerCallView
AS
SELECT CustomerId,group_concat(CallType,':') AS CallType,group_concat(ConfirmStatus,':') AS ConfirmStatus,SUM(ConfirmStatus) AS ConfirmCount,count(*) AS TotalCount,CASE WHEN SUM(ConfirmStatus) = count(*) THEN '1' ELSE '0' END AS Confirmed
    FROM  CustomerCall
    GROUP BY CustomerId;

-- ----------------------------
-- View structure for CustomerEmphaticProductView
-- ----------------------------
DROP VIEW IF EXISTS "main"."CustomerEmphaticProductView";
CREATE VIEW "CustomerEmphaticProductView" AS
SELECT
	EmphaticProduct.UniqueId AS RuleId,
	EmphaticProduct.EmphasisProductErrorTypeId AS TypeId,
	EmphaticProductCount.ProductId AS ProductId,
	EmphaticProductCount.ProductCount AS ProductCount,
	EmphaticProduct.FromDate AS FromDate,
	EmphaticProduct.ToDate AS ToDate,
	((strftime('%s','now')- (EmphaticProduct.WarningDay * 86400))*1000) AS WarningDate,
	((strftime('%s','now')- (EmphaticProduct.DangerDay * 86400))*1000) AS DangerDate,
	Customer.UniqueId AS CustomerId
FROM
	EmphaticProduct
INNER JOIN EmphaticProductCount ON EmphaticProduct.UniqueId == EmphaticProductCount.RuleId
LEFT JOIN Customer
WHERE
	-- ((EmphaticProduct.SaleZoneId is null) or (EmphaticProduct.SaleZoneId = IFNULL(Customer.SaleZoneNo,0)))
	-- AND ((EmphaticProduct.DCId is null) or (EmphaticProduct.DCId = IFNULL(Customer.DCRef ,0)))
	(
		(
			EmphaticProduct.StateId IS NULL
		)
		OR (
			EmphaticProduct.StateId = IFNULL(Customer.StateId, NULL)
		)
	)
AND (
	(
		EmphaticProduct.CityId IS NULL
	)
	OR (
		EmphaticProduct.CityId = IFNULL(Customer.CityId, NULL)
	)
)
AND (
	(
		EmphaticProduct.CustomerActivityId IS NULL
	)
	OR (
		EmphaticProduct.CustomerActivityId = IFNULL(
			Customer.CustomerActivityId,
			NULL
		)
	)
)
AND (
	(
		EmphaticProduct.CustomerCategoryId IS NULL
	)
	OR (
		EmphaticProduct.CustomerCategoryId = IFNULL(
			Customer.CustomerCategoryId,
			NULL
		)
	)
)
AND (
	(
		EmphaticProduct.CustomerLevelId IS NULL
	)
	OR (
		EmphaticProduct.CustomerLevelId = IFNULL(
			Customer.CustomerLevelId,
			NULL
		)
	)
);

-- ----------------------------
-- View structure for CustomerInventoryView
-- ----------------------------
DROP VIEW IF EXISTS "main"."CustomerInventoryView";
CREATE VIEW "CustomerInventoryView" AS
SELECT
CustomerInventory.UniqueId,
CustomerInventory.CustomerId,
CustomerInventory.ProductId,
Product.ProductName,
Product.ProductCode,
group_concat(CustomerInventoryQty.Qty,':') as Qty,
group_concat(Unit.UnitName,':') as UnitName,
sum(CustomerInventoryQty.Qty * ProductUnit.ConvertFactor) as TotalQty,
CustomerInventory.IsAvailable,
CustomerInventory.IsSold
from CustomerInventory
LEFT JOIN CustomerInventoryQty ON CustomerInventory.UniqueId = CustomerInventoryQty.CustomerInventoryId
LEFT JOIN ProductUnit ON ProductUnit.UniqueId = CustomerInventoryQty.ProductUnitId
LEFT JOIN Unit ON ProductUnit.UnitId = Unit.UniqueId
JOIN Product On Product.UniqueId = CustomerInventory.ProductId
GROUP BY CustomerInventory.UniqueId;

-- ----------------------------
-- View structure for CustomerOpenInvoicesView
-- ----------------------------
DROP VIEW IF EXISTS "main"."CustomerOpenInvoicesView";
CREATE VIEW "CustomerOpenInvoicesView" AS
SELECT
Customer.UniqueId AS CustomerId,
CustomerOldInvoiceHeader.SaleNo,
CustomerOldInvoiceHeader.SalePDate,
CustomerOldInvoiceHeader.TotalAmount,
CustomerOldInvoiceHeader.PayAmount,
CustomerOldInvoiceHeader.DealerName,
CustomerOldInvoiceHeader.DealerId,
CustomerOldInvoiceHeader.TotalAmount - CustomerOldInvoiceHeader.PayAmount AS RemAmount
FROM CustomerOldInvoiceHeader JOIN Customer ON Customer.UniqueId = CustomerOldInvoiceHeader.CustomerId
WHERE CustomerOldInvoiceHeader.PayAmount < CustomerOldInvoiceHeader.TotalAmount;

-- ----------------------------
-- View structure for CustomerPathView
-- ----------------------------
DROP VIEW IF EXISTS "main"."CustomerPathView";
CREATE VIEW "CustomerPathView" AS
SELECT c.*, VisitTemplatePathCustomer.VisitTemplatePathId AS VisitTemplatePathId
FROM (SELECT Customer.*, SUM(CustomerCallOrderPreview.TotalPrice) as TotalOrderAmount
FROM Customer
LEFT JOIN CustomerCallOrderPreview ON CustomerCallOrderPreview.CustomerUniqueId = Customer.UniqueId
GROUP BY Customer.UniqueId) c
JOIN VisitTemplatePathCustomer ON c.UniqueId = VisitTemplatePathCustomer.CustomerId;

-- ----------------------------
-- View structure for DiscountItemCountView
-- ----------------------------
DROP VIEW IF EXISTS "main"."DiscountItemCountView";
CREATE VIEW "DiscountItemCountView" AS
SELECT
DiscountItemCount.UniqueId AS UniqueId,
DiscountItemCount.DisRef AS DisRef,
DiscountItemCount.GoodsRef AS GoodsRef,
Product.ProductName AS ProductName,
Product.ProductCode AS ProductCode,
Product.UniqueId AS ProductId
FROM
DiscountItemCount
INNER JOIN Product ON DiscountItemCount.GoodsRef = Product.BackOfficeId;

-- ----------------------------
-- View structure for OldInvoiceDetailReportView
-- ----------------------------
DROP VIEW IF EXISTS "main"."OldInvoiceDetailReportView";
CREATE VIEW "OldInvoiceDetailReportView" AS
SELECT
CustomerOldInvoiceHeader.CustomerId,
CustomerOldInvoiceDetail.ProductId ,
CustomerOldInvoiceDetail.AddAmount,
CustomerOldInvoiceDetail.UnitQty,
CustomerOldInvoiceHeader.SaleNo,
CustomerOldInvoiceHeader.SalePDate,
CustomerOldInvoiceDetail.TotalQty,
CustomerOldInvoiceDetail.UnitName,
CustomerOldInvoiceDetail.UnitPrice,
CustomerOldInvoiceDetail.SaleId,
Product.ProductName,
Product.ProductCode,
Product.ProductGroupId,
CustomerOldInvoiceDetail.UnitPrice * CustomerOldInvoiceDetail.TotalQty AS TotalAmount,
CustomerCallReturnView.TotalReturnQty as TotalReturnQty
FROM CustomerOldInvoiceDetail
JOIN Product ON CustomerOldInvoiceDetail.ProductId = Product.UniqueId
LEFT JOIN CustomerCallReturnView ON CustomerCallReturnView.ProductId = Product.UniqueId AND
CustomerCallReturnView.InvoiceId = SaleId
JOIN CustomerOldInvoiceHeader ON CustomerOldInvoiceHeader.UniqueId = CustomerOldInvoiceDetail.SaleId;

-- ----------------------------
-- View structure for OldInvoiceDetailView
-- ----------------------------
DROP VIEW IF EXISTS OldInvoiceDetailView;
CREATE VIEW OldInvoiceDetailView AS
SELECT
CustomerOldInvoiceHeader.CustomerId,
CustomerOldInvoiceHeader.StockId,
CustomerOldInvoiceDetail.SaleId AS SaleId,
CustomerOldInvoiceHeader.SaleNo,
CustomerOldInvoiceDetail.ProductId ,
CustomerOldInvoiceDetail.AddAmount,
CustomerOldInvoiceDetail.UnitQty,
CustomerOldInvoiceDetail.TotalQty,
CustomerOldInvoiceDetail.UnitName,
CustomerOldInvoiceDetail.UnitPrice,
Product.ProductName,
Product.ProductCode,
Product.ProductGroupId,
CustomerOldInvoiceDetail.UnitPrice * CustomerOldInvoiceDetail.TotalQty AS TotalAmount,
CustomerCallReturnView.TotalReturnQty as TotalReturnQty
FROM CustomerOldInvoiceDetail
JOIN Product ON CustomerOldInvoiceDetail.ProductId = Product.UniqueId
LEFT JOIN CustomerCallReturnView ON CustomerCallReturnView.ProductId = Product.UniqueId AND
CustomerCallReturnView.InvoiceId = SaleId
JOIN CustomerOldInvoiceHeader ON CustomerOldInvoiceHeader.UniqueId = CustomerOldInvoiceDetail.SaleId;

-- ----------------------------
-- View structure for OldInvoiceHeaderView
-- ----------------------------
DROP VIEW IF EXISTS "main"."OldInvoiceHeaderView";
CREATE VIEW OldInvoiceHeaderView
AS
SELECT CustomerOldInvoiceHeader.UniqueId AS UniqueId,Customer.UniqueId AS CustomerUniqueId,CustomerOldInvoiceHeader.TotalAmount AS Amount,CustomerOldInvoiceHeader.PayAmount AS PayAmount,(CustomerOldInvoiceHeader.Dis1Amount + CustomerOldInvoiceHeader.Dis2Amount) + CustomerOldInvoiceHeader.Dis3Amount AS DiscountAmount,CustomerOldInvoiceHeader.TaxAmount AS TaxAmount,CustomerOldInvoiceHeader.CustomerId AS CustomerId,Customer.Address AS Address,Customer.CustomerName AS CustomerName,Customer.CustomerCode AS CustomerCode,CustomerOldInvoiceHeader.SaleNo AS InvoiceNo,CustomerOldInvoiceHeader.SalePDate AS InvoiceDate,CAST (CASE WHEN CustomerInvoicePayment.UniqueId IS NULL  THEN 0 ELSE 1 END AS INT) AS HasPayment,CAST (CustomerOldInvoiceHeader.TotalAmount - CustomerOldInvoiceHeader.PayAmount AS REAL) AS RemAmount
    FROM  CustomerOldInvoiceHeader ,  Customer ON CustomerOldInvoiceHeader.CustomerId = Customer.UniqueId  LEFT JOIN  CustomerInvoicePayment ON CustomerInvoicePayment.InvoiceId = CustomerOldInvoiceHeader.UniqueId;

-- ----------------------------
-- View structure for OperationReport
-- ----------------------------
DROP VIEW IF EXISTS "main"."OperationReport";
CREATE VIEW "OperationReport" AS
SELECT
	Customer.UniqueId,
	Customer.CustomerName,
	Customer.CustomerCode,
	Customer.StoreName,
	IFNULL(AmountCheque,0) As AmountCheque,
	IFNULL(AmountCard,0) As AmountCard,
	IFNULL(AmountCash,0) As AmountCash,
	IFNULL(AmountDiscount,0) As AmountDiscount,
	IFNULL(AmountCredit,0) As AmountCredit,
	IFNULL(TotalAmount,0) As TotalAmount,
	IFNULL(OrderDiscountAmount,0) As OrderDiscountAmount,
	IFNULL(OrderAddAmount,0) As OrderAddAmount,
	IFNULL(ReturnDiscountAmount,0) As ReturnDiscountAmount,
	IFNULL(ReturnAddAmount,0) As ReturnAddAmount,
	IFNULL(TotalAmount,0) - IFNULL(OrderDiscountAmount,0) + IFNULL(OrderAddAmount,0) As TotalNetAmount,
  IFNULL(AmountCheque,0) + IFNULL(AmountCard,0) + IFNULL(AmountCash,0) +  IFNULL(AmountDiscount,0) +  IFNULL(AmountCredit,0) AS TotalPaidAmount
  ,IFNULL(TotalAmount,0) - IFNULL(OrderDiscountAmount,0) + IFNULL(OrderAddAmount,0) - IFNULL(AmountCheque,0) + IFNULL(AmountCard,0) + IFNULL(AmountCash,0) +  IFNULL(AmountDiscount,0) +  IFNULL(AmountCredit,0)  AS Recipe
FROM
	Customer JOIN
	( SELECT CustomerCallOrder.CustomerUniqueId,
			SUM(RequestDis1Amount + RequestDis2Amount + RequestDis3Amount) as OrderDiscountAmount,
			SUM(RequestAdd1Amount + RequestAdd2Amount) as OrderAddAmount,
			TotalQty * CustomerPrice.Price AS TotalAmount
		FROM CustomerCallOrder
		JOIN CustomerCallOrderLines ON CustomerCallOrderLines.OrderUniqueId = CustomerCallOrder.UniqueId
		JOIN (SELECT SUM(Qty * ConvertFactor) AS TotalQty, OrderLineUniqueId
		FROM	CustomerCallOrderLinesOrderQtyDetail
		JOIN ProductUnit ON ProductUnit.UniqueId = CustomerCallOrderLinesOrderQtyDetail.ProductUnitId
		GROUP BY OrderLineUniqueId) AS OrerDetail
		ON OrerDetail.OrderLineUniqueId = CustomerCallOrderLines.UniqueId
		LEFT JOIN CustomerPrice ON 	CustomerPrice.CustomerUniqueId = CustomerCallOrder.CustomerUniqueId
		AND CustomerPrice.ProductUniqueId = CustomerCallOrderLines.ProductUniqueId
		GROUP BY CustomerCallOrder.CustomerUniqueId
	) AS CustomerOrder ON CustomerOrder.CustomerUniqueId = Customer.UniqueId
	LEFT JOIN
	( SELECT CustomerUniqueId,
			SUM(CustomerCallReturnLines.TotalReturnDis1 + CustomerCallReturnLines.TotalReturnDis2  + CustomerCallReturnLines.TotalReturnDis3) as ReturnDiscountAmount,
			SUM(TotalReturnAdd1 + TotalReturnAdd2) as ReturnAddAmount
		FROM CustomerCallReturn
		JOIN CustomerCallReturnLines ON CustomerCallReturnLines.ReturnUniqueId = CustomerCallReturn.UniqueId
		GROUP BY CustomerUniqueId
	) AS CustomerReturn ON CustomerReturn.CustomerUniqueId = Customer.UniqueId
	LEFT JOIN
	( SELECT CustomerId, IFNULL(Sum(Amount),0) AS AmountCheque
				FROM Payment
				WHERE PaymentType = 'e3a93634-ae20-4d57-8e27-eee7b768adfc'
				GROUP BY CustomerId) AS PaymentCheque ON PaymentCheque.CustomerId = Customer.UniqueId
	LEFT JOIN
	( 	SELECT CustomerId, IFNULL(Sum(Amount),0) AS AmountCard
				FROM Payment
				WHERE PaymentType = 'f1b06da6-122d-4427-abd0-84a7cf72b29c'
				GROUP BY CustomerId) AS PaymentCard ON PaymentCard.CustomerId = Customer.UniqueId
	LEFT JOIN
	( 	SELECT CustomerId, IFNULL(Sum(Amount),0) AS AmountCash
				FROM Payment
				WHERE PaymentType = '837689e8-2115-4085-bf7f-0d0da86f3d71'
				GROUP BY CustomerId) AS PaymentCash ON PaymentCash.CustomerId = Customer.UniqueId
	LEFT JOIN
	( 	SELECT CustomerId, IFNULL(Sum(Amount),0) AS AmountDiscount
				FROM Payment
				WHERE PaymentType = 'df7e99c9-2ed9-436a-b9a3-8ec0f4e86651'
				GROUP BY CustomerId) AS PaymentDiscount ON PaymentDiscount.CustomerId = Customer.UniqueId
	LEFT JOIN
	( 	SELECT CustomerId, IFNULL(Sum(Amount),0) AS AmountCredit
				FROM Payment
				WHERE PaymentType = '56c7d3ee-4d18-4c5c-bbbd-aacc6bebd862'
				GROUP BY CustomerId) AS PaymentCredit ON PaymentCredit.CustomerId = Customer.UniqueId;

-- ----------------------------
-- View structure for OrderPrizeView
-- ----------------------------
DROP VIEW IF EXISTS "main"."OrderPrizeView";
CREATE VIEW "OrderPrizeView" AS
SELECT
OrderPrize.UniqueId AS UniqueId,
OrderPrize.DiscountId AS DiscountId,
OrderPrize.ProductId AS ProductId,
OrderPrize.TotalQty AS TotalQty,
OrderPrize.DisRef AS DisRef,
OrderPrize.CustomerId AS CustomerId,
Product.ProductName AS ProductName,
Product.ProductCode AS ProductCode,
Product.BackOfficeId AS GoodsRef
FROM
OrderPrize
INNER JOIN Product ON OrderPrize.ProductId = Product.UniqueId;

-- ----------------------------
-- View structure for OrderView
-- ----------------------------
DROP VIEW IF EXISTS "main"."OrderView";
CREATE VIEW OrderView
AS
SELECT UniqueId,OrderUniqueId,CustomerUniqueId,RequestAdd1Amount,RequestAdd2Amount,RequestTaxAmount,RequestChargeAmount,RequestDis1Amount,RequestDis2Amount,RequestDis3Amount,RequestOtherDiscountAmount,InvoiceAmount,InvoiceAdd1Amount,InvoiceAdd2Amount,InvoiceTaxAmount,InvoiceChargeAmount,InvoiceOtherDiscountAmount,InvoiceDis1Amount,InvoiceDis2Amount,InvoiceDis3Amount,ProductName,ProductCode,IsFreeItem,UnitPrice,PriceId,ProductId,group_concat(Qty,'|') AS Qty,group_concat(ConvertFactor,'|') AS ConvertFactor,group_concat(UnitName,'|') AS UnitName,group_concat(ProductUnitId,'|') AS ProductUnitId,AllConvertFactors,AllUnitNames,sum(TotalQty) AS TotalQty,IsRequestFreeItem,EmphaticType,EmphaticProductCount,SaleDate,RequestAmount,OnHandQty,ProductTotalOrderedQty,group_concat(FreeReasonId,'|') AS FreeReasonId,group_concat(FreeReasonName,'|') AS FreeReasonName
    FROM  CustomerCallOrderOrderView
    GROUP BY CustomerUniqueId,ProductId,IsRequestFreeItem,OrderUniqueId;

-- ----------------------------
-- View structure for PathCustomerView
-- ----------------------------
DROP VIEW IF EXISTS "main"."PathCustomerView";
CREATE VIEW "PathCustomerView" AS
SELECT * FROM Customer LEFT JOIN VisitTemplatePathCustomer ON Customer.UniqueId = VisitTemplatePathCustomer.CustomerId;

-- ----------------------------
-- View structure for PictureCustomerView
-- ----------------------------
DROP VIEW IF EXISTS "main"."PictureCustomerView";
CREATE VIEW "PictureCustomerView" AS
SELECT
	PictureCustomer.UniqueId AS UniqueId,
	PictureCustomer.PictureSubjectId AS PictureSubjectId,
	PictureCustomer.CustomerId AS CustomerId,
	PictureCustomer.DemandTypeUniqueId AS DemandTypeUniqueId,
	PictureCustomer.DemandType AS DemandType,
	PictureCustomer.NoPictureReason AS NoPictureReason,
	PictureSubject.Title AS Title,
	ifnull(
		PictureCustomerHistory.UniqueId,
		'0'
	) AS AlreadyTaken,
	group_concat(PictureFile.FileId, ':') AS FileIds,
	count(PictureFile.FileId) AS FileCount
FROM
	PictureCustomer
INNER JOIN PictureSubject ON PictureSubject.UniqueId = PictureCustomer.PictureSubjectId
LEFT JOIN PictureFile ON PictureFile.PictureSubjectId = PictureCustomer.PictureSubjectId
AND PictureFile.CustomerId = PictureCustomer.CustomerId
LEFT JOIN PictureCustomerHistory ON PictureCustomerHistory.CustomerId = PictureCustomer.CustomerId
AND PictureCustomerHistory.PictureSubjectId = PictureCustomer.PictureSubjectId
GROUP BY
	PictureCustomer.UniqueId;

-- ----------------------------
-- View structure for ProductGroupCatalogView
-- ----------------------------
DROP VIEW IF EXISTS "main"."ProductGroupCatalogView";
CREATE VIEW "ProductGroupCatalogView" AS
SELECT ProductGroupCatalog.* FROM ProductGroupCatalog
INNER JOIN ProductGroup ON ProductGroup.UniqueId = ProductGroupCatalog.ProductMainGroupId
INNER JOIN ProductGroup SubProductGroup ON ProductGroup.UniqueId = SubProductGroup.ProductGroupParentId
INNER JOIN Product ON Product.ProductGroupId = SubProductGroup.UniqueId
and Product.IsForSale = 1
GROUP BY ProductGroupCatalog.ProductMainGroupId;

-- ----------------------------
-- View structure for ProductOrderQtyHistoryView
-- ----------------------------
DROP VIEW IF EXISTS "main"."ProductOrderQtyHistoryView";
CREATE VIEW "ProductOrderQtyHistoryView" AS
SELECT
	CustomerOldInvoiceHeader.CustomerId AS CustomerId,
	CustomerOldInvoiceDetail.ProductId AS ProductId,
	SaleDate,
	ifnull(
		CustomerOldInvoiceDetail.TotalQty,
		0
	) AS TotalQty
FROM
	"CustomerOldInvoiceDetail"
JOIN CustomerOldInvoiceHeader ON CustomerOldInvoiceDetail.SaleId = CustomerOldInvoiceHeader.UniqueId;

-- ----------------------------
-- View structure for ProductUnitView
-- ----------------------------
DROP VIEW IF EXISTS "main"."ProductUnitView";
CREATE VIEW ProductUnitView
AS
SELECT ProductUnit.UniqueId AS UniqueId,Unit.BackOfficeId AS UnitRef,ProductUnit.IsForReturn AS IsForReturn,ProductUnit.IsForSale AS IsForSale,ProductUnit.IsDefault AS IsDefault,ProductName,ProductId,Product.BackOfficeId,ProductCode,Unit.UniqueId AS UnitId,ConvertFactor,UnitName,CASE WHEN (Product.ProductTypeId = 'EDFDADE5-45B5-41AF-BCB0-BEA2AE733789') AND (ProductUnit.UnitStatusId = '1f68ee36-13d7-4a3c-a082-fe6014ac77cc') THEN 1 ELSE 0 END AS Decimal
    FROM  Product ,  ProductUnit ON Product.UniqueId = ProductUnit.ProductId ,  Unit ON Unit.UniqueId = ProductUnit.UnitId
    ORDER BY ConvertFactor DESC;

-- ----------------------------
-- View structure for QuestionnaireCustomerView
-- ----------------------------
DROP VIEW IF EXISTS "main"."QuestionnaireCustomerView";
CREATE VIEW "QuestionnaireCustomerView" AS
SELECT
	QuestionnaireHeader.UniqueId AS QuestionnaireId,
	QuestionnaireHeader.Title,
	QuestionnaireCustomer.CustomerId,
	QuestionnaireCustomer.DemandTypeId,
	QuestionnaireCustomer.DemandType,
	QuestionnaireCustomer.UniqueId,
	QuestionnaireAnswer.AttachmentId,
	CASE
WHEN QuestionnaireAnswer.QuestionnaireId IS NULL THEN
	'0'
ELSE
	'1'
END AS HasAnswer,
 CASE
WHEN QuestionnaireHistory.UniqueId IS NULL THEN
	'0'
ELSE
	'1'
END AS AlreadyAnswered,
 QuestionnaireCustomer.NoAnswerReason
FROM
	QuestionnaireCustomer
INNER JOIN QuestionnaireHeader ON QuestionnaireHeader.UniqueId == QuestionnaireCustomer.QuestionnaireId
LEFT JOIN QuestionnaireAnswer ON QuestionnaireAnswer.CustomerId == QuestionnaireCustomer.CustomerId
AND QuestionnaireAnswer.QuestionnaireId == QuestionnaireCustomer.QuestionnaireId
LEFT JOIN QuestionnaireHistory ON QuestionnaireHistory.CustomerId == QuestionnaireCustomer.CustomerId
AND QuestionnaireHistory.QuestionnaireId == QuestionnaireCustomer.QuestionnaireId
GROUP BY
	QuestionnaireCustomer.UniqueId;

-- ----------------------------
-- View structure for Rep3013View
-- ----------------------------
DROP VIEW IF EXISTS "main"."Rep3013View";
CREATE VIEW "Rep3013View" AS
SELECT
UniqueId as UniqueId,
CustRemAmountForSaleOffice,
CustRemAmountAll,
CustomerName,
CustomerCode
FROM
 Customer
WHERE
CustRemAmountAll <> 0 OR CustRemAmountForSaleOffice <> 0;

-- ----------------------------
-- View structure for RequestLineView
-- ----------------------------
DROP VIEW IF EXISTS "main"."RequestLineView";
CREATE VIEW RequestLineView AS
SELECT
	Product.UniqueId,
	Product.ProductCode,
	Product.ProductName,
	Product.ProductGroupId,
	RequestLine.RowIndex,
	RequestLine.BulkQty,
	RequestLine.BulkQtyUnitUniqueId,
	RequestLine.UniqueId AS RequestLineUniqueId,
	group_concat(RequestLineQty.Qty,':') AS Qty,
	group_concat(RequestLineQty.ProductUnitId,':') AS ProductUnitId,
	group_concat(ProductUnitView.UnitName,':') as UnitName,
	CAST(group_concat(ProductUnitView.ConvertFactor,':') as real) as ConvertFactor,
	CAST(SUM(RequestLineQty.Qty * ProductUnitView.ConvertFactor) as real) as TotalQty,
	PR.SalePrice AS UnitPrice,
	CAST(SUM(RequestLineQty.Qty * ProductUnitView.ConvertFactor) * PR.SalePrice as real) as TotalPrice
FROM
	Product
	JOIN (
SELECT * FROM	Product
	JOIN PriceHistory PR
		 on PR.GoodsRef= Product.BackOfficeId
 		 AND pr.BackOfficeId = (
 				 Select tP2.BackOfficeId
 				 From PriceHistory tP2
				 Where tP2.GoodsRef=Product.BackOfficeId
				 AND ((SELECT Value FROM SysConfig WHERE UniqueId = '94E76D64-62B7-430D-B5F9-420695C8DB66') BETWEEN tP2.StartDate AND IFNULL(tP2.EndDate, (SELECT Value FROM SysConfig WHERE UniqueId = '94E76D64-62B7-430D-B5F9-420695C8DB66')))
				 LIMIT 1
 		 ) LIMIT 1) as PR
LEFT JOIN RequestLine ON Product.UniqueId = RequestLine.ProductId
LEFT JOIN RequestLineQty ON RequestLine.UniqueId = RequestLineQty.RequestLineUniqueId
LEFT JOIN ProductUnitView ON ProductUnitView.UniqueId = RequestLineQty.ProductUnitId
WHERE
Product.IsForRequest = 1
GROUP BY
	Product.UniqueId;

-- ----------------------------
-- View structure for RequestReportView
-- ----------------------------
DROP VIEW IF EXISTS "main"."RequestReportView";
CREATE VIEW RequestReportView AS
SELECT
	Customer.UniqueId,
	Customer.CustomerName,
	Customer.CustomerCode,
	Customer.StoreName,
	CustomerCallOrder.UniqueId AS OrderUniqueId,
	PaymentTypeOrder.PaymentTypeOrderName AS PaymentTypeBaseName,
	SUM(CustomerCallOrderOrderView.RequestAmount) AS TotalOrderNetAmount,
    CustomerCallOrder.LocalPaperNo AS LocalPaperNo,
	group_concat(CustomerCall.CallType , ':') as CallType,
	group_concat(CustomerCall.ConfirmStatus , ':') as ConfirmStatus,
	(
		NOT CustomerCallReturn.UniqueId IS NULL
	) AS HasReturn,
CustomerCallOrderOrderView.RequestDis1Amount + CustomerCallOrderOrderView.RequestDis2Amount + CustomerCallOrderOrderView.RequestDis3Amount as Discount
FROM
	Customer
JOIN CustomerCallOrder ON CustomerCallOrder.CustomerUniqueId = Customer.UniqueId
JOIN CustomerCallOrderOrderView ON CustomerCallOrderOrderView.OrderUniqueId = CustomerCallOrder.UniqueId
JOIN PaymentTypeOrder ON CustomerCallOrder.OrderPaymentTypeUniqueId = PaymentTypeOrder.UniqueId
LEFT JOIN CustomerCallReturn ON CustomerCallReturn.CustomerUniqueId = Customer.UniqueId
LEFT JOIN CustomerCall ON CustomerCall.CustomerId = Customer.UniqueId
GROUP BY
	Customer.UniqueId, CustomerCallOrder.UniqueId;

-- ----------------------------
-- View structure for ReturnReportView
-- ----------------------------
DROP VIEW IF EXISTS "main"."ReturnReportView";
CREATE VIEW ReturnReportView AS
SELECT
	group_concat(ReturnProductTypeId, ':') AS ReturnProductTypeId,
	group_concat(ReturnReasonId, ':') AS ReturnReasonId,
	CustomerCallReturnLinesView.ProductName AS ProductName,
	CustomerCallReturnLinesView.ProductCode AS ProductCode,
	CustomerCallReturnLinesView.ProductId AS ProductId,
	group_concat(
		CustomerCallReturnLinesView.ConvertFactor,
		'|'
	) AS ConvertFactor,
	group_concat(
		CustomerCallReturnLinesView.ProductUnitId,
		'|'
	) AS ProductUnitId,
	group_concat(
		CustomerCallReturnLinesView.Qty,
		'|'
	) AS Qty,
	group_concat(
		CustomerCallReturnLinesView.UnitName,
		'|'
	) AS UnitName,
	sum(
		CustomerCallReturnLinesView.TotalReturnQty
	) AS TotalReturnQty,
	CustomerCallReturnLinesView.RequestUnitPrice,
	sum(CustomerCallReturnLinesView.TotalRequestAmount) AS TotalRequestAmount
FROM
	CustomerCallReturnLinesView
JOIN ReturnReason ON ReturnReason.UniqueId = CustomerCallReturnLinesView.ReturnReasonId
LEFT JOIN CustomerOldInvoiceDetail ON CustomerOldInvoiceDetail.ProductId = CustomerCallReturnLinesView.ProductId
and CustomerOldInvoiceDetail.SaleId = CustomerCallReturnLinesView.InvoiceId
LEFT JOIN CustomerOldInvoiceHeader ON CustomerOldInvoiceHeader.UniqueId = CustomerOldInvoiceDetail.SaleId
AND CustomerOldInvoiceDetail.SaleId = CustomerCallReturnLinesView.InvoiceId
GROUP BY
	CustomerCallReturnLinesView.ProductId;

-- ----------------------------
-- View structure for TempReportPreView
-- ----------------------------
DROP VIEW IF EXISTS "main"."TempReportPreView";
CREATE VIEW "TempReportPreView" AS
SELECT
	CustomerCallOrderLines.OrderUniqueId AS OrderUniqueId,
	CustomerCallOrderLines.UniqueId AS OrderLineUniqueId,
	Product.ProductCode,
	Product.ProductName AS ProductName,
	ProductUniqueId AS ProductId,
	7000 AS UnitPrice,
	70000 AS TotalPrice,
	5000 AS Discont,
	9000 AS Tax,
	12454 AS RequestAmount,
	Qty,
	group_concat(ConvertFactor, ':') AS ConvertFactor,
	'' AS ProductUnitId,
	UnitName,
	sum(Qty * ConvertFactor) AS TotalQty
FROM
	CustomerCallOrderLines
JOIN (
	SELECT
		ProductUnitId,
		OrderLineUniqueId,
		1 AS ConvertFactor,
		group_concat(Qty, ':') AS Qty,
		group_concat('عدد', ':') AS UnitName
	FROM
		CustomerCallOrderLinesOrderQtyDetail
	GROUP BY
		ProductUnitId,
		OrderLineUniqueId
) AS qt ON qt.OrderLineUniqueId = CustomerCallOrderLines.UniqueId
JOIN Product ON Product.UniqueId = CustomerCallOrderLines.ProductUniqueId;

-- ----------------------------
-- View structure for TotalProductSaleView
-- ----------------------------
DROP VIEW IF EXISTS "main"."TotalProductSaleView";
CREATE VIEW "TotalProductSaleView" AS SELECT
	sum(
		ifnull(
			CustomerOldInvoiceDetail.TotalQty,
			0
		)
	) AS TotalQty,
	CustomerOldInvoiceDetail.ProductId,
	CustomerOldInvoiceDetail.ProductId AS UniqueId,
	CustomerOldInvoiceHeader.CustomerId,
	count(*) AS InvoiceCount
FROM
	"CustomerOldInvoiceDetail"
JOIN CustomerOldInvoiceHeader ON CustomerOldInvoiceDetail.SaleId = CustomerOldInvoiceHeader.UniqueId
GROUP BY
	ProductId,
	CustomerId;

-- ----------------------------
-- View structure for WarehouseProductQtyView
-- ----------------------------
DROP VIEW IF EXISTS "main"."WarehouseProductQtyView";
CREATE VIEW "WarehouseProductQtyView" AS
SELECT
Product.UniqueId AS UniqueId,
Product.ProductCode,
Product.ProductName,
Product.ProductTypeId,
ProductUnitsView.UnitName AS UnitName,
ProductUnitsView.ConvertFactor as ConvertFactor,
ProductUnitsView.ProductUnitId as ProductUnitId,
OnHandQty.OnHandQty,
OnHandQty.RenewQty,
TotalProductOrderQtyView.TotalQty,
PR.SalePrice,
PR.SalePrice * (OnHandQty.OnHandQty - TotalProductOrderQtyView.TotalQty) as RemainedPriceQty
FROM
OnHandQty
JOIN ProductUnitsView ON ProductUnitsView.UniqueId = OnHandQty.ProductId
JOIN Product On Product.UniqueId = OnHandQty.ProductId
JOIN PriceHistory PR
     on PR.GoodsRef= Product.BackOfficeId
      AND pr.BackOfficeId = (
          Select tP2.BackOfficeId
          From PriceHistory tP2
         Where tP2.GoodsRef=Product.BackOfficeId
         AND ((SELECT Value FROM SysConfig WHERE UniqueId = '94E76D64-62B7-430D-B5F9-420695C8DB66') BETWEEN tP2.StartDate AND IFNULL(tP2.EndDate, (SELECT Value FROM SysConfig WHERE UniqueId = '94E76D64-62B7-430D-B5F9-420695C8DB66')))
         LIMIT 1
      )
LEFT JOIN TotalProductOrderQtyView ON TotalProductOrderQtyView.ProductId = OnHandQty.ProductId
GROUP BY Product.UniqueId;

-- ----------------------------
-- Indexes structure for table ContractPriceSDS
-- ----------------------------
CREATE INDEX "main"."CustCtgrIndex"
ON "ContractPriceSDS" ("CustCtgrRef" ASC);
CREATE INDEX "main"."CustLevelIndex"
ON "ContractPriceSDS" ("CustLevelRef" ASC);
CREATE INDEX "main"."CustomerIndex"
ON "ContractPriceSDS" ("CustRef" ASC);
CREATE INDEX "main"."ProductIndex"
ON "ContractPriceSDS" ("GoodsRef" ASC);

-- ----------------------------
-- Indexes structure for table CustomerCallOrder
-- ----------------------------
CREATE UNIQUE INDEX "main"."uniqueid"
ON "CustomerCallOrder" ("UniqueId" ASC);

-- ----------------------------
-- Indexes structure for table CustomerCallOrderLinesOrderQtyDetail
-- ----------------------------
CREATE INDEX "main"."order_line_unique_id"
ON "CustomerCallOrderLinesOrderQtyDetail" ("OrderLineUniqueId" COLLATE NOCASE ASC);

-- ----------------------------
-- Indexes structure for table CustomerEmphaticProduct
-- ----------------------------
CREATE INDEX "main"."customer_emphatic_product_product_count_idx"
ON "CustomerEmphaticProduct" ("ProductCount" COLLATE NOCASE ASC);
CREATE INDEX "main"."customer_emphatic_product_product_id_idx"
ON "CustomerEmphaticProduct" ("ProductId" COLLATE NOCASE ASC);
CREATE INDEX "main"."customer_emphatic_product_product_type_idx"
ON "CustomerEmphaticProduct" ("Type" COLLATE NOCASE ASC);

-- ----------------------------
-- Indexes structure for table CustomerInventory
-- ----------------------------
CREATE INDEX "main"."CustonerInventory_Customerid_Idx"
ON "CustomerInventory" ("CustomerId" COLLATE NOCASE ASC);
CREATE INDEX "main"."CustonerInventory_ProductId_Idx"
ON "CustomerInventory" ("ProductId" COLLATE NOCASE ASC);

-- ----------------------------
-- Indexes structure for table CustomerInventoryQty
-- ----------------------------
CREATE INDEX "main"."CustonerInventory_Qty_PriductUnitId_Idx"
ON "CustomerInventoryQty" ("ProductUnitId" COLLATE NOCASE ASC);

-- ----------------------------
-- Indexes structure for table CustomerOldInvoiceDetail
-- ----------------------------
CREATE INDEX "main"."customer_old_invoice_detail_product_id_idx"
ON "CustomerOldInvoiceDetail" ("ProductId" ASC);
CREATE INDEX "main"."customer_old_invoice_detail_sale_id_idx"
ON "CustomerOldInvoiceDetail" ("SaleId" ASC);

-- ----------------------------
-- Indexes structure for table CustomerOldInvoiceHeader
-- ----------------------------
CREATE INDEX "main"."new_customer_old_invoice_header_customer_id_idx"
ON "CustomerOldInvoiceHeader" ("CustomerId" ASC);
CREATE INDEX "main"."new_customer_old_invoice_header_sale_date_idx"
ON "CustomerOldInvoiceHeader" ("SaleDate" ASC);

-- ----------------------------
-- Indexes structure for table CustomerPrice
-- ----------------------------
CREATE INDEX "main"."CustomerPriceCustomerIndex"
ON "CustomerPrice" ("CustomerUniqueId" ASC);
CREATE INDEX "main"."CustomerPriceProductIndex"
ON "CustomerPrice" ("ProductUniqueId" ASC);

-- ----------------------------
-- Indexes structure for table CustomerProductPrize
-- ----------------------------
CREATE INDEX "main"."CustomerProductPrizeCustomerIndex"
ON "CustomerProductPrize" ("CustomerId" ASC);
CREATE INDEX "main"."CustomerProductPrizeProductIndex"
ON "CustomerProductPrize" ("ProductId" ASC);

-- ----------------------------
-- Indexes structure for table PaymentTypeOrder
-- ----------------------------
CREATE INDEX "main".""
ON "PaymentTypeOrder" ("UniqueId" ASC);

-- ----------------------------
-- Indexes structure for table PriceHistory
-- ----------------------------
CREATE INDEX "main"."CCCC"
ON "PriceHistory" ("CustRef" ASC, "CustActRef" ASC, "CustLevelRef" ASC, "StateRef" ASC);
CREATE INDEX "main"."DCRefASC"
ON "PriceHistory" ("DCRef" ASC);
CREATE INDEX "main"."DCRefDESC"
ON "PriceHistory" ("DCRef" DESC);
CREATE INDEX "main"."ENDDATEASC"
ON "PriceHistory" ("EndDate" ASC);
CREATE INDEX "main"."ENDDATEDESC"
ON "PriceHistory" ("EndDate" DESC);
CREATE INDEX "main"."STARTDATEASC"
ON "PriceHistory" ("StartDate" ASC);
CREATE INDEX "main"."STARTDATEDESC"
ON "PriceHistory" ("StartDate" DESC);
CREATE INDEX "main"."UniqueIdASC"
ON "PriceHistory" ("UniqueId" ASC);
CREATE INDEX "main"."UniqueIdDESC"
ON "PriceHistory" ("UniqueId" DESC);
CREATE INDEX "main"."XXXXX"
ON "PriceHistory" ("GoodsRef" ASC, "CustRef" DESC, "CustActRef" DESC, "CustLevelRef" DESC, "StateRef" DESC);

-- ----------------------------
-- Indexes structure for table Product
-- ----------------------------
CREATE INDEX "main"."product_is_for_sale_idx"
ON "Product" ("IsForSale" COLLATE NOCASE ASC);
CREATE UNIQUE INDEX "main"."product_product_code_idx"
ON "Product" ("ProductCode" ASC);
CREATE INDEX "main"."product_product_group_id_idx"
ON "Product" ("ProductGroupId" COLLATE RTRIM ASC);
CREATE INDEX "main"."product_product_name_idx"
ON "Product" ("ProductName" ASC);
CREATE UNIQUE INDEX "main"."product_unique_idx"
ON "Product" ("UniqueId" ASC);

-- ----------------------------
-- Indexes structure for table ProductBoGroup
-- ----------------------------
CREATE INDEX "main"."BackofficeId"
ON "ProductBoGroup" ("BackOfficeId" ASC);

-- ----------------------------
-- Indexes structure for table ProductMainSubType
-- ----------------------------
CREATE INDEX "main"."GoodIndex"
ON "ProductMainSubType" ("GoodsRef" ASC);

-- ----------------------------
-- Indexes structure for table ProductUnit
-- ----------------------------
CREATE INDEX "main"."product_unit_productid_idx"
ON "ProductUnit" ("ProductId" COLLATE NOCASE ASC);
