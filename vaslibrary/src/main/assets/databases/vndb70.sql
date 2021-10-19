-- ----------------------------
-- Table structure for DistributionCustomerPrice
-- ----------------------------
DROP TABLE IF EXISTS "main"."DistributionCustomerPrice";
CREATE TABLE "DistributionCustomerPrice" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"CustomerUniqueId"  TEXT COLLATE NOCASE ,
"ProductUniqueId"  TEXT COLLATE NOCASE ,
"UserPrice"  REAL,
"PriceId"  INTEGER,
"Price"  REAL,
"CallOrderId"  TEXT,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for CustomerCallInvoice
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerCallInvoice";
CREATE TABLE "CustomerCallInvoice" (
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
"StartTime" Integer,
"EndTime" Integer,
"IsInvoice" Integer,
"DealerName" TEXT,
PRIMARY KEY ("UniqueId" ASC),
CONSTRAINT "fkey0" FOREIGN KEY ("CustomerUniqueId") REFERENCES "Customer" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT "fkey1" FOREIGN KEY ("PromotionUniqueId") REFERENCES "CustomerCallInvoice" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for CustomerCallInvoiceLines
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerCallInvoiceLines";
CREATE TABLE "CustomerCallInvoiceLines"
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
    PromotionPrice REAL,
    UnitPrice REAl,
    UserPrice REAL,
    RequestAmount REAL,
    CPriceUniqueId TEXT COLLATE NOCASE,
    PriceUniqueId TEXT COLLATE NOCASE,
    PRIMARY KEY (UniqueId ASC),
    CONSTRAINT fkey0 FOREIGN KEY (OrderUniqueId) REFERENCES CustomerCallInvoice(UniqueId) ON DELETE CASCADE ON UPDATE CASCADE,
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
CONSTRAINT "fkey1" FOREIGN KEY ("OrderLineUniqueId") REFERENCES "CustomerCallInvoiceLines" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT "fkey2" FOREIGN KEY ("ProductUnitId") REFERENCES "ProductUnit" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- Create view structure for CustomerCallInvoicePreview.
-- ----------------------------
DROP VIEW IF EXISTS "main"."CustomerCallInvoicePreview";
CREATE VIEW "CustomerCallInvoicePreview" AS
SELECT
	CustomerCallInvoice.UniqueId,
	CustomerCallInvoice.CustomerUniqueId,
	ifnull(SUM(a.TotalPrice),0) as TotalPrice,
	ifnull(SUM(a.TotalQty),0) as TotalQty,
	CustomerCallInvoice.BackOfficeOrderNo,
	CustomerCallInvoice.SaleNoSDS,
	CustomerCallInvoice.IsInvoice,
	CustomerCallInvoice.Comment,
	CustomerCall.CallType,
	CustomerCall.ConfirmStatus
FROM
CustomerCallInvoice LEFT JOIN
	(
	SELECT
		CustomerCallInvoiceLines.OrderUniqueId AS OrderUniqueId,
		CustomerCallInvoice.CustomerUniqueId AS CustomerUniqueId,
		CustomerCallInvoiceLines.RequestBulkQty AS RequestBulkQty,
		CustomerCallInvoiceLines.RequestBulkQtyUnitUniqueId AS RequestBulkQtyUnitUniqueId,
		CustomerPrice.Price AS UnitPrice,
		CASE
		WHEN CustomerCallInvoiceLines.RequestBulkQtyUnitUniqueId IS NULL THEN
			sum(Qty * ConvertFactor)
		ELSE
			CustomerCallInvoiceLines.RequestBulkQty
		END AS TotalQty,
		CASE
		WHEN IsRequestFreeItem = 1 THEN
		(
			0
		)
		WHEN IsPromoLine = 1 THEN
		(
			PromotionPrice
		)
		WHEN RequestBulkQtyUnitUniqueId IS NULL THEN
		(
			sum(Qty * ConvertFactor) * CustomerPrice.Price
		)
		ELSE
		(
			RequestBulkQty * CustomerPrice.Price
		)
		END AS TotalPrice,
		CustomerCallInvoice.LocalPaperNo AS LocalPaperNo,
		CustomerCallInvoice.Comment AS Comment
	FROM
		CustomerCallInvoiceLines,
		ProductUnit ON CustomerCallOrderLinesInvoiceQtyDetail.ProductUnitId = ProductUnit.UniqueId,
		CustomerCallOrderLinesInvoiceQtyDetail ON CustomerCallOrderLinesInvoiceQtyDetail.OrderLineUniqueId = CustomerCallInvoiceLines.UniqueId,
		Product ON Product.UniqueId = CustomerCallInvoiceLines.ProductUniqueId,
		CustomerCallInvoice ON CustomerCallInvoice.UniqueId = CustomerCallInvoiceLines.OrderUniqueId
	LEFT JOIN CustomerPrice ON (CustomerPrice.CallOrderId = CustomerCallInvoice.UniqueId)
	AND (CustomerPrice.ProductUniqueId = CustomerCallInvoiceLines.ProductUniqueId)
	GROUP BY OrderLineUniqueId
	) as a
ON CustomerCallInvoice.UniqueId = a.OrderUniqueId
LEFT JOIN CustomerCall ON CustomerCall.ExtraField1 = a.OrderUniqueId
GROUP BY
	OrderUniqueId
ORDER BY CustomerCallInvoice.CustomerUniqueId;

-- ----------------------------
-- Create view structure for CustomerCallInvoiceLinesView.
-- ----------------------------
DROP VIEW IF EXISTS "main"."CustomerCallInvoiceLinesView";
CREATE VIEW "CustomerCallInvoiceLinesView" AS
SELECT
	CustomerCallInvoiceLines.UniqueId AS UniqueId,
	CustomerCallInvoiceLines.OrderUniqueId AS OrderUniqueId,
	CustomerCallInvoice.CustomerUniqueId AS CustomerUniqueId,
	CustomerCallInvoiceLines.RequestBulkQty AS RequestBulkQty,
	CustomerCallInvoiceLines.RequestBulkQtyUnitUniqueId AS RequestBulkQtyUnitUniqueId,
	CustomerPrice.Price AS UnitPrice,
	CASE
WHEN CustomerCallInvoiceLines.RequestBulkQtyUnitUniqueId IS NULL THEN
	sum(Qty * ConvertFactor)
ELSE
	CustomerCallInvoiceLines.RequestBulkQty
END AS TotalQty,
 CASE
WHEN IsRequestFreeItem = 1 THEN
	(0)
WHEN IsPromoLine = 1 THEN
	(PromotionPrice)
WHEN RequestBulkQtyUnitUniqueId IS NULL THEN
	(
		sum(Qty * ConvertFactor) * CustomerPrice.Price
	)
ELSE
	(
		RequestBulkQty * CustomerPrice.Price
	)
END AS TotalPrice,
 CustomerCallInvoice.LocalPaperNo AS LocalPaperNo,
 CustomerCallInvoice.Comment AS Comment
FROM
	CustomerCallInvoiceLines,
	ProductUnit ON CustomerCallOrderLinesInvoiceQtyDetail.ProductUnitId = ProductUnit.UniqueId,
	CustomerCallOrderLinesInvoiceQtyDetail ON CustomerCallOrderLinesInvoiceQtyDetail.OrderLineUniqueId = CustomerCallInvoiceLines.UniqueId,
	Product ON Product.UniqueId = CustomerCallInvoiceLines.ProductUniqueId,
	CustomerCallInvoice ON CustomerCallInvoice.UniqueId = CustomerCallInvoiceLines.OrderUniqueId
LEFT JOIN CustomerPrice ON (
	CustomerPrice.CallOrderId = CustomerCallInvoice.UniqueId
)
AND (
	CustomerPrice.ProductUniqueId = CustomerCallInvoiceLines.ProductUniqueId
)
GROUP BY
	OrderLineUniqueId;


-- ----------------------------
-- Create view structure for CustomerCallOrderOrderView.
-- ----------------------------
DROP VIEW IF EXISTS "main"."CustomerCallOrderOrderView";
CREATE VIEW "CustomerCallOrderOrderView" AS
SELECT
CustomerCallInvoiceLinesView.TotalQty as OriginalTotalQty,
 a.*,
 CASE
  WHEN a.RequestBulkQtyUnitUniqueId IS NULL THEN
   (a.TotalQty * a.UnitPrice)
  ELSE
  (a.RequestBulkQty * a.UnitPrice)
 END AS RequestAmount,
 FreeReason.FreeReasonName AS FreeReasonName,
 OnHandQty.OnHandQty AS OnHandQty,
 TotalProductOrderQtyView.TotalQty AS ProductTotalOrderedQty,
 ProductBatchView.BatchNo AS BatchNo,
 ProductBatchView.ExpDate AS ExpDate,
 ProductBatchView.OnHandQty AS BatchOnHandQty,
 ProductBatchView.ItemRef AS ItemRef,
 ProductBatchView.BatchRef AS BatchRef,
 OnHandQty.HasAllocation AS HasAllocation
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
			CustomerCallOrderLines.PromotionPrice AS PromotionPrice,
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
			CustomerCallOrder.SaleDate AS SaleDate,
			CustomerCallOrder.LocalPaperNo AS LocalPaperNo,
			CustomerCallOrder.OrderPaymentTypeUniqueId AS OrderPaymentTypeUniqueId,
			CustomerCallOrder.OrderTypeUniqueId AS OrderTypeUniqueId
		FROM
			CustomerCallOrderLines,
			ProductUnit ON CustomerCallOrderLinesOrderQtyDetail.ProductUnitId = ProductUnit.UniqueId,
			CustomerCallOrderLinesOrderQtyDetail ON CustomerCallOrderLinesOrderQtyDetail.OrderLineUniqueId = CustomerCallOrderLines.UniqueId,
			Product ON Product.UniqueId = CustomerCallOrderLines.ProductUniqueId,
			Unit ON Unit.UniqueId = ProductUnit.UnitId,
			CustomerCallOrder ON CustomerCallOrder.UniqueId = CustomerCallOrderLines.OrderUniqueId
		LEFT JOIN CustomerEmphaticProduct ON CustomerEmphaticProduct.ProductId = Product.UniqueId
		LEFT JOIN CustomerPrice ON (
			CustomerPrice.CallOrderId = CustomerCallOrder.UniqueId
		)
		AND (
			CustomerPrice.ProductUniqueId = CustomerCallOrderLines.ProductUniqueId
		)
		GROUP BY
			OrderLineUniqueId
	) AS a
