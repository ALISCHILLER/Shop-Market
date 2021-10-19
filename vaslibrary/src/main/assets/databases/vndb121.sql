-- ----------------------------
--  Alter views related to issue: NGT-3582
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
LEFT JOIN CustomerOldInvoiceHeader ON CustomerOldInvoiceHeader.UniqueId = CustomerCallReturn.BackOfficeInvoiceId
LEFT JOIN CustomerCallReturnLinesRequestView ON CustomerCallReturnLinesRequestView.UniqueId = CustomerCallReturnLines.UniqueId
GROUP BY
	ReturnLineUniqueId;

DROP VIEW IF EXISTS "main"."CustomerCallReturnView";
CREATE VIEW "CustomerCallReturnView" AS
SELECT
	CustomerCallReturnLinesView.*,
	CustomerOldInvoiceHeader.SaleNo AS SaleNo,
	TotalRequestNetAmount
FROM
	(
	SELECT
		CustomerCallReturnLinesView.ProductId,
		CustomerCallReturnLinesView.StockId,
		CustomerUniqueId AS CustomerUniqueId,
		ReturnUniqueId AS ReturnUniqueId,
		CustomerCallReturnLinesView.RequestUnitPrice,
		CustomerCallReturnLinesView.TotalRequestAmount AS TotalRequestAmount,
		CustomerCallReturnLinesView.IsFromRequest AS IsFromRequest,
		OriginalTotalReturnQty AS OriginalTotalReturnQty,
		Comment AS Comment,
		CustomerCallReturnLinesView.DealerUniqueId AS DealerUniqueId,
		ProductName AS ProductName,
		ProductCode AS ProductCode,
		sum( TotalReturnQty ) AS TotalReturnQty,
		group_concat( ConvertFactor, '|' ) AS ConvertFactor,
		group_concat( ProductUnitId, '|' ) AS ProductUnitId,
		group_concat( Qty, '|' ) AS Qty,
		group_concat( CustomerCallReturnLinesView.UnitName, '|' ) AS UnitName,
		group_concat( CustomerCallReturnLinesView.ReturnProductTypeId, ':' ) AS ReturnProductTypeId,
		group_concat( CustomerCallReturnLinesView.ReturnReasonId, ':' ) AS ReturnReasonId,
		CustomerCallReturnLinesView.InvoiceId,
		IsPromoLine,
		sum(
		CASE

				WHEN CustomerCallReturnLinesView.IsPromoLine THEN
				ifnull( CustomerCallReturnLinesView.TotalRequestNetAmount, 0 ) ELSE ifnull( CustomerCallReturnLinesView.TotalRequestAmount, 0 ) - ifnull( CustomerCallReturnLinesView.TotalRequestDis1Amount, 0 ) - ifnull( CustomerCallReturnLinesView.TotalRequestDis2Amount, 0 ) - ifnull( CustomerCallReturnLinesView.TotalRequestDis3Amount, 0 ) + ifnull( CustomerCallReturnLinesView.TotalRequestDisOtherAmount, 0 ) + ifnull( CustomerCallReturnLinesView.TotalRequestAdd1Amount, 0 ) + ifnull( CustomerCallReturnLinesView.TotalRequestAdd2Amount, 0 ) + ifnull( CustomerCallReturnLinesView.TotalRequestTax, 0 ) + ifnull( CustomerCallReturnLinesView.TotalRequestCharge, 0 )
			END
			) AS TotalRequestNetAmount,
			CustomerOldInvoiceDetail.TotalQty AS InvoiceQty
		FROM
			CustomerCallReturnLinesView
			LEFT JOIN CustomerOldInvoiceDetail ON CustomerOldInvoiceDetail.ProductId = CustomerCallReturnLinesView.ProductId
			AND CustomerOldInvoiceDetail.SaleId = CustomerCallReturnLinesView.InvoiceId
			AND PrizeType = IsPromoLine
			JOIN ReturnReason ON ReturnReason.UniqueId = CustomerCallReturnLinesView.ReturnReasonId
		GROUP BY
			CustomerCallReturnLinesView.ProductId,
			CustomerCallReturnLinesView.ReturnUniqueId,
			CustomerCallReturnLinesView.InvoiceId
		) AS CustomerCallReturnLinesView
	LEFT JOIN CustomerOldInvoiceHeader ON CustomerOldInvoiceHeader.UniqueId = InvoiceId;

DROP VIEW IF EXISTS "main"."CustomerCallReturnAfterDiscountView";
CREATE VIEW "CustomerCallReturnAfterDiscountView" AS
SELECT
	CustomerCallReturnLinesView.*,
	CustomerOldInvoiceDetail.TotalQty AS InvoiceQty,
	CustomerOldInvoiceHeader.SaleNo AS SaleNo,
	TotalRequestNetAmount
