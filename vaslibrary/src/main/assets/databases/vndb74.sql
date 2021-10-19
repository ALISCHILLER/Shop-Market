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