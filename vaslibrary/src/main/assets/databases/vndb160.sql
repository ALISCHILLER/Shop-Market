-- ----------------------------
--  Alter views related to issue: NGT-3831
-- ----------------------------
DROP VIEW IF EXISTS CustomerCallReturnLinesView;
CREATE VIEW CustomerCallReturnLinesView AS
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
 	CustomerCallReturn.IsCancelled as IsCancelled,
	CustomerCallReturn.BackOfficeInvoiceId AS InvoiceId,
	CustomerCallReturn.Comment AS Comment,
	CustomerCallReturn.DealerUniqueId AS DealerUniqueId,
	CustomerOldInvoiceHeader.SaleNo AS SaleNo,
	CustomerCallReturnLinesRequestView.TotalReturnQty AS OriginalTotalReturnQty,
	CASE
	    WHEN CustomerCallReturnLines.RequestBulkUnitId IS NULL THEN
            sum(CustomerCallReturnLinesQtyDetail.Qty * ProductUnit.ConvertFactor)*(IFNULL(Product.productWeight,0)*0.001)
        ELSE
            CustomerCallReturnLines.RequestBulkQty
    END AS TotalWeight
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


-- ----------------------------
-- Alter views related to issue: NGT-3831
-- ----------------------------
DROP VIEW IF EXISTS "main"."ReturnReportView";
CREATE VIEW ReturnReportView AS
SELECT
	group_concat(ReturnProductTypeId, ':') AS ReturnProductTypeId,
	group_concat(ReturnReasonId, ':') AS ReturnReasonId,
	CustomerCallReturnLinesView.ProductName AS ProductName,
	CustomerCallReturnLinesView.ProductCode AS ProductCode,
	CustomerCallReturnLinesView.ProductId AS ProductId,
	group_concat(
		CustomerCallReturnLinesView.ConvertFactor,
		'|'
	) AS ConvertFactor,
	group_concat(
		CustomerCallReturnLinesView.ProductUnitId,
		'|'
	) AS ProductUnitId,
	group_concat(
		CustomerCallReturnLinesView.Qty,
		'|'
	) AS Qty,
	group_concat(
		CustomerCallReturnLinesView.UnitName,
		'|'
	) AS UnitName,
	sum(
		CustomerCallReturnLinesView.TotalReturnQty
	) AS TotalReturnQty,
	CustomerCallReturnLinesView.RequestUnitPrice,
	sum(CustomerCallReturnLinesView.TotalRequestAmount) AS TotalRequestAmount,
	sum(CustomerCallReturnLinesView.TotalWeight) AS TotalWeight
FROM
	CustomerCallReturnLinesView
JOIN ReturnReason ON ReturnReason.UniqueId = CustomerCallReturnLinesView.ReturnReasonId
LEFT JOIN CustomerOldInvoiceDetail ON CustomerOldInvoiceDetail.ProductId = CustomerCallReturnLinesView.ProductId
and CustomerOldInvoiceDetail.SaleId = CustomerCallReturnLinesView.InvoiceId
LEFT JOIN CustomerOldInvoiceHeader ON CustomerOldInvoiceHeader.UniqueId = CustomerOldInvoiceDetail.SaleId
AND CustomerOldInvoiceDetail.SaleId = CustomerCallReturnLinesView.InvoiceId
GROUP BY
	CustomerCallReturnLinesView.ProductId;
