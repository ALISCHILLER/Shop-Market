-- ----------------------------
-- Alter Table structure for CustomerCardex
-- ----------------------------

DROP TABLE IF EXISTS "main"."CustomerCardexTemp";
CREATE TABLE "CustomerCardexTemp" (
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

INSERT INTO CustomerCardexTemp SELECT * FROM CustomerCardex ;

DROP TABLE IF EXISTS "main"."CustomerCardex";
CREATE TABLE "CustomerCardex" (
"UniqueId"  TEXT NOT NULL,
"CustomerUniqueId" TEXT,
"SortId" INTEGER,
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

INSERT INTO CustomerCardex  (
UniqueId,
CustomerUniqueId,
SortId,
Type,
VoucherNo ,
Date,
BedAmount,
BesAmount,
RemainAmount,
VoucherDate,
VoucherTypeName,
NotDueDate,
NotDueDateMiladi
) SELECT
UniqueId,
CustomerUniqueId,
RowId,
Type,
VoucherNo ,
Date,
BedAmount,
BesAmount,
RemainAmount,
VoucherDate,
VoucherTypeName,
NotDueDate,
NotDueDateMiladi
FROM CustomerCardexTemp ;
DROP TABLE IF EXISTS CustomerCardexTemp ;

-- ----------------------------
-- End Alter Table structure for CustomerCardex
-- ----------------------------


-- ----------------------------
-- Alter Table structure for Payment
-- ----------------------------
alter table Payment add column ChqAccountName text;
alter table Payment add column ChqBranchName text;
alter table Payment add column ChqBranchCode text;
alter table EVCItemStatuesCustomers add column OrderLineId text;

-- ----------------------------
-- Table structure for CustomerOldInvoiceDetailTemp
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerOldInvoiceDetailTemp";
CREATE TABLE "CustomerOldInvoiceDetailTemp" (
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
-- Table structure for CustomerOldInvoiceHeaderTemp
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerOldInvoiceHeaderTemp";
CREATE TABLE "CustomerOldInvoiceHeaderTemp" (
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
-- Table structure for CustomerCardexTemp
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerCardexTemp";
CREATE TABLE "CustomerCardexTemp" (
"UniqueId"  TEXT NOT NULL,
"CustomerUniqueId" TEXT,
"SortId" INTEGER,
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
WHERE CustomerOldInvoiceHeader.PayAmount < CustomerOldInvoiceHeader.TotalAmount
AND (not exists (SELECT 1 FROM CustomerOldInvoiceHeaderTemp))
UNION
SELECT
Customer.UniqueId AS CustomerId,
CustomerOldInvoiceHeaderTemp.SaleNo,
CustomerOldInvoiceHeaderTemp.SalePDate,
CustomerOldInvoiceHeaderTemp.TotalAmount,
CustomerOldInvoiceHeaderTemp.PayAmount,
CustomerOldInvoiceHeaderTemp.DealerName,
CustomerOldInvoiceHeaderTemp.DealerId,
CustomerOldInvoiceHeaderTemp.TotalAmount - CustomerOldInvoiceHeaderTemp.PayAmount AS RemAmount
FROM CustomerOldInvoiceHeaderTemp JOIN Customer ON Customer.UniqueId = CustomerOldInvoiceHeaderTemp.CustomerId
WHERE CustomerOldInvoiceHeaderTemp.PayAmount < CustomerOldInvoiceHeaderTemp.TotalAmount;

-- ----------------------------
-- View structure for OldInvoiceHeaderView
-- ----------------------------
DROP VIEW IF EXISTS "main"."OldInvoiceHeaderView";
CREATE VIEW "OldInvoiceHeaderView" AS
SELECT
	CustomerOldInvoiceHeader.UniqueId AS UniqueId,
	Customer.UniqueId AS CustomerUniqueId,
	CustomerOldInvoiceHeader.TotalAmount AS Amount,
	CustomerOldInvoiceHeader.PayAmount AS PayAmount,
	(
		CustomerOldInvoiceHeader.Dis1Amount + CustomerOldInvoiceHeader.Dis2Amount
	) + CustomerOldInvoiceHeader.Dis3Amount AS DiscountAmount,
	CustomerOldInvoiceHeader.TaxAmount AS TaxAmount,
	CustomerOldInvoiceHeader.CustomerId AS CustomerId,
	Customer.Address AS Address,
	Customer.CustomerName AS CustomerName,
	Customer.CustomerCode AS CustomerCode,
	CustomerOldInvoiceHeader.SaleNo AS InvoiceNo,
	CustomerOldInvoiceHeader.SaleRef AS InvoiceRef,
	CustomerOldInvoiceHeader.SalePDate AS InvoiceDate,
	CustomerOldInvoiceHeader.PaymentTypeOrderUniqueId as PaymentTypeOrderUniqueId,
	CAST (
		CASE
		WHEN CustomerInvoicePayment.UniqueId IS NULL THEN
			0
		ELSE
			1
		END AS INT
	) AS HasPayment,
	CAST (
		CustomerOldInvoiceHeader.TotalAmount - CustomerOldInvoiceHeader.PayAmount AS REAL
	) AS RemAmount
FROM
	CustomerOldInvoiceHeader,
	Customer ON CustomerOldInvoiceHeader.CustomerId = Customer.UniqueId
LEFT JOIN CustomerInvoicePayment ON CustomerInvoicePayment.InvoiceId = CustomerOldInvoiceHeader.UniqueId
WHERE (not exists (SELECT 1 FROM CustomerOldInvoiceHeaderTemp))
UNION
SELECT
	CustomerOldInvoiceHeaderTemp.UniqueId AS UniqueId,
	Customer.UniqueId AS CustomerUniqueId,
	CustomerOldInvoiceHeaderTemp.TotalAmount AS Amount,
	CustomerOldInvoiceHeaderTemp.PayAmount AS PayAmount,
	(
		CustomerOldInvoiceHeaderTemp.Dis1Amount + CustomerOldInvoiceHeaderTemp.Dis2Amount
	) + CustomerOldInvoiceHeaderTemp.Dis3Amount AS DiscountAmount,
	CustomerOldInvoiceHeaderTemp.TaxAmount AS TaxAmount,
	CustomerOldInvoiceHeaderTemp.CustomerId AS CustomerId,
	Customer.Address AS Address,
	Customer.CustomerName AS CustomerName,
	Customer.CustomerCode AS CustomerCode,
	CustomerOldInvoiceHeaderTemp.SaleNo AS InvoiceNo,
	CustomerOldInvoiceHeaderTemp.SaleRef AS InvoiceRef,
	CustomerOldInvoiceHeaderTemp.SalePDate AS InvoiceDate,
	CustomerOldInvoiceHeaderTemp.PaymentTypeOrderUniqueId as PaymentTypeOrderUniqueId,
	CAST (
		CASE
		WHEN CustomerInvoicePayment.UniqueId IS NULL THEN
			0
		ELSE
			1
		END AS INT
	) AS HasPayment,
	CAST (
		CustomerOldInvoiceHeaderTemp.TotalAmount - CustomerOldInvoiceHeaderTemp.PayAmount AS REAL
	) AS RemAmount
FROM
	CustomerOldInvoiceHeaderTemp,
	Customer ON CustomerOldInvoiceHeaderTemp.CustomerId = Customer.UniqueId
LEFT JOIN CustomerInvoicePayment ON CustomerInvoicePayment.InvoiceId = CustomerOldInvoiceHeaderTemp.UniqueId;

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
JOIN CustomerOldInvoiceHeader ON CustomerOldInvoiceHeader.UniqueId = CustomerOldInvoiceDetail.SaleId
WHERE (not exists (SELECT 1 FROM CustomerOldInvoiceDetailTemp))
UNION
SELECT
CustomerOldInvoiceHeaderTemp.CustomerId,
CustomerOldInvoiceDetailTemp.ProductId ,
CustomerOldInvoiceDetailTemp.AddAmount,
CustomerOldInvoiceDetailTemp.UnitQty,
CustomerOldInvoiceHeaderTemp.SaleNo,
CustomerOldInvoiceHeaderTemp.SalePDate,
CustomerOldInvoiceDetailTemp.TotalQty,
CustomerOldInvoiceDetailTemp.UnitName,
CustomerOldInvoiceDetailTemp.UnitPrice,
CustomerOldInvoiceDetailTemp.SaleId,
Product.ProductName,
Product.ProductCode,
Product.ProductGroupId,
CustomerOldInvoiceDetailTemp.UnitPrice * CustomerOldInvoiceDetailTemp.TotalQty AS TotalAmount,
CustomerCallReturnView.TotalReturnQty as TotalReturnQty
FROM CustomerOldInvoiceDetailTemp
JOIN Product ON CustomerOldInvoiceDetailTemp.ProductId = Product.UniqueId
LEFT JOIN CustomerCallReturnView ON CustomerCallReturnView.ProductId = Product.UniqueId AND
CustomerCallReturnView.InvoiceId = SaleId
JOIN CustomerOldInvoiceHeaderTemp ON CustomerOldInvoiceHeaderTemp.UniqueId = CustomerOldInvoiceDetailTemp.SaleId;

-- ----------------------------
-- Table structure for CustomerRemainPerLine
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerRemainPerLine";
CREATE TABLE "CustomerRemainPerLine" (
"UniqueId"  TEXT NOT NULL,
"CustomerId"  TEXT,
"CustRef"  INTEGER,
"CountRetChq"  INTEGER,
"AmountRetChq"  REAL,
"CountOpenFact"  INTEGER,
"AmountOpenFact"  REAL,
"CustRemAmount"  REAL,
"CountChq"  INTEGER,
"AmountChq"  REAL,
PRIMARY KEY ("UniqueId"));