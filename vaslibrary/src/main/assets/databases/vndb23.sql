/*
Date: 2018-05-22 12:27:54
*/
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
	SUM(case when CustomerCall.CallType = 0 then CustomerCallOrderOrderView.RequestAmount else 0 end) AS TotalOrderNetAmount,
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
--  CustomerCallOrderPreview
-- ----------------------------
DROP VIEW IF EXISTS "main"."CustomerCallOrderPreview";
CREATE VIEW CustomerCallOrderPreview AS
SELECT
	OrderUniqueId AS UniqueId,
	CustomerUniqueId,
	CASE
	WHEN RequestBulkQtyUnitUniqueId IS NULL THEN
		(TotalQty * UnitPrice)
	ELSE
		(RequestBulkQty * UnitPrice)
	END AS TotalPrice,
	TotalQty,
	LocalPaperNo,
	Comment
FROM
	(
		SELECT
			CustomerCallOrderLines.OrderUniqueId AS OrderUniqueId,
			CustomerCallOrder.CustomerUniqueId AS CustomerUniqueId,
			CustomerCallOrderLines.RequestBulkQty AS RequestBulkQty,
			CustomerCallOrderLines.RequestBulkQtyUnitUniqueId AS RequestBulkQtyUnitUniqueId,
			CustomerPrice.Price AS UnitPrice,
			CASE
			WHEN CustomerCallOrderLines.RequestBulkQtyUnitUniqueId IS NULL THEN
				sum(Qty * ConvertFactor)
			ELSE
				CustomerCallOrderLines.RequestBulkQty
			END AS TotalQty,
		CustomerCallOrder.LocalPaperNo AS LocalPaperNo,
		CustomerCallOrder.Comment AS Comment
	FROM
		CustomerCallOrderLines,
		ProductUnit ON CustomerCallOrderLinesOrderQtyDetail.ProductUnitId = ProductUnit.UniqueId,
		CustomerCallOrderLinesOrderQtyDetail ON CustomerCallOrderLinesOrderQtyDetail.OrderLineUniqueId = CustomerCallOrderLines.UniqueId,
		Product ON Product.UniqueId = CustomerCallOrderLines.ProductUniqueId,
		CustomerCallOrder ON CustomerCallOrder.UniqueId = CustomerCallOrderLines.OrderUniqueId
	LEFT JOIN CustomerPrice ON (
		CustomerPrice.CustomerUniqueId = CustomerCallOrder.CustomerUniqueId
	)
	AND (
		CustomerPrice.ProductUniqueId = CustomerCallOrderLines.ProductUniqueId
	)
	GROUP BY
		OrderLineUniqueId
	);

-- ----------------------------
--  CustomerPathView
-- ----------------------------
DROP VIEW IF EXISTS "main"."CustomerPathView";
CREATE VIEW CustomerPathView AS
SELECT
	c.*, VisitTemplatePathCustomer.VisitTemplatePathId AS VisitTemplatePathId
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
JOIN VisitTemplatePathCustomer ON c.UniqueId = VisitTemplatePathCustomer.CustomerId;


-- ----------------------------
-- CustomerCallOrderOrderView
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
LEFT JOIN TotalProductOrderQtyView ON TotalProductOrderQtyView.ProductId = a.ProductId;

-- ----------------------------
--  OrderView
-- ----------------------------
DROP VIEW IF EXISTS "main"."OrderView";
CREATE VIEW OrderView AS
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
	group_concat(FreeReasonName, '|') AS FreeReasonName
FROM
	CustomerCallOrderOrderView
GROUP BY
	ProductId,
	IsRequestFreeItem,
	OrderUniqueId;
