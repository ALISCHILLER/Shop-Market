-- ----------------------------
--  Alter table OrderPrize related to issue: DMC-42051
-- ----------------------------
@UNCHECKED
ALTER TABLE OrderPrize ADD COLUMN CallOrderId TEXT;

-- ----------------------------
-- Table structure for GoodsNosale
-- ----------------------------
DROP TABLE IF EXISTS "main"."GoodsNosale";
CREATE TABLE "GoodsNosale" (
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
-- Table structure for GoodsFixUnit
-- ----------------------------
DROP TABLE IF EXISTS "main"."GoodsFixUnit";
CREATE TABLE "GoodsFixUnit" (
"uniqueId"  TEXT NOT NULL,
"GoodsRef"  INTEGER,
"UnitRef"  INTEGER,
"UnitName"  TEXT,
"Qty"  INTEGER,
PRIMARY KEY ("uniqueId")
);

-- ----------------------------
-- Table structure for DiscountGood
-- ----------------------------
DROP TABLE IF EXISTS "main"."DiscountGood";
CREATE TABLE "DiscountGood" (
"uniqueId"  TEXT NOT NULL,
"BackOfficeId"  INTEGER,
"DiscountRef"  TEXT,
"GoodsRef"  TEXT,
PRIMARY KEY ("uniqueId")
);

-- ----------------------------
-- Table structure for PaymentUsances
-- ----------------------------
DROP TABLE IF EXISTS "main"."PaymentUsances";
CREATE TABLE "PaymentUsances" (
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
-- Table structure for RetSaleHdr
-- ----------------------------
DROP TABLE IF EXISTS "main"."RetSaleHdr";
CREATE TABLE "RetSaleHdr" (
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
-- Table structure for RetSaleItem
-- ----------------------------
DROP TABLE IF EXISTS "main"."RetSaleItem";
CREATE TABLE "RetSaleItem" (
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
-- Table structure for CustExtraField
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustExtraField";
CREATE TABLE "CustExtraField" (
"UniqueId"  TEXT NOT NULL,
"BackOfficeId"  INTEGER,
"CustRef"  INTEGER,
"ExtraField1"  TEXT,
"ExtraField2"  TEXT,
"ExtraField3"  TEXT,
"ExtraField4"  TEXT,
"ExtraField5"  TEXT,
"ExtraField6"  TEXT,
"ExtraField7"  TEXT,
"ExtraField8"  TEXT,
"ExtraField9"  TEXT,
"ExtraField10"  TEXT,
"ExtraField11"  TEXT,
"ExtraField12"  TEXT,
"ExtraField13"  TEXT,
"ExtraField14"  TEXT,
"ExtraField15"  TEXT,
"ExtraField16"  TEXT,
"ExtraField17"  TEXT,
"ExtraField18"  TEXT,
"ExtraField19"  TEXT,
"ExtraField20"  TEXT,
PRIMARY KEY ("UniqueId")
);

-- ----------------------------
-- Add FreeReasonId, IsDeleted to CustomerOldInvoiceDetailTemp
-- ----------------------------
alter table CustomerOldInvoiceDetailTemp add column FreeReasonId INTEGER;
alter table CustomerOldInvoiceDetailTemp add column IsDeleted INTEGER;

-- ----------------------------
-- Add StockDcRef to OnHandQty
-- ----------------------------
alter table OnHandQty add column StockDcRef INTEGER;

-- ----------------------------
-- Add PackUnitRef, UnitRef to Product
-- ----------------------------
alter table Product add column PackUnitRef INTEGER;
alter table Product add column UnitRef INTEGER;

-- ----------------------------
-- Add some of fields to DiscountSDS
-- ----------------------------
alter table DiscountSDS add column CustRefList TEXT;
alter table DiscountSDS add column DiscountAreaRefList TEXT;
alter table DiscountSDS add column DiscountCustActRefList TEXT;
alter table DiscountSDS add column DiscountCustCtgrRefList TEXT;
alter table DiscountSDS add column DiscountCustGroupRefList TEXT;
alter table DiscountSDS add column DiscountCustLevelRefList TEXT;
alter table DiscountSDS add column DiscountDcRefList TEXT;
alter table DiscountSDS add column DiscountGoodRefList TEXT;
alter table DiscountSDS add column DiscountMainCustTypeRefList TEXT;
alter table DiscountSDS add column DiscountOrderNoList TEXT;
alter table DiscountSDS add column DiscountOrderRefList TEXT;
alter table DiscountSDS add column DiscountOrderTypeList TEXT;
alter table DiscountSDS add column DiscountPaymentUsanceRefList TEXT;
alter table DiscountSDS add column DiscountPayTypeList TEXT;
alter table DiscountSDS add column DiscountSaleOfficeRefList TEXT;
alter table DiscountSDS add column DiscountSaleZoneRefList TEXT;
alter table DiscountSDS add column DiscountStateRefList TEXT;
alter table DiscountSDS add column DiscountSubCustTypeRefList TEXT;
alter table DiscountSDS add column IsComplexCondition INTEGER;
alter table DiscountSDS add column PrizeSelectionList TEXT;
alter table DiscountSDS add column IsSelfPrize INTEGER;

-- ----------------------------
-- Add DiscountRef to GoodsPackageItem
-- ----------------------------
alter table GoodsPackageItem add column DiscountRef INTEGER;

-- ----------------------------
-- Add UniqueId to DisAcc
-- ----------------------------
alter table DisAcc add column UniqueId TEXT;
alter table DisAcc add column Code TEXT;
alter table DisAcc add column BackOfficeId INTEGER;
alter table DisAcc add column Name TEXT;

-- ----------------------------
-- Table structure for CustomerBoGroup
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerBoGroup";
CREATE TABLE "CustomerBoGroup" (
"UniqueId"  TEXT NOT NULL,
"Id"  INTEGER NOT NULL,
"ParentRef"  INTEGER,
"NLeft"  INTEGER,
"NRight"  INTEGER,
"NLevel"  INTEGER,
"backOfficeId" INTEGER,
PRIMARY KEY ("Id")
);

-- ----------------------------
-- Add PayDuration to Product
-- ----------------------------
alter table Product add column PayDuration INTEGER;

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
			Product.PayDuration AS PayDuration,
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
-- Alter View structure for OperationReportView .
-- Related to issue DMC-43039
-- We add the records for the customers who just have payment without any order and who don't have payments for their orders // CustomerCall.CallType == 0 OR CustomerCall.CallType=11
-- ----------------------------
DROP VIEW IF EXISTS "OperationReportView";
CREATE VIEW "OperationReportView" AS
SELECT
	Customer.UniqueId AS UniqueId,
	Customer.CustomerName AS CustomerName,
	Customer.CustomerCode AS CustomerCode,
	Customer.StoreName AS StoreName,
	ifnull(SellAmount.TotalAmount,0) as TotalAmount,
	ifnull(SellAmount.OrderDiscountAmount,0) as OrderDiscountAmount,
	ifnull(SellAmount.OrderAddAmount,0) as OrderAddAmount,
	ifnull(SellAmount.TotalNetAmount,0) as TotalNetAmount,
	ifnull(CustomerPayment.Amount,0) as TotalPaidAmount,
	ifnull(CustomerPayment.AmountCheque,0) as AmountCheque,
	ifnull(CustomerPayment.AmountCard,0) as AmountCard,
	ifnull(CustomerPayment.AmountCash,0) as AmountCash,
	ifnull(CustomerPayment.AmountDiscount,0) as AmountDiscount,
	ifnull(CustomerPayment.AmountCredit,0) as AmountCredit,
	ifnull(SellAmount.TotalNetAmount,0) - ifnull(CallReturn.RequestAmount,0) as PayableAmount,
	CASE WHEN (ifnull(SellAmount.TotalNetAmount,0) - ifnull(CallReturn.RequestAmount,0) - ifnull(CustomerPayment.AmountCheque,0) - ifnull(CustomerPayment.AmountCard,0) - ifnull(CustomerPayment.AmountCash,0) - ifnull(CustomerPayment.AmountDiscount,0) - ifnull(CustomerPayment.AmountCredit,0)) > 0 then (ifnull(SellAmount.TotalNetAmount,0) - ifnull(CallReturn.RequestAmount,0) - ifnull(CustomerPayment.AmountCheque,0) - ifnull(CustomerPayment.AmountCard,0) - ifnull(CustomerPayment.AmountCash,0) - ifnull(CustomerPayment.AmountDiscount,0) - ifnull(CustomerPayment.AmountCredit,0)) else 0 end as Recipe,
	ifnull(CallReturn.AddAmount,0) as ReturnAddAmount,
	ifnull(CallReturn.DisAmount,0) as ReturnDiscountAmount,
	ifnull(CallReturn.RequestAmount,0) as ReturnRequestAmount
FROM
	Customer
LEFT JOIN (
	SELECT
		CustomerCallOrderOrderView.CustomerUniqueId,
		SUM(CASE WHEN(CustomerCallOrderOrderView.IsPromoLine != 1) THEN	ifnull(CustomerCallOrderOrderView.RequestAmount,0) ELSE	0	END) AS TotalAmount,
		SUM(ifnull(CustomerCallOrderOrderView.RequestDis1Amount,0) + ifnull(CustomerCallOrderOrderView.RequestDis2Amount,0) + ifnull(CustomerCallOrderOrderView.RequestDis3Amount,0)) AS OrderDiscountAmount,
		SUM(ifnull(CustomerCallOrderOrderView.RequestTaxAmount,0) + ifnull(CustomerCallOrderOrderView.RequestChargeAmount,0) + ifnull(CustomerCallOrderOrderView.RequestAdd1Amount,0) + ifnull(CustomerCallOrderOrderView.RequestAdd2Amount,0)) AS OrderAddAmount,
		SUM((CASE	WHEN (CustomerCallOrderOrderView.IsPromoLine != 1) THEN	ifnull(CustomerCallOrderOrderView.RequestAmount,0) ELSE	0	END) - ifnull(CustomerCallOrderOrderView.RequestDis1Amount,0) - ifnull(CustomerCallOrderOrderView.RequestDis2Amount,0) - ifnull(CustomerCallOrderOrderView.RequestDis3Amount,0) + ifnull(CustomerCallOrderOrderView.RequestAdd1Amount,0) + ifnull(CustomerCallOrderOrderView.RequestAdd2Amount,0) + ifnull(CustomerCallOrderOrderView.RequestTaxAmount,0) + ifnull(CustomerCallOrderOrderView.RequestChargeAmount,0)) AS TotalNetAmount
	FROM
		CustomerCallOrderOrderView
	GROUP BY
		CustomerCallOrderOrderView.CustomerUniqueId
) AS SellAmount ON SellAmount.CustomerUniqueId = Customer.UniqueId
LEFT JOIN (
	SELECT
		CustomerId,
		SUM(Amount) as Amount,
		SUM(case when PaymentType = 'e3a93634-ae20-4d57-8e27-eee7b768adfc' then Amount else 0 end) as AmountCheque,
		SUM(case when PaymentType = 'f1b06da6-122d-4427-abd0-84a7cf72b29c' then Amount else 0 end) as AmountCard,
		SUM(case when PaymentType = '837689e8-2115-4085-bf7f-0d0da86f3d71' then Amount else 0 end) as AmountCash,
		SUM(case when PaymentType = 'df7e99c9-2ed9-436a-b9a3-8ec0f4e86651' then Amount else 0 end) as AmountDiscount,
		SUM(case when PaymentType = '56c7d3ee-4d18-4c5c-bbbd-aacc6bebd862' then Amount else 0 end) as AmountCredit
	FROM
		Payment
	GROUP BY Payment.CustomerId
) CustomerPayment ON CustomerPayment.CustomerId = Customer.UniqueId
LEFT JOIN (
SELECT
		CustomerCallReturnLinesView.CustomerUniqueId,
		sum(ifnull(CustomerCallReturnLinesView.TotalRequestAmount,0)) as RequestAmount,
		sum(ifnull(CustomerCallReturnLinesView.TotalRequestAdd1Amount,0) + ifnull(CustomerCallReturnLinesView.TotalRequestAdd2Amount,0) + ifnull(CustomerCallReturnLinesView.TotalRequestTax,0) + ifnull(CustomerCallReturnLinesView.TotalRequestCharge,0)) as AddAmount,
		sum(ifnull(CustomerCallReturnLinesView.TotalRequestDiscount,0))as DisAmount
	FROM
		CustomerCallReturnLinesView
	GROUP BY CustomerCallReturnLinesView.CustomerUniqueId
) CallReturn ON CallReturn.CustomerUniqueId = Customer.UniqueId
JOIN CustomerCall On (CustomerCall.CallType == 0 OR CustomerCall.CallType=11) AND CustomerCall.ConfirmStatus == 1 AND CustomerCall.CustomerId = Customer.UniqueId
GROUP BY CustomerCall.CustomerId;

-- ----------------------------
-- View structure for ProductBatchView
-- ----------------------------
DROP VIEW IF EXISTS "main"."ProductBatchView";
CREATE VIEW "ProductBatchView" AS
SELECT
ProductBatchOnHandQty.ProductId AS ProductId,
group_concat(BatchNo, ':') AS BatchNo,
group_concat(ExpDate, ':') AS ExpDate,
group_concat(OnHandQty, ':') AS OnHandQty,
group_concat(BatchRef, ':') AS BatchRef,
group_concat(ItemRef, ':') AS ItemRef
from ProductBatchOnHandQty
JOIN Product ON Product.UniqueId = ProductBatchOnHandQty.ProductId
GROUP BY ProductBatchOnHandQty.ProductId;

-- ----------------------------
-- Alter View structure for CustomerCallReturnLinesView .
-- Related to issue DMC-42178
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
	CASE
       WHEN CustomerCallReturnLines.RequestBulkUnitId IS NULL THEN
          sum(CustomerCallReturnLinesQtyDetail.Qty * ProductUnit.ConvertFactor)
       ELSE
          CustomerCallReturnLines.RequestBulkQty
      END AS TotalReturnQty,
	CASE
		WHEN CustomerCallReturnLines.RequestBulkUnitId IS NULL THEN
		   CustomerCallReturnLines.RequestUnitPrice * sum(CustomerCallReturnLinesQtyDetail.Qty * ProductUnit.ConvertFactor)
		ELSE
		   CustomerCallReturnLines.RequestBulkQty * CustomerCallReturnLines.RequestUnitPrice
	END AS TotalRequestAmount,
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
--  Add view UnConfirmedCustomerPathView related to issue: NGT-2935
--  This was added for Contractor
-- ----------------------------
DROP VIEW IF EXISTS "main"."UnConfirmedCustomerPathView";
CREATE VIEW "UnConfirmedCustomerPathView" AS
SELECT
	c.*, VisitTemplatePathCustomer.PathRowId, VisitTemplatePathCustomer.VisitTemplatePathId AS VisitTemplatePathId,
group_concat(CustomerCall.CallType) as CallType, CustomerCall.ConfirmStatus
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
LEFT JOIN CustomerCall  ON CustomerCall.CustomerId = c.UniqueId
GROUP BY c.UniqueId , CustomerCall.CustomerId, VisitTemplatePathId;

-- ----------------------------
-- Add Table structure for TrackingLogModel
-- ----------------------------
DROP TABLE IF EXISTS "main"."TrackingLog";
CREATE TABLE "TrackingLog" (
"uniqueId"  TEXT NOT NULL,
"ENTime"  TEXT,
"FATime"  TEXT,
"EventType"  TEXT,
"Description"  TEXT,
"Trace"  TEXT,
"Time"  INTEGER,
"Level" TEXT,
PRIMARY KEY ("uniqueId")
);

-- ----------------------------
--  Alter table Location
-- ----------------------------
ALTER TABLE Location ADD COLUMN Provider TEXT;
ALTER TABLE Location ADD COLUMN LicensePolicy INTEGER;
ALTER TABLE Location ADD COLUMN DateAndTime TEXT;

-- ----------------------------
-- Alter Table structure for OnHandQty
-- ----------------------------
@UNCHECKED
alter table OrderPrize add column CallOrderId TEXT;

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
Product.BackOfficeId AS GoodsRef,
OrderPrize.CallOrderId AS CallOrderId
FROM
OrderPrize
INNER JOIN Product ON OrderPrize.ProductId = Product.UniqueId;

-- ----------------------------
--  Alter table Image related to issue: NGT-3023
-- ----------------------------
ALTER TABLE Image ADD COLUMN LastUpdate INTEGER;