LEFT JOIN FreeReason ON FreeReason.UniqueId = a.FreeReasonId
LEFT JOIN OnHandQty ON OnHandQty.ProductId = a.ProductId
LEFT JOIN TotalProductOrderQtyView ON TotalProductOrderQtyView.ProductId = a.ProductId
LEFT JOIN ProductBatchView ON a.ProductId = ProductBatchView.ProductId
LEFT JOIN CustomerCallInvoiceLinesView ON CustomerCallInvoiceLinesView.UniqueId = a.UniqueId;

-- ----------------------------
-- Add new fields to CustomerCallOrder.
-- ----------------------------
alter table CustomerCallOrder add column IsInvoice INTEGER;
alter table CustomerCallOrder add column DealerName TEXT;

-- ----------------------------
-- Create view structure for CustomerSettledInvoicesView.
-- ----------------------------
DROP VIEW IF EXISTS "main"."CustomerSettledInvoicesView";
CREATE VIEW "CustomerSettledInvoicesView" AS
SELECT
	CustomerCallInvoice.UniqueId
FROM
	CustomerCallInvoice
JOIN CustomerCall ON CustomerCallInvoice.UniqueId = CustomerCall.ExtraField1
and CustomerCall.ConfirmStatus == 1 and ( CustomerCall.CallType == 15 OR CustomerCall.CallType == 16 OR CustomerCall.CallType == 17 OR CustomerCall.CallType == 18)
UNION
SELECT
	CustomerCallInvoice.UniqueId
FROM
	CustomerCallInvoice
JOIN CustomerCall ON CustomerCallInvoice.CustomerUniqueId = CustomerCall.CustomerId
and ( CustomerCall.CallType == 13 OR CustomerCall.CallType == 14);

-- ----------------------------
-- Add IsReturnDefault to ProductUnit. DMC-38055
-- ----------------------------
alter table ProductUnit add column IsReturnDefault INTEGER;
-- ----------------------------
-- Alter view structure for ProductUnitView DMC-38055
-- ----------------------------
DROP VIEW IF EXISTS "main"."ProductUnitView";
CREATE VIEW "ProductUnitView" AS
SELECT
	ProductUnit.UniqueId AS UniqueId,
	Unit.BackOfficeId AS UnitRef,
	ProductUnit.IsForReturn AS IsForReturn,
	ProductUnit.IsForSale AS IsForSale,
	ProductUnit.IsDefault AS IsDefault,
    ProductUnit.IsReturnDefault AS IsReturnDefault,
	ProductName,
	ProductId,
	Product.BackOfficeId,
	ProductCode,
	Unit.UniqueId AS UnitId,
	ConvertFactor,
	UnitName,
	CASE
WHEN (
	Product.ProductTypeId = 'EDFDADE5-45B5-41AF-BCB0-BEA2AE733789'
)
AND (
	ProductUnit.UnitStatusId = '1f68ee36-13d7-4a3c-a082-fe6014ac77cc'
) THEN
	1
ELSE
	0
END AS Decimal
FROM
	Product,
	ProductUnit ON Product.UniqueId = ProductUnit.ProductId,
	Unit ON Unit.UniqueId = ProductUnit.UnitId
ORDER BY
	ConvertFactor DESC;


-- ----------------------------
-- Table structure for CustomerCallReturnRequest
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerCallReturnRequest";
CREATE TABLE "CustomerCallReturnRequest" (
"UniqueId"  TEXT(100) NOT NULL COLLATE NOCASE ,
"CustomerUniqueId"  TEXT(100) COLLATE NOCASE,
"ReturnTypeUniqueId"  TEXT COLLATE NOCASE,
"PersonnelUniqueId"  TEXT COLLATE NOCASE,
"LocalPaperNo"  TEXT(20),
"BackOfficeDistId"  TEXT(100),
"BackOfficeInvoiceId"  TEXT COLLATE NOCASE,
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
"SaleOfficeRefSDS"  INTEGER, StartTime Integer, EndTime Integer, DealerUniqueId Text,
"IsFromRequest"  INTEGER DEFAULT 1,
PRIMARY KEY ("UniqueId" ASC),
CONSTRAINT "fkey1" FOREIGN KEY ("CustomerUniqueId") REFERENCES "Customer" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for CustomerCallReturnLinesRequest
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerCallReturnLinesRequest";
CREATE TABLE "CustomerCallReturnLinesRequest" (
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
"ReturnProductTypeId"  TEXT COLLATE NOCASE,
"ReferenceId"  TEXT,
"ReferenceNo"  INTEGER,
"ReturnReasonId"  TEXT NOT NULL COLLATE NOCASE,
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
IsPromoLine INTEGER,
StockId TEXT,
PRIMARY KEY ("UniqueId" ASC),
CONSTRAINT "fkey0" FOREIGN KEY ("RequestBulkUnitId") REFERENCES "ProductUnit" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT "fkey3" FOREIGN KEY ("ProductUniqueId") REFERENCES "Product" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT "fkey4" FOREIGN KEY ("ReturnUniqueId") REFERENCES "CustomerCallReturnRequest" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for CustomerCallReturnLinesQtyDetailRequest
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerCallReturnLinesQtyDetailRequest";
CREATE TABLE "CustomerCallReturnLinesQtyDetailRequest" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"ReturnLineUniqueId"  TEXT COLLATE NOCASE ,
"ProductUnitId"  TEXT COLLATE NOCASE ,
"Qty"  REAL,
PRIMARY KEY ("UniqueId" ASC),
CONSTRAINT "fkey1" FOREIGN KEY ("ProductUnitId") REFERENCES "ProductUnit" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT "fkey2" FOREIGN KEY ("ReturnLineUniqueId") REFERENCES "CustomerCallReturnLinesRequest" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- View structure for CustomerCallReturnLinesRequestView
-- ----------------------------
DROP VIEW IF EXISTS "main"."CustomerCallReturnLinesRequestView";
CREATE VIEW "CustomerCallReturnLinesRequestView" AS
SELECT
	CustomerCallReturnLinesRequest.*, CustomerCallReturnRequest.CustomerUniqueId AS CustomerUniqueId,
	Product.ProductName AS ProductName,
	Product.ProductCode AS ProductCode,
 	Product.UniqueId AS ProductId,
	group_concat(Qty, ':') AS Qty,
 	group_concat(ConvertFactor, ':') AS ConvertFactor,
 	group_concat(
 		CustomerCallReturnLinesQtyDetailRequest.ProductUnitId,
 		':'
 	) AS ProductUnitId,
 	group_concat(Unit.UnitName, ':') AS UnitName,
 	sum(Qty * ConvertFactor) AS TotalReturnQty,
 	CustomerCallReturnLinesRequest.RequestUnitPrice * sum(Qty * ConvertFactor) AS TotalRequestAmount,
	CustomerCallReturnRequest.BackOfficeInvoiceId AS InvoiceId,
	CustomerOldInvoiceHeader.SaleNo AS SaleNo
FROM
	CustomerCallReturnLinesRequest
JOIN ProductUnit ON CustomerCallReturnLinesQtyDetailRequest.ProductUnitId = ProductUnit.UniqueId
JOIN CustomerCallReturnLinesQtyDetailRequest ON CustomerCallReturnLinesQtyDetailRequest.ReturnLineUniqueId = CustomerCallReturnLinesRequest.UniqueId
JOIN Product ON Product.UniqueId = CustomerCallReturnLinesRequest.ProductUniqueId
JOIN Unit ON Unit.UniqueId = ProductUnit.UnitId
JOIN CustomerCallReturnRequest ON CustomerCallReturnRequest.UniqueId = CustomerCallReturnLinesRequest.ReturnUniqueId
JOIN ReturnReason ON ReturnReason.UniqueId = CustomerCallReturnLinesRequest.ReturnReasonId
LEFT JOIN CustomerOldInvoiceDetail ON CustomerOldInvoiceDetail.ProductId = CustomerCallReturnLinesRequest.ProductUniqueId
and CustomerOldInvoiceDetail.SaleId = CustomerCallReturnRequest.BackOfficeInvoiceId
LEFT JOIN CustomerOldInvoiceHeader ON CustomerOldInvoiceHeader.UniqueId = CustomerOldInvoiceDetail.SaleId
AND CustomerOldInvoiceHeader.UniqueId = CustomerCallReturnRequest.BackOfficeInvoiceId
GROUP BY
	ReturnLineUniqueId;

-- ----------------------------
-- View structure for CustomerCallReturnDistView
-- ----------------------------
DROP VIEW IF EXISTS "main"."CustomerCallReturnDistView";
CREATE VIEW "CustomerCallReturnDistView" AS
SELECT
	CustomerCallReturnLinesRequest.*, CustomerCallReturnRequest.CustomerUniqueId AS CustomerUniqueId,
	Product.ProductName AS ProductName,
	Product.ProductCode AS ProductCode,
 	ProductUnit.ProductId AS ProductId,
	group_concat(Qty, ':') AS Qty,
 	group_concat(ConvertFactor, ':') AS ConvertFactor,
 	group_concat(
 		CustomerCallReturnLinesQtyDetailRequest.ProductUnitId,
 		':'
 	) AS ProductUnitId,
 	group_concat(Unit.UnitName, ':') AS UnitName,
 	sum(Qty * ConvertFactor) AS TotalReturnQty,
 	CustomerCallReturnLinesRequest.RequestUnitPrice * sum(Qty * ConvertFactor) AS TotalRequestAmount,
	CustomerCallReturnRequest.BackOfficeInvoiceId AS InvoiceId,
	CustomerOldInvoiceHeader.SaleNo AS SaleNo
FROM
	CustomerCallReturnLinesRequest
JOIN ProductUnit ON CustomerCallReturnLinesQtyDetailRequest.ProductUnitId = ProductUnit.UniqueId
JOIN CustomerCallReturnLinesQtyDetailRequest ON CustomerCallReturnLinesQtyDetailRequest.ReturnLineUniqueId = CustomerCallReturnLinesRequest.UniqueId
JOIN Product ON Product.UniqueId = CustomerCallReturnLinesRequest.ProductUniqueId
JOIN Unit ON Unit.UniqueId = ProductUnit.UnitId
JOIN CustomerCallReturnRequest ON CustomerCallReturnRequest.UniqueId = CustomerCallReturnLinesRequest.ReturnUniqueId
JOIN ReturnReason ON ReturnReason.UniqueId = CustomerCallReturnLinesRequest.ReturnReasonId
LEFT JOIN CustomerOldInvoiceDetail ON CustomerOldInvoiceDetail.ProductId = CustomerCallReturnLinesRequest.ProductUniqueId
and CustomerOldInvoiceDetail.SaleId = CustomerCallReturnRequest.BackOfficeInvoiceId
LEFT JOIN CustomerOldInvoiceHeader ON CustomerOldInvoiceHeader.UniqueId = CustomerOldInvoiceDetail.SaleId
AND CustomerOldInvoiceHeader.UniqueId = CustomerCallReturnRequest.BackOfficeInvoiceId
GROUP BY
	ReturnLineUniqueId;


-- ----------------------------
-- Alter table structure for CustomerCallReturn
-- ----------------------------
alter table CustomerCallReturn add column IsFromRequest INTEGER DEFAULT 0;

-- ----------------------------
-- alter view structure for CustomerCallReturnLinesView
-- ----------------------------
DROP VIEW IF EXISTS "main"."CustomerCallReturnLinesView";
CREATE VIEW "CustomerCallReturnLinesView" AS
SELECT
	CustomerCallReturnLines.*, CustomerCallReturn.CustomerUniqueId AS CustomerUniqueId,
	Product.ProductName AS ProductName,
	Product.ProductCode AS ProductCode,
	Product.UniqueId AS ProductId,
	group_concat(CustomerCallReturnLinesQtyDetail.Qty, ':') AS Qty,
	group_concat(ProductUnit.ConvertFactor, ':') AS ConvertFactor,
	group_concat(
		CustomerCallReturnLinesQtyDetail.ProductUnitId,
		':'
	) AS ProductUnitId,
	group_concat(Unit.UnitName, ':') AS UnitName,
	sum(CustomerCallReturnLinesQtyDetail.Qty * ProductUnit.ConvertFactor) AS TotalReturnQty,
	CustomerCallReturnLines.RequestUnitPrice * sum(CustomerCallReturnLinesQtyDetail.Qty * ProductUnit.ConvertFactor) AS TotalRequestAmount,
	CustomerCallReturn.IsFromRequest as IsFromRequest,
	CustomerCallReturn.BackOfficeInvoiceId AS InvoiceId,
	CustomerCallReturn.Comment AS Comment,
	CustomerCallReturn.DealerUniqueId AS DealerUniqueId,
	CustomerOldInvoiceHeader.SaleNo AS SaleNo,
	CustomerCallReturnLinesRequestView.TotalReturnQty AS OriginalTotalReturnQty
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
LEFT JOIN CustomerCallReturnLinesRequestView ON CustomerCallReturnLinesRequestView.UniqueId = CustomerCallReturnLines.UniqueId
GROUP BY
	ReturnLineUniqueId;

-- ----------------------------
-- alter view structure for CustomerCallReturnView
-- ----------------------------
DROP VIEW IF EXISTS "main"."CustomerCallReturnView";
CREATE VIEW "CustomerCallReturnView" AS
SELECT
	CustomerCallReturnLinesView.UniqueId,
	CustomerCallReturnLinesView.IsPromoLine,
	CustomerCallReturnLinesView.StockId,
	CustomerCallReturnLinesView.CustomerUniqueId AS CustomerUniqueId,
	CustomerCallReturnLinesView.ReturnUniqueId AS ReturnUniqueId,
	CustomerCallReturnLinesView.InvoiceId AS InvoiceId,
	CustomerCallReturnLinesView.TotalRequestAmount,
	CustomerCallReturnLinesView.IsFromRequest AS IsFromRequest,
	CustomerCallReturnLinesView.OriginalTotalReturnQty as OriginalTotalReturnQty,
	CustomerCallReturnLinesView.Comment as Comment,
	CustomerCallReturnLinesView.DealerUniqueId as DealerUniqueId,
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
	CASE WHEN CustomerCallReturnLinesView.IsPromoLine THEN
		ifnull(CustomerCallReturnLinesView.TotalRequestNetAmount,0)
	ELSE
		ifnull(CustomerCallReturnLinesView.TotalRequestAmount,0) -
		ifnull(CustomerCallReturnLinesView.TotalRequestDiscount,0) +
		ifnull(CustomerCallReturnLinesView.TotalRequestAdd1Amount,0) +
		ifnull(CustomerCallReturnLinesView.TotalRequestAdd2Amount,0) +
		ifnull(CustomerCallReturnLinesView.TotalRequestTax,0) +
		ifnull(CustomerCallReturnLinesView.TotalRequestCharge,0)
	END  AS TotalRequestNetAmount
FROM CustomerCallReturnLinesView
JOIN ReturnReason ON ReturnReason.UniqueId = CustomerCallReturnLinesView.ReturnReasonId
LEFT JOIN CustomerOldInvoiceDetail ON CustomerOldInvoiceDetail.ProductId = CustomerCallReturnLinesView.ProductId
and CustomerOldInvoiceDetail.SaleId = CustomerCallReturnLinesView.InvoiceId
LEFT JOIN CustomerOldInvoiceHeader ON CustomerOldInvoiceHeader.UniqueId = CustomerOldInvoiceDetail.SaleId
AND CustomerOldInvoiceDetail.SaleId = CustomerCallReturnLinesView.InvoiceId
GROUP BY CustomerCallReturnLinesView.UniqueId;

-- ----------------------------
-- Add FreeReasonId, IsDeleted to DiscountCustomerOldInvoiceDetail
-- ----------------------------
alter table CustomerOldInvoiceDetail add column FreeReasonId INTEGER;
alter table CustomerOldInvoiceDetail add column IsDeleted INTEGER;


-- ----------------------------
-- alter view structure for OldInvoiceHeaderView
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
	CustomerOldInvoiceHeader.DealerId AS DealerId,
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
	CustomerOldInvoiceHeaderTemp.DealerId AS DealerId,
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
--  Alter view OldInvoiceDetailView. Related to issue: DMC-39432
-- ----------------------------
DROP VIEW IF EXISTS "main"."OldInvoiceDetailView";
CREATE VIEW "OldInvoiceDetailView" AS
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
CustomerOldInvoiceHeader.SalePDate,
Product.ProductName,
Product.ProductCode,
Product.ProductGroupId,
CustomerOldInvoiceDetail.UnitPrice * CustomerOldInvoiceDetail.TotalQty AS TotalAmount,
CustomerCallReturnView.TotalReturnQty as TotalReturnQty,
CustomerCallReturnView.TotalRequestAmount as TotalRequestAmount
FROM CustomerOldInvoiceDetail
JOIN Product ON CustomerOldInvoiceDetail.ProductId = Product.UniqueId
LEFT JOIN CustomerCallReturnView ON CustomerCallReturnView.ProductId = Product.UniqueId AND
CustomerCallReturnView.InvoiceId = SaleId
JOIN CustomerOldInvoiceHeader ON CustomerOldInvoiceHeader.UniqueId = CustomerOldInvoiceDetail.SaleId;


-- ----------------------------
--  Alter view CustomerPathView related to issue: DMC-40733
-- ----------------------------
DROP VIEW IF EXISTS "main"."CustomerPathView";
CREATE VIEW "CustomerPathView" AS
SELECT
	c.*, VisitTemplatePathCustomer.PathRowId, VisitTemplatePathCustomer.VisitTemplatePathId AS VisitTemplatePathId,
group_concat(CustomerCall.CallType) as CallType
FROM
	(
		SELECT
			Customer.*, SUM(
				CustomerCallOrderPreview.TotalPrice
			) AS TotalOrderAmount
		FROM
			Customer
		LEFT JOIN CustomerCallOrderPreview ON CustomerCallOrderPreview.CustomerUniqueId = Customer.UniqueId
		GROUP BY
			Customer.UniqueId
	) c
JOIN VisitTemplatePathCustomer ON c.UniqueId = VisitTemplatePathCustomer.CustomerId
LEFT JOIN CustomerCall ON CustomerCall.ConfirmStatus == 1 AND CustomerCall.CustomerId = c.UniqueId
GROUP BY c.UniqueId , CustomerCall.CustomerId, VisitTemplatePathId;

-- ----------------------------
-- Add new fields to PictureTemplateHeader.
-- ----------------------------
alter table PictureTemplateHeader add column FromPDate TEXT;
alter table PictureTemplateHeader add column ToPDate TEXT;