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
 ProductBatchView.BatchRef AS BatchRef
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
-- View structure for OrderView
-- ----------------------------
DROP VIEW IF EXISTS "main"."OrderView";
CREATE VIEW "OrderView" AS
SELECT
	UniqueId,
	OrderUniqueId,
	CustomerUniqueId,
	RequestAdd1Amount,
	RequestAdd2Amount,
	RequestTaxAmount,
	RequestChargeAmount,
	RequestDis1Amount,
	RequestDis2Amount,
	RequestDis3Amount,
	RequestOtherDiscountAmount,
	InvoiceAmount,
	InvoiceAdd1Amount,
	InvoiceAdd2Amount,
	InvoiceTaxAmount,
	InvoiceChargeAmount,
	InvoiceOtherDiscountAmount,
	InvoiceDis1Amount,
	InvoiceDis2Amount,
	InvoiceDis3Amount,
	ProductName,
	ProductCode,
	IsFreeItem,
	UnitPrice,
	PriceId,
	ProductId,
	group_concat(Qty, '|') AS Qty,
	group_concat(ConvertFactor, '|') AS ConvertFactor,
	group_concat(UnitName, '|') AS UnitName,
	group_concat(ProductUnitId, '|') AS ProductUnitId,
	sum(TotalQty) AS TotalQty,
	IsRequestFreeItem,
	EmphaticType,
	EmphaticProductCount,
	SaleDate,
	RequestAmount,
	OnHandQty,
	ProductTotalOrderedQty,
	group_concat(FreeReasonId, '|') AS FreeReasonId,
	group_concat(FreeReasonName, '|') AS FreeReasonName,
	CustomerCallOrderOrderView.LocalPaperNo,
	CustomerCallOrderOrderView.OrderPaymentTypeUniqueId,
	CustomerCallOrderOrderView.OrderTypeUniqueId
FROM
	CustomerCallOrderOrderView
GROUP BY
	ProductId,
	IsRequestFreeItem,
	OrderUniqueId;


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
LEFT JOIN CustomerInvoicePayment ON CustomerInvoicePayment.InvoiceId = CustomerOldInvoiceHeader.UniqueId;

-- ----------------------------
-- Table structure for InvoicePaymentInfo
-- ----------------------------
DROP TABLE IF EXISTS "main"."InvoicePaymentInfo";
CREATE TABLE "InvoicePaymentInfo" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"InvoiceId"  TEXT NOT NULL COLLATE NOCASE ,
"PaymentId"  TEXT NOT NULL COLLATE NOCASE ,
"Amount"  REAL NOT NULL,
PRIMARY KEY ("UniqueId" ASC),
CONSTRAINT "fkey0" FOREIGN KEY ("PaymentId") REFERENCES "Payment" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- View structure for InvoicePaymentInfoView
-- ----------------------------
DROP VIEW IF EXISTS "main"."InvoicePaymentInfoView";
CREATE VIEW "InvoicePaymentInfoView" AS
SELECT
	InvoicePaymentInfo.UniqueId as UniqueId,
	a.UniqueId AS InvoiceId,
	a.InvoiceNo AS InvoiceNo,
	a.InvoiceRef AS InvoiceRef,
	a.IsOldInvoice AS IsOldInvoice,
	a.Amount AS Amount,
	ifnull(a.RemAmount, 0) - sum(ifnull(b.Amount,0)) AS TotalRemAmount,
	ifnull(a.RemAmount, 0) - ifnull(InvoicePaymentInfo.Amount,0) AS RemAmount,
	sum(ifnull(InvoicePaymentInfo.Amount,0)) AS PaidAmount,
	sum(ifnull(b.Amount,0)) as TotalPaidAmount,
	InvoicePaymentInfo.PaymentId AS PaymentId,
	Payment.PaymentType AS PaymentType,
	a.CustomerId as CustomerId,
	a.InvoiceDate as InvoiceDate,
	a.PaymentTypeOrderUniqueId AS OrderPaymentTypeUniqueId
FROM
	(
		SELECT
			UniqueId,
			InvoiceNo,
			InvoiceRef,
			1 AS IsOldInvoice,
			Amount,
			RemAmount,
			CustomerId,
			InvoiceDate,
			OldInvoiceHeaderView.PaymentTypeOrderUniqueId AS PaymentTypeOrderUniqueId
		FROM
			OldInvoiceHeaderView
		WHERE
			OldInvoiceHeaderView.HasPayment = 1
		UNION ALL
			SELECT
	CustomerCallOrderOrderView.OrderUniqueId AS UniqueId,
	CustomerCallOrderOrderView.LocalPaperNo AS InvoiceNo,
	NULL AS InvoiceRef,
	0 AS IsOldInvoice,
	sum(
		ifnull(CustomerCallOrderOrderView.RequestAmount,0) -
		ifnull(CustomerCallOrderOrderView.RequestDis1Amount,0) -
		ifnull(CustomerCallOrderOrderView.RequestDis2Amount,0) - ifnull(CustomerCallOrderOrderView.RequestDis3Amount,0) +
		ifnull(CustomerCallOrderOrderView.RequestAdd1Amount,0) + ifnull(CustomerCallOrderOrderView.RequestAdd2Amount,0) +
		ifnull(CustomerCallOrderOrderView.RequestChargeAmount,0) + ifnull(CustomerCallOrderOrderView.RequestTaxAmount,0)
		) AS Amount,
	sum(
		ifnull(CustomerCallOrderOrderView.RequestAmount,0) -
		ifnull(CustomerCallOrderOrderView.RequestDis1Amount,0) -
		ifnull(CustomerCallOrderOrderView.RequestDis2Amount,0) -
		ifnull(CustomerCallOrderOrderView.RequestDis3Amount,0) +
		ifnull(CustomerCallOrderOrderView.RequestAdd1Amount,0) +
		ifnull(CustomerCallOrderOrderView.RequestAdd2Amount,0) +
		ifnull(CustomerCallOrderOrderView.RequestChargeAmount,0) +
		ifnull(CustomerCallOrderOrderView.RequestTaxAmount,0)
	) AS RemAmount,
	CustomerCallOrderOrderView.CustomerUniqueId AS CustomerId,
	CustomerCallOrderOrderView.SaleDate AS InvoiceDate,
	CustomerCallOrderOrderView.OrderPaymentTypeUniqueId AS PaymentTypeOrderUniqueId
FROM
	CustomerCallOrderOrderView
WHERE
	CustomerCallOrderOrderView.IsPromoLine = 0
GROUP BY
	CustomerCallOrderOrderView.OrderUniqueId
	) AS a
LEFT JOIN
(select sum(InvoicePaymentInfo.Amount) as Amount,InvoicePaymentInfo.InvoiceId as InvoiceId FROM InvoicePaymentInfo
 GROUP BY InvoicePaymentInfo.InvoiceId) AS b ON b.InvoiceId = a.UniqueId
LEFT JOIN InvoicePaymentInfo ON InvoicePaymentInfo.InvoiceId = a.UniqueId
LEFT JOIN Payment ON Payment.UniqueId = InvoicePaymentInfo.PaymentId
GROUP BY
	a.UniqueId,
	PaymentType,
	PaymentId
ORDER BY IsOldInvoice, InvoiceDate;