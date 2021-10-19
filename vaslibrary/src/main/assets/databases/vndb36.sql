-- ----------------------------
-- Alter view structure for CustomerCallOrderPreview.
-- This fixes the bug for orders which do not have any lines
-- Related to issue DMC-31495
-- ----------------------------
DROP VIEW IF EXISTS "main"."CustomerCallOrderPreview";
CREATE VIEW "CustomerCallOrderPreview" AS
SELECT
	CustomerCallOrder.UniqueId,
	CustomerCallOrder.CustomerUniqueId,
	ifnull(SUM(a.TotalPrice),0) as TotalPrice,
	ifnull(SUM(a.TotalQty),0) as TotalQty,
	CustomerCallOrder.LocalPaperNo,
	CustomerCallOrder.Comment
FROM
CustomerCallOrder LEFT JOIN
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
	) as a
ON CustomerCallOrder.UniqueId = a.OrderUniqueId
GROUP BY
	OrderUniqueId
ORDER BY CustomerCallOrder.CustomerUniqueId;