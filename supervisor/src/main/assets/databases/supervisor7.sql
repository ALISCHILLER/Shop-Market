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
"number" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
"UniqueId" TEXT  COLLATE NOCASE,
"productCategory"  TEXT ,
"amount"  TEXT  COLLATE NOCASE ,
"productCode" TEXT ,
"productName"  TEXT ,
"productCount" TEXT,
"productCountStr" TEXT,
"tax" INT
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

PRIMARY KEY ("UniqueId" ASC)
);
-- ----------------------------
-- Table OrderStatusReportFlatModel for
-- ----------------------------

CREATE TABLE "OrderStatusReportFlatdbMode" (
"number" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
"level" INT,
"date" TEXT ,
"dealerName"  TEXT ,
"dealerCode"  TEXT  ,
"customerName" TEXT ,
"customerCode" TEXT ,
"orderWeight"  DOUBLE ,
"pendingOrderWeight" DOUBLE,
"inProgressOrderWeight"  DOUBLE ,
"undeliverdOrderWeight" DOUBLE,
"finalWeight"  DOUBLE ,
"deliverdOrderWeight" DOUBLE
);