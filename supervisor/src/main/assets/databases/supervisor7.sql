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
PRIMARY KEY ("CustomerBackOfficeCode" ASC)
);

