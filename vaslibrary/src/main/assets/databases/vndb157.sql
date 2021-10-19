-- ----------------------------
--  Alter table CustomerCallReturnAfterDiscountView add EditReasonName for thirdParty
-- ----------------------------
DROP VIEW IF EXISTS "main"."CustomerCallReturnAfterDiscountView";
CREATE VIEW "CustomerCallReturnAfterDiscountView" AS
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
											group_concat(ReturnReasonName, ':') AS ReturnReasonName,
                      InvoiceId,
                      IsPromoLine,
                      IsCancelled,
                      ReferenceNo,
                      sum(CASE WHEN CustomerCallReturnLinesView.IsPromoLine THEN ifnull(CustomerCallReturnLinesView.TotalRequestNetAmount, 0) ELSE ifnull(CustomerCallReturnLinesView.TotalRequestAmount, 0) - ifnull(CustomerCallReturnLinesView.TotalRequestDis1Amount, 0) - ifnull(CustomerCallReturnLinesView.TotalRequestDis2Amount, 0) - ifnull(CustomerCallReturnLinesView.TotalRequestDis3Amount, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestDisOtherAmount, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestAdd1Amount, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestAdd2Amount, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestAddOtherAmount, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestTax, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestCharge, 0) END) AS TotalRequestNetAmount,
                      CustomerCallReturnLinesView.EditReasonId,
											NoSaleReason.NoSaleReasonName AS EditReasonName
                 FROM CustomerCallReturnLinesView
                      JOIN
                      ReturnReason ON ReturnReason.UniqueId = CustomerCallReturnLinesView.ReturnReasonId
											LEFT JOIN
                      NoSaleReason ON NoSaleReason.UniqueId = CustomerCallReturnLinesView.EditReasonId
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