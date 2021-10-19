ALTER TABLE DiscountVnLt ADD COLUMN TotalDiscount REAL NULL;

-- ----------------------------
-- add PromotionPrice to CustomerCallOrderLines
-- ----------------------------
alter table CustomerCallOrderLines add column PromotionPrice REAL;

-- ----------------------------
-- alter CustomerCallOrderPreview use PromotionPrice for promotions
-- ----------------------------
DROP VIEW IF EXISTS CustomerCallOrderPreview;
CREATE VIEW CustomerCallOrderPreview AS
SELECT
	OrderUniqueId AS UniqueId,
	CustomerUniqueId,
	SUM(TotalPrice) as TotalPrice,
	SUM(TotalQty) as TotalQty,
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
		CustomerCallOrder.LocalPaperNo AS LocalPaperNo,
		CustomerCallOrder.Comment AS Comment
	FROM
		CustomerCallOrderLines,
		ProductUnit ON CustomerCallOrderLinesOrderQtyDetail.ProductUnitId = ProductUnit.UniqueId,
		CustomerCallOrderLinesOrderQtyDetail ON CustomerCallOrderLinesOrderQtyDetail.OrderLineUniqueId = CustomerCallOrderLines.UniqueId,
		Product ON Product.UniqueId = CustomerCallOrderLines.ProductUniqueId,
		CustomerCallOrder ON CustomerCallOrder.UniqueId = CustomerCallOrderLines.OrderUniqueId
	LEFT JOIN CustomerPrice ON (CustomerPrice.CustomerUniqueId = CustomerCallOrder.CustomerUniqueId)
	AND (CustomerPrice.ProductUniqueId = CustomerCallOrderLines.ProductUniqueId)
	GROUP BY OrderLineUniqueId
	)
GROUP BY
	OrderUniqueId;


-- ----------------------------
-- alter CustomerCallOrderOrderView add PromotionPrice
-- ----------------------------
DROP VIEW IF EXISTS CustomerCallOrderOrderView;
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