-- ----------------------------
--  Alter views related to issue: NGT-3674
-- ----------------------------
DROP VIEW IF EXISTS CustomerCallReturnLinesRequestView;
CREATE VIEW CustomerCallReturnLinesRequestView AS
SELECT
	CustomerCallReturnLinesRequest.*, CustomerCallReturnRequest.CustomerUniqueId AS CustomerUniqueId,
	Product.ProductName AS ProductName,
	Product.ProductCode AS ProductCode,
	Product.UniqueId AS ProductId,
	group_concat(CustomerCallReturnLinesQtyDetailRequest.Qty, ':') AS Qty,
	group_concat(ProductUnit.ConvertFactor, ':') AS ConvertFactor,
	group_concat(
		CustomerCallReturnLinesQtyDetailRequest.ProductUnitId,
		':'
	) AS ProductUnitId,
	group_concat(Unit.UnitName, ':') AS UnitName,
	CASE
       WHEN CustomerCallReturnLinesRequest.RequestBulkUnitId IS NULL THEN
          sum(CustomerCallReturnLinesQtyDetailRequest.Qty * ProductUnit.ConvertFactor)
       ELSE
          CustomerCallReturnLinesRequest.RequestBulkQty
      END AS TotalReturnQty,
	CASE
		WHEN CustomerCallReturnLinesRequest.RequestBulkUnitId IS NULL THEN
		   CustomerCallReturnLinesRequest.RequestUnitPrice * sum(CustomerCallReturnLinesQtyDetailRequest.Qty * ProductUnit.ConvertFactor)
		ELSE
		   CustomerCallReturnLinesRequest.RequestBulkQty * CustomerCallReturnLinesRequest.RequestUnitPrice
	END AS TotalRequestAmount,
	CustomerCallReturnRequest.IsFromRequest as IsFromRequest,
	CustomerCallReturnRequest.BackOfficeInvoiceId AS InvoiceId,
	CustomerCallReturnRequest.Comment AS Comment,
	CustomerCallReturnRequest.DealerUniqueId AS DealerUniqueId,
	CustomerOldInvoiceHeader.SaleNo AS SaleNo
FROM
	CustomerCallReturnLinesRequest
JOIN ProductUnit ON CustomerCallReturnLinesQtyDetailRequest.ProductUnitId = ProductUnit.UniqueId
JOIN CustomerCallReturnLinesQtyDetailRequest ON CustomerCallReturnLinesQtyDetailRequest.ReturnLineUniqueId = CustomerCallReturnLinesRequest.UniqueId
JOIN Product ON Product.UniqueId = CustomerCallReturnLinesRequest.ProductUniqueId
JOIN Unit ON Unit.UniqueId = ProductUnit.UnitId
JOIN CustomerCallReturnRequest ON CustomerCallReturnRequest.UniqueId = CustomerCallReturnLinesRequest.ReturnUniqueId
JOIN ReturnReason ON ReturnReason.UniqueId = CustomerCallReturnLinesRequest.ReturnReasonId
LEFT JOIN CustomerOldInvoiceHeader ON CustomerOldInvoiceHeader.UniqueId = CustomerCallReturnRequest.BackOfficeInvoiceId
GROUP BY
	ReturnLineUniqueId;

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
