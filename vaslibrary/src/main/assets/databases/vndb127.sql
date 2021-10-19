-- ----------------------------
--  Alter views related to issue: DMC-56170
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

DROP VIEW IF EXISTS CustomerCallReturnView;
CREATE VIEW CustomerCallReturnView AS
SELECT CustomerCallReturnLinesView.*,
           CustomerOldInvoiceHeader.SaleNo AS SaleNo,
           TotalRequestNetAmount
      FROM (
               SELECT CustomerCallReturnLinesView.UniqueId,
                      CustomerCallReturnLinesView.ProductId,
                      CustomerCallReturnLinesView.StockId,
                      CustomerUniqueId AS CustomerUniqueId,
                      ReturnUniqueId AS ReturnUniqueId,
                      CustomerCallReturnLinesView.RequestUnitPrice,
                      CustomerCallReturnLinesView.TotalRequestAmount AS TotalRequestAmount,
                      CustomerCallReturnLinesView.IsFromRequest AS IsFromRequest,
                      CustomerCallReturnLinesView.IsCancelled AS IsCancelled,
                      OriginalTotalReturnQty AS OriginalTotalReturnQty,
                      Comment AS Comment,
                      CustomerCallReturnLinesView.DealerUniqueId AS DealerUniqueId,
                      ProductName AS ProductName,
                      ProductCode AS ProductCode,
                      sum(TotalReturnQty) AS TotalReturnQty,
                      group_concat(ConvertFactor, '|') AS ConvertFactor,
                      group_concat(ProductUnitId, '|') AS ProductUnitId,
                      group_concat(Qty, '|') AS Qty,
                      group_concat(CustomerCallReturnLinesView.UnitName, '|') AS UnitName,
                      group_concat(CustomerCallReturnLinesView.ReturnProductTypeId, ':') AS ReturnProductTypeId,
                      group_concat(CustomerCallReturnLinesView.ReturnReasonId, ':') AS ReturnReasonId,
                      CustomerCallReturnLinesView.InvoiceId,
                      IsPromoLine,
                      sum(CASE WHEN CustomerCallReturnLinesView.IsPromoLine THEN ifnull(CustomerCallReturnLinesView.TotalRequestNetAmount, 0) ELSE ifnull(CustomerCallReturnLinesView.TotalRequestAmount, 0) - ifnull(CustomerCallReturnLinesView.TotalRequestDis1Amount, 0) - ifnull(CustomerCallReturnLinesView.TotalRequestDis2Amount, 0) - ifnull(CustomerCallReturnLinesView.TotalRequestDis3Amount, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestDisOtherAmount, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestAdd1Amount, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestAdd2Amount, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestTax, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestCharge, 0) END) AS TotalRequestNetAmount,
                      sum(CustomerOldInvoiceDetail.TotalQty) AS InvoiceQty
                 FROM CustomerCallReturnLinesView
                      LEFT JOIN
                      (
                          SELECT ProductId,
                                 SaleId,
                                 sum(TotalQty) AS TotalQty
                            FROM CustomerOldInvoiceDetail
                           GROUP BY CustomerOldInvoiceDetail.ProductId,
                                    CustomerOldInvoiceDetail.SaleId
                      )
                      AS CustomerOldInvoiceDetail ON CustomerOldInvoiceDetail.ProductId = CustomerCallReturnLinesView.ProductId AND
                                                     CustomerOldInvoiceDetail.SaleId = CustomerCallReturnLinesView.InvoiceId
                      JOIN
                      ReturnReason ON ReturnReason.UniqueId = CustomerCallReturnLinesView.ReturnReasonId
                GROUP BY CustomerCallReturnLinesView.ProductId,
                         CustomerCallReturnLinesView.ReturnUniqueId,
                         CustomerCallReturnLinesView.InvoiceId
           )
           AS CustomerCallReturnLinesView
           LEFT JOIN
           CustomerOldInvoiceHeader ON CustomerOldInvoiceHeader.UniqueId = InvoiceId;

DROP VIEW IF EXISTS CustomerCallReturnAfterDiscountView;
CREATE VIEW CustomerCallReturnAfterDiscountView AS
    SELECT CustomerCallReturnLinesView.*,
           CustomerOldInvoiceDetail.TotalQty AS InvoiceQty,
           CustomerOldInvoiceHeader.SaleNo AS SaleNo,
           TotalRequestNetAmount
      FROM (
               SELECT CustomerCallReturnLinesView.UniqueId AS UniqueId,
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
                      CAST (sum(TotalReturnQty) AS REAL) AS TotalReturnQty,
                      group_concat(ConvertFactor, '|') AS ConvertFactor,
                      group_concat(ProductUnitId, '|') AS ProductUnitId,
                      group_concat(Qty, '|') AS Qty,
                      group_concat(UnitName, '|') AS UnitName,
                      group_concat(ReturnProductTypeId, ':') AS ReturnProductTypeId,
                      group_concat(ReturnReasonId, ':') AS ReturnReasonId,
                      InvoiceId,
                      IsPromoLine,
                      IsCancelled,
                      sum(CASE WHEN CustomerCallReturnLinesView.IsPromoLine THEN ifnull(CustomerCallReturnLinesView.TotalRequestNetAmount, 0) ELSE ifnull(CustomerCallReturnLinesView.TotalRequestAmount, 0) - ifnull(CustomerCallReturnLinesView.TotalRequestDis1Amount, 0) - ifnull(CustomerCallReturnLinesView.TotalRequestDis2Amount, 0) - ifnull(CustomerCallReturnLinesView.TotalRequestDis3Amount, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestDisOtherAmount, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestAdd1Amount, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestAdd2Amount, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestTax, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestCharge, 0) END) AS TotalRequestNetAmount
                 FROM CustomerCallReturnLinesView
                      JOIN
                      ReturnReason ON ReturnReason.UniqueId = CustomerCallReturnLinesView.ReturnReasonId
                GROUP BY ProductId,
                         ReturnUniqueId,
                         InvoiceId,
                         IsPromoLine
           )
           AS CustomerCallReturnLinesView
           LEFT JOIN
           CustomerOldInvoiceDetail ON CustomerOldInvoiceDetail.ProductId = CustomerCallReturnLinesView.ProductId AND
                                       CustomerOldInvoiceDetail.SaleId = CustomerCallReturnLinesView.InvoiceId AND
                                       PrizeType = IsPromoLine
           LEFT JOIN
           CustomerOldInvoiceHeader ON CustomerOldInvoiceHeader.UniqueId = CustomerOldInvoiceDetail.SaleId AND
                                       CustomerOldInvoiceDetail.SaleId = CustomerCallReturnLinesView.InvoiceId;
