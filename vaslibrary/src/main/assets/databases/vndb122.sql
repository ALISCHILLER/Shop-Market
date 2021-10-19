-- ----------------------------
--  Alter views related to issue: NGT-3597 for InvoiceQty -> bug fixed
-- ----------------------------
DROP VIEW IF EXISTS "main"."CustomerCallReturnView";
CREATE VIEW "CustomerCallReturnView" AS
SELECT CustomerCallReturnLinesView.*,
       CustomerOldInvoiceHeader.SaleNo AS SaleNo,
       TotalRequestNetAmount
  FROM (
           SELECT CustomerCallReturnLinesView.ProductId,
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

