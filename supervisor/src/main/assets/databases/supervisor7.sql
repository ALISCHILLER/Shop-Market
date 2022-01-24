-- ----------------------------
-- Table ReviewreportModel for Customer
-- ----------------------------

CREATE TABLE "Reviewreport" (
"UniqueId" TEXT NOT NULL COLLATE NOCASE,
"orderNumber"  TEXT ,
"customerUniqueId"  TEXT  COLLATE NOCASE ,
"orderStatus" TEXT ,
"orderDate"  TEXT ,
"dealerCode" TEXT,
"dealerName" TEXT,
"customerCode" TEXT,
"customerName" TEXT,
"paymentType"  TEXT,
"comment"      TEXT,
"customerCategory" TEXT,

PRIMARY KEY ("UniqueId" ASC)
);
-- ----------------------------
-- Table Items for Items
-- ----------------------------

CREATE TABLE "Items" (
"UniqueId" TEXT NOT NULL COLLATE NOCASE,
"productCategory"  TEXT ,
"amount"  TEXT  COLLATE NOCASE ,
"productCode" TEXT ,
"productName"  TEXT ,
"productCount" TEXT,
"productCountStr" TEXT,
"tax" INT,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table ProductInvoiveBalanceReport for
-- ----------------------------

CREATE TABLE "ProductInvoiveBalanceReport" (
"UniqueId" TEXT  COLLATE NOCASE,
"CustomerBackOfficeCode" TEXT NOT NULL COLLATE NOCASE,
"CustomerName"  TEXT ,
"InvoiceNumber"  TEXT  COLLATE NOCASE ,
"InvoiceShmsiDate" TEXT ,
"InvoiceOverDue"  TEXT ,
"InvoiceFinalPrice" DECIMAL,
"PaidPose" DECIMAL,
"PaidCash" DECIMAL,
"PaidCheck" DECIMAL,
"IvoiceRemain" DECIMAL,
"UsancePaid" DECIMAL,
PRIMARY KEY ("UniqueId" ASC)
);
-- ----------------------------
-- Table ProductCustomerGroupSalesSummary for
-- ----------------------------

CREATE TABLE "ProductCustomerGroupSalesSummary" (
"UniqueId" TEXT  COLLATE NOCASE,
"CustomerGroup" TEXT NOT NULL COLLATE NOCASE,
"CustomerGroupTXT"  TEXT ,
"CustomerActivity"  TEXT  COLLATE NOCASE ,
"CustomerActivityTXT" TEXT ,
"NetWeight"  DECIMAL ,
"NetCount_CA" DECIMAL,
PRIMARY KEY ("UniqueId" ASC)
);
-- ----------------------------
-- Table ProductCustomerGroupSalesSummary for
-- ----------------------------

CREATE TABLE "TProductsPurchaseHistoryReport" (
"UniqueId" TEXT  COLLATE NOCASE,
"ProductBackOfficeCode" TEXT NOT NULL COLLATE NOCASE,
"ProductName"  TEXT ,
"ProductCategoryCode"  TEXT  COLLATE NOCASE ,
"ProductCategoryName" TEXT ,
"ProductNetWeight"  DECIMAL ,
"ProductNetCount_CA" DECIMAL,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table CustomerNoSaleModel for
-- ----------------------------

CREATE TABLE "CustomerNoSale" (
"UniqueId" TEXT  COLLATE NOCASE,
"BackOfficeId" INT,
"CustomerCode" TEXT NOT NULL COLLATE NOCASE,
"CustomerName"  TEXT ,
"Address"  TEXT  COLLATE NOCASE ,
"Phone" TEXT ,
"StoreName" TEXT ,
"Mobile" TEXT ,
"Longitude"  DOUBLE ,
"Latitude" DOUBLE,
"CustomerActivity" TEXT ,
"CustomerCategory" TEXT ,
"CityId" TEXT  COLLATE NOCASE,
"NationalCode"TEXT,

--"Description" TEXT ,
--"OPathId"   INT,
--"ParentCustomerId" INT,
--"IgnoreLocation" INT,
--"PayableTypes"INT,
--"Barcode" TEXT  ,
--"RealName"TEXT,
--"DCName"TEXT,
--"CustomerPostalCode"TEXT,
--"CityZone" INT,
--"ErrorMessage"TEXT,
--"ErrorType"INT,
--"AmountChq"DOUBLE,
--"CountChq"INT,
--"CustomerSubGroup1Id"TEXT,
--"CustomerSubGroup2Id"TEXT,
--"DCRef" INTEGER,
--"DcCode"TEXT,
--"AreaId"TEXT  COLLATE NOCASE,
--"CenterId"TEXT  COLLATE NOCASE,
--"StateCode"TEXT  ,
--"OwnerTypeCode"TEXT  ,
--"OwnerTypeRef"INT  ,
--"CityArea" TEXT ,
--"CustLevelCode" TEXT,
--"CustActCode" TEXT,
--"CustCtgrCode" TEXT,
--"CountyRef" INT,
--"CountyCode"TEXT,
--"CityCode"INT,
--"DistZoneNo"INT,
--"DistZoneRef"INT,
--"DistAreaNo"INT,
--"DistAreaRef"INT,
--"DistPathNo"INT,
--"DistPathRef"INT,
--"SaleZoneNo"INT,
--"SaleZoneRef"INT,
--"SaleAreaNo"INT,
--"SaleAreaRef"INT,
--"SalePathNo"INT,
--"SalePathRef"INT,
--"IsNewCustomer"BIT,
--"EconomicCode" TEXT,
--"Alarm" TEXT,
--"rowIndex" INT,
--"checkDebit"BIT,
--"checkCredit"BIT,
--"ReturnChequeAmount"DECIMAL,
--"ReturnChequeCount"INT,
--"OpenChequeAmount"DECIMAL,
--"OpenChequeCount"INT,
--"OpenInvoiceAmount"DECIMAL,
--"OpenInvoiceCount"INT,
--"InitDebit"DECIMAL,
--"InitCredit"DECIMAL,
--"CustomerRemain"DECIMAL,
--"CustRemAmountAll"DECIMAL,
--"CustRemAmountForSaleOffice"DECIMAL,
--"RemainCredit"DECIMAL,
--"RemainDebit"DECIMAL,
--
--"RemainDebit"DECIMAL,
--"CustomerCategoryRef"INT,
--"CustomerActivityRef"INT,
--"CustomerLevelRef"INT,
--"CustomerCategoryId"TEXT  COLLATE NOCASE,
--"CustomerActivityId"TEXT  COLLATE NOCASE,
--"CustomerLevelId"TEXT  COLLATE NOCASE,
--"CityRef"INT,
--"StateRef"TEXT  COLLATE NOCASE,
--"StateId"TEXT  COLLATE NOCASE,
--"CountyId"TEXT  COLLATE NOCASE,
--"IsActive"BIT,
--"NationalCode"TEXT ,
PRIMARY KEY ("UniqueId" ASC)
);