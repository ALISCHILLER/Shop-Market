-- ----------------------------
--  Alter views related
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
	CustomerCallReturnLinesRequestView.TotalReturnQty AS OriginalTotalReturnQty,CustomerCallReturn.ReturnRequestBackOfficeNo

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
--  Alter views related
-- ----------------------------
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
		CustomerCallReturnLinesView.UniqueId AS UniqueId,
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
		CAST ( sum( TotalReturnQty ) AS REAL ) AS TotalReturnQty,
		group_concat( ConvertFactor, '|' ) AS ConvertFactor,
		group_concat( ProductUnitId, '|' ) AS ProductUnitId,
		group_concat( Qty, '|' ) AS Qty,
		group_concat( UnitName, '|' ) AS UnitName,
		group_concat( ReturnProductTypeId, ':' ) AS ReturnProductTypeId,
		group_concat( ReturnReasonId, ':' ) AS ReturnReasonId,
		group_concat( ReturnReasonName, ':' ) AS ReturnReasonName,
		InvoiceId,
		IsPromoLine,
		IsCancelled,
		ReferenceNo,
		sum(
		CASE

				WHEN CustomerCallReturnLinesView.IsPromoLine THEN
				ifnull( CustomerCallReturnLinesView.TotalRequestNetAmount, 0 ) ELSE ifnull( CustomerCallReturnLinesView.TotalRequestAmount, 0 ) - ifnull( CustomerCallReturnLinesView.TotalRequestDis1Amount, 0 ) - ifnull( CustomerCallReturnLinesView.TotalRequestDis2Amount, 0 ) - ifnull( CustomerCallReturnLinesView.TotalRequestDis3Amount, 0 ) + ifnull( CustomerCallReturnLinesView.TotalRequestDisOtherAmount, 0 ) + ifnull( CustomerCallReturnLinesView.TotalRequestAdd1Amount, 0 ) + ifnull( CustomerCallReturnLinesView.TotalRequestAdd2Amount, 0 ) + ifnull( CustomerCallReturnLinesView.TotalRequestAddOtherAmount, 0 ) + ifnull( CustomerCallReturnLinesView.TotalRequestTax, 0 ) + ifnull( CustomerCallReturnLinesView.TotalRequestCharge, 0 )
			END
			) AS TotalRequestNetAmount,
			CustomerCallReturnLinesView.EditReasonId,
			NoSaleReason.NoSaleReasonName AS EditReasonName,
			CustomerCallReturnLinesView.ReturnRequestBackOfficeNo
		FROM
			CustomerCallReturnLinesView
			JOIN ReturnReason ON ReturnReason.UniqueId = CustomerCallReturnLinesView.ReturnReasonId
			LEFT JOIN NoSaleReason ON NoSaleReason.UniqueId = CustomerCallReturnLinesView.EditReasonId
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