FROM
	(
	SELECT
		ProductId,
		StockId,
		CustomerUniqueId AS CustomerUniqueId,
		ReturnUniqueId AS ReturnUniqueId,
		RequestUnitPrice,
		TotalRequestAmount AS TotalRequestAmount,
		IsFromRequest AS IsFromRequest,
		OriginalTotalReturnQty AS OriginalTotalReturnQty,
		Comment AS Comment,
		DealerUniqueId AS DealerUniqueId,
		ProductName AS ProductName,
		ProductCode AS ProductCode,
		sum( TotalReturnQty ) AS TotalReturnQty,
		group_concat( ConvertFactor, '|' ) AS ConvertFactor,
		group_concat( ProductUnitId, '|' ) AS ProductUnitId,
		group_concat( Qty, '|' ) AS Qty,
		group_concat( UnitName, '|' ) AS UnitName,
		group_concat( ReturnProductTypeId, ':' ) AS ReturnProductTypeId,
		group_concat( ReturnReasonId, ':' ) AS ReturnReasonId,
		InvoiceId,
		IsPromoLine,
		sum(
		CASE

				WHEN CustomerCallReturnLinesView.IsPromoLine THEN
				ifnull( CustomerCallReturnLinesView.TotalRequestNetAmount, 0 ) ELSE ifnull( CustomerCallReturnLinesView.TotalRequestAmount, 0 ) - ifnull( CustomerCallReturnLinesView.TotalRequestDis1Amount, 0 ) - ifnull( CustomerCallReturnLinesView.TotalRequestDis2Amount, 0 ) - ifnull( CustomerCallReturnLinesView.TotalRequestDis3Amount, 0 ) + ifnull( CustomerCallReturnLinesView.TotalRequestDisOtherAmount, 0 ) + ifnull( CustomerCallReturnLinesView.TotalRequestAdd1Amount, 0 ) + ifnull( CustomerCallReturnLinesView.TotalRequestAdd2Amount, 0 ) + ifnull( CustomerCallReturnLinesView.TotalRequestTax, 0 ) + ifnull( CustomerCallReturnLinesView.TotalRequestCharge, 0 )
			END
			) AS TotalRequestNetAmount
		FROM
			CustomerCallReturnLinesView
			JOIN ReturnReason ON ReturnReason.UniqueId = CustomerCallReturnLinesView.ReturnReasonId
		GROUP BY
			ProductId,
			ReturnUniqueId,
			InvoiceId,
			IsPromoLine
		) AS CustomerCallReturnLinesView
		LEFT JOIN CustomerOldInvoiceDetail ON CustomerOldInvoiceDetail.ProductId = CustomerCallReturnLinesView.ProductId
		AND CustomerOldInvoiceDetail.SaleId = CustomerCallReturnLinesView.InvoiceId
		AND PrizeType = IsPromoLine
	LEFT JOIN CustomerOldInvoiceHeader ON CustomerOldInvoiceHeader.UniqueId = CustomerOldInvoiceDetail.SaleId
	AND CustomerOldInvoiceDetail.SaleId = CustomerCallReturnLinesView.InvoiceId;

DROP VIEW IF EXISTS "main"."OldInvoiceDetailView";
CREATE VIEW "OldInvoiceDetailView" AS
SELECT
CustomerOldInvoiceHeader.CustomerId,
CustomerOldInvoiceHeader.StockId,
CustomerOldInvoiceDetail.SaleId AS SaleId,
CustomerOldInvoiceHeader.SaleNo,
CustomerOldInvoiceDetail.ProductId ,
CustomerOldInvoiceDetail.PrizeType,
AddAmount,
UnitQty,
TotalQty,
UnitPrice,
CustomerOldInvoiceHeader.SalePDate,
Product.ProductName,
Product.ProductCode,
Product.ProductGroupId,
CustomerOldInvoiceDetail.TotalAmount,
CustomerCallReturnView.TotalReturnQty as TotalReturnQty,
CustomerCallReturnView.TotalRequestAmount as TotalRequestAmount
FROM (
SELECT SaleId, ProductId,
	SUM(CustomerOldInvoiceDetail.AddAmount) as AddAmount,
	SUM(CustomerOldInvoiceDetail.UnitQty) as UnitQty,
	SUM(CustomerOldInvoiceDetail.TotalQty) as TotalQty,
	CustomerOldInvoiceDetail.PrizeType as PrizeType,
	MAX(CustomerOldInvoiceDetail.UnitPrice * ABS(PrizeType -1)) as UnitPrice,
	SUM((CustomerOldInvoiceDetail.UnitPrice * CustomerOldInvoiceDetail.TotalQty) * ABS(PrizeType -1)) AS TotalAmount
FROM   CustomerOldInvoiceDetail
GROUP BY CustomerOldInvoiceDetail.ProductId , CustomerOldInvoiceDetail.SaleId
) as CustomerOldInvoiceDetail
JOIN Product ON CustomerOldInvoiceDetail.ProductId = Product.UniqueId
LEFT JOIN CustomerCallReturnView ON CustomerCallReturnView.ProductId = Product.UniqueId AND
CustomerCallReturnView.InvoiceId = SaleId
JOIN CustomerOldInvoiceHeader ON CustomerOldInvoiceHeader.UniqueId = CustomerOldInvoiceDetail.SaleId;

