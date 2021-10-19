-- ----------------------------
-- Table structure for Image
-- ----------------------------
DROP TABLE IF EXISTS "main"."Image";
CREATE TABLE "Image" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"TokenId"  TEXT NOT NULL COLLATE NOCASE ,
"ImageFileName"  TEXT NOT NULL,
"IsDefault"  INTEGER DEFAULT 1,
"ImageType"  TEXT,
PRIMARY KEY ("UniqueId" ASC)
);
-- ----------------------------
-- Table structure for GoodsCustTemp
-- ----------------------------
DROP TABLE IF EXISTS "main"."GoodsCustTemp";
CREATE TABLE "GoodsCustTemp" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE,
"Qty"  INTEGER,
"ProductId"  TEXT,
"UnitId"  TEXT,
PRIMARY KEY ("UniqueId")
);

-- ----------------------------
-- Table structure for GoodsCustQuotas
-- ----------------------------
DROP TABLE IF EXISTS "main"."GoodsCustQuotas";
CREATE TABLE "GoodsCustQuotas" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"id"  INTEGER,
"startDate"  TEXT,
"endDate"  TEXT,
"ruleNo"  INTEGER,
"ruleDesc"  TEXT,
"applyInGroup"  INTEGER,
"goodsRef"  INTEGER,
"goodsCtgrRef"  INTEGER,
"mainTypeRef"  INTEGER,
"subTypeRef"  INTEGER,
"dcRef"  INTEGER,
"custRef"  INTEGER,
"custCtgrRef"  INTEGER,
"custActRef"  INTEGER,
"stateRef"  INTEGER,
"countyRef"  INTEGER,
"areaRef"  INTEGER,
"saleOfficeRef"  INTEGER,
"minQty"  INTEGER,
"maxQty"  INTEGER,
"unitRef"  INTEGER,
"checkDuration"  INTEGER,
"hostName"  TEXT,
"userRef"  INTEGER,
"defineDate"  TEXT,
"goodsGroupRef"  INTEGER,
"manufacturerRef"  INTEGER,
"custLevelRef"  INTEGER,
"unitUniqueId"  TEXT,
"unitStatusUniqueId"  TEXT,
"goodUniqueId"  TEXT,
"goodGroupUniqueId"  TEXT,
"dcUniqueId"  TEXT,
"customerUniqueId"  TEXT,
"customerActUniqueId"  TEXT,
"stateUniqueId"  TEXT,
"countyUniqueId"  TEXT,
"areaUniqueId"  TEXT,
PRIMARY KEY ("UniqueId")
);

-- ----------------------------
-- Alter Table structure for OnHandQty
-- ----------------------------
alter table OnHandQty add column HasAllocation INTEGER;

-- ----------------------------
-- View structure for CustomerCallOrderOrderView
-- ----------------------------
DROP VIEW IF EXISTS "main"."CustomerCallOrderOrderView";
CREATE VIEW "CustomerCallOrderOrderView" AS
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
LEFT JOIN TotalProductOrderQtyView ON TotalProductOrderQtyView.ProductId = a.ProductId
LEFT JOIN ProductBatchView ON a.ProductId = ProductBatchView.ProductId;


-- ----------------------------
-- Alter View structure for OperationReportView .
-- Related to issue DMC-34907
-- We only show the records for the customers who have a confirmed payment.
-- ----------------------------
DROP VIEW IF EXISTS "OperationReportView";
CREATE VIEW "OperationReportView" AS
SELECT
	Customer.UniqueId AS UniqueId,
	Customer.CustomerName AS CustomerName,
	Customer.CustomerCode AS CustomerCode,
	Customer.StoreName AS StoreName,
	SellAmount.TotalAmount,
	SellAmount.OrderDiscountAmount,
	SellAmount.OrderAddAmount,
	SellAmount.TotalNetAmount,
	CustomerPayment.Amount as TotalPaidAmount,
	CustomerPayment.AmountCheque as AmountCheque,
	CustomerPayment.AmountCard as AmountCard,
	CustomerPayment.AmountCash as AmountCash,
	CustomerPayment.AmountDiscount as AmountDiscount,
	CustomerPayment.AmountCredit as AmountCredit,
	ifnull(SellAmount.TotalNetAmount,0) - ifnull(CallReturn.RequestAmount,0) as PayableAmount,
	CASE WHEN (SellAmount.TotalNetAmount - CallReturn.RequestAmount - CustomerPayment.AmountCheque - CustomerPayment.AmountCard - CustomerPayment.AmountCash - CustomerPayment.AmountDiscount - CustomerPayment.AmountCredit) > 0 then (SellAmount.TotalNetAmount - CallReturn.RequestAmount - CustomerPayment.AmountCheque - CustomerPayment.AmountCard - CustomerPayment.AmountCash - CustomerPayment.AmountDiscount - CustomerPayment.AmountCredit) else 0 end as Recipe,
	CallReturn.AddAmount as ReturnAddAmount,
	CallReturn.DisAmount as ReturnDiscountAmount,
	CallReturn.RequestAmount as ReturnRequestAmount
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
JOIN CustomerCall On CustomerCall.CallType == 11 AND CustomerCall.ConfirmStatus == 1 AND CustomerCall.CustomerId = Customer.UniqueId;