-- ----------------------------
--  Alter views related to issue: NGT-3610
-- ----------------------------
DROP VIEW IF EXISTS "main"."OperationReportView";
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
		sum(ifnull(CustomerCallReturnLinesView.TotalRequestAdd1Amount,0) + ifnull(CustomerCallReturnLinesView.TotalRequestAdd2Amount,0) + ifnull(CustomerCallReturnLinesView.TotalRequestAddOtherAmount,0) + ifnull(CustomerCallReturnLinesView.TotalRequestTax,0) + ifnull(CustomerCallReturnLinesView.TotalRequestCharge,0)) as AddAmount,
		sum(ifnull(CustomerCallReturnLinesView.TotalRequestDis1Amount,0) + ifnull(CustomerCallReturnLinesView.TotalRequestDis2Amount,0) + ifnull(CustomerCallReturnLinesView.TotalRequestDis3Amount,0) + ifnull(CustomerCallReturnLinesView.TotalRequestDisOtherAmount,0))as DisAmount
	FROM
		CustomerCallReturnLinesView
	GROUP BY CustomerCallReturnLinesView.CustomerUniqueId
) CallReturn ON CallReturn.CustomerUniqueId = Customer.UniqueId
JOIN CustomerCall On (CustomerCall.CallType == 0 OR CustomerCall.CallType=11) AND CustomerCall.ConfirmStatus == 1 AND CustomerCall.CustomerId = Customer.UniqueId
GROUP BY CustomerCall.CustomerId;

-- ----------------------------
-- Table structure for TargetReviewReportView
-- ----------------------------
DROP TABLE IF EXISTS "TargetReviewReportView";
CREATE TABLE "TargetReviewReportView" (
  "UniqueId" text NOT NULL,
  "CalcDate" TEXT,
  "Condition" TEXT,
  "Type" TEXT,
  "Title" TEXT,
  "Target" real,
  "TargettoDate" integer,
  "AchievementDate" real,
  "Achievement" real,
  "DailyPlan" integer,
  "AchievementStimate" integer,
  "CalculationPeriodId" integer,
  PRIMARY KEY ("UniqueId")
);

-- ----------------------------
--  Alter view CustomerCallOrderLines related to issue: NGT-3631
-- ----------------------------
alter table CustomerCallOrderLines add column RequestOtherAddAmount REAL;
alter table CustomerCallInvoiceLines add column RequestOtherAddAmount REAL;

-- ----------------------------
--  Alter view customerCallOrderOrderView related to issue: DMC-45928
-- ----------------------------
DROP VIEW IF EXISTS "main"."customerCallOrderOrderView";
CREATE VIEW "customerCallOrderOrderView" AS
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
 OnHandQty.HasAllocation AS HasAllocation,
 CASE
  WHEN a.RequestBulkQtyUnitUniqueId IS NULL THEN
   (a.TotalQty * (productWeight*0.001))
  ELSE
  (a.RequestBulkQty)
 END AS TotalWeight
FROM
	(
		SELECT
			CustomerCallOrderLines.UniqueId AS UniqueId,
			CustomerCallOrderLines.OrderUniqueId AS OrderUniqueId,
			CustomerCallOrder.CustomerUniqueId AS CustomerUniqueId,
			CustomerCallOrderLines.FreeReasonId AS FreeReasonId,
			CustomerCallOrderLines.RequestAdd1Amount AS RequestAdd1Amount,
			CustomerCallOrderLines.RequestAdd2Amount AS RequestAdd2Amount,
			CustomerCallOrderLines.RequestOtherAddAmount AS RequestAddOtherAmount,
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
			Qty * ConvertFactor As TotalQtyBulk,
			CustomerCallOrderLines.SortId AS SortId,
			CustomerCallOrderLines.RequestBulkQtyUnitUniqueId AS RequestBulkQtyUnitUniqueId,
			CustomerCallOrderLines.IsPromoLine AS IsPromoLine,
			CustomerCallOrderLines.PromotionPrice AS PromotionPrice,
			CustomerCallOrderLines.PayDuration AS PayDuration,
			CustomerCallOrderLines.RuleNo AS RuleNo,
			Product.ProductName AS ProductName,
			Product.ProductCode AS ProductCode,
			Product.IsFreeItem AS IsFreeItem,
			Product.OrderPoint AS OrderPoint,
			Product.PayDuration AS ProductPayDuration,
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
			CustomerEmphaticProduct.TypeLawUniqueId AS TypeLawUniqueId,
			CustomerEmphaticProduct.EmphasisRuleId AS EmphasisRuleId,
			CustomerCallOrder.SaleDate AS SaleDate,
			CustomerCallOrder.LocalPaperNo AS LocalPaperNo,
			CustomerCallOrder.OrderPaymentTypeUniqueId AS OrderPaymentTypeUniqueId,
			CustomerCallOrder.OrderTypeUniqueId AS OrderTypeUniqueId,
			IFNULL(Product.productWeight,0) As productWeight
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