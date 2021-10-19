-- ----------------------------
--  Alter InvoicePaymentInfoView related to issue: DMC-56654
-- ----------------------------
DROP VIEW IF EXISTS InvoicePaymentInfoView;
CREATE VIEW InvoicePaymentInfoView AS
    SELECT InvoicePaymentInfo.UniqueId AS UniqueId,
           a.UniqueId AS InvoiceId,
           a.InvoiceNo AS InvoiceNo,
           a.InvoiceRef AS InvoiceRef,
           a.IsOldInvoice AS IsOldInvoice,
           a.Amount AS Amount,
           ifnull(a.RemAmount, 0) - sum(ifnull(b.Amount, 0) ) AS TotalRemAmount,
           ifnull(a.RemAmount, 0) - ifnull(InvoicePaymentInfo.Amount, 0) AS RemAmount,
           sum(ifnull(InvoicePaymentInfo.Amount, 0) ) AS PaidAmount,
           sum(ifnull(b.Amount, 0) ) AS TotalPaidAmount,
           InvoicePaymentInfo.PaymentId AS PaymentId,
           Payment.PaymentType AS PaymentType,
           a.CustomerId AS CustomerId,
           a.InvoiceDate AS InvoiceDate,
           a.PaymentTypeOrderUniqueId AS OrderPaymentTypeUniqueId
      FROM (
               SELECT UniqueId,
                      InvoiceNo,
                      InvoiceRef,
                      1 AS IsOldInvoice,
                      Amount,
                      RemAmount,
                      CustomerId,
                      InvoiceDate,
                      OldInvoiceHeaderView.PaymentTypeOrderUniqueId AS PaymentTypeOrderUniqueId
                 FROM OldInvoiceHeaderView
                WHERE OldInvoiceHeaderView.HasPayment = 1
               UNION ALL
               SELECT CustomerCallOrderOrderView.OrderUniqueId AS UniqueId,
                      CustomerCallOrderOrderView.LocalPaperNo AS InvoiceNo,
                      NULL AS InvoiceRef,
                      0 AS IsOldInvoice,
                      sum(ifnull(CustomerCallOrderOrderView.RequestAmount, 0) - ifnull(CustomerCallOrderOrderView.RequestDis1Amount, 0) - ifnull(CustomerCallOrderOrderView.RequestDis2Amount, 0) - ifnull(CustomerCallOrderOrderView.RequestDis3Amount, 0) - ifnull(CustomerCallOrderOrderView.RequestOtherDiscountAmount, 0) + ifnull(CustomerCallOrderOrderView.RequestAdd1Amount, 0) + ifnull(CustomerCallOrderOrderView.RequestAdd2Amount, 0) + ifnull(CustomerCallOrderOrderView.RequestAddOtherAmount, 0) + ifnull(CustomerCallOrderOrderView.RequestChargeAmount, 0) + ifnull(CustomerCallOrderOrderView.RequestTaxAmount, 0) ) AS Amount,
                      sum(ifnull(CustomerCallOrderOrderView.RequestAmount, 0) - ifnull(CustomerCallOrderOrderView.RequestDis1Amount, 0) - ifnull(CustomerCallOrderOrderView.RequestDis2Amount, 0) - ifnull(CustomerCallOrderOrderView.RequestDis3Amount, 0) - ifnull(CustomerCallOrderOrderView.RequestOtherDiscountAmount, 0) + ifnull(CustomerCallOrderOrderView.RequestAdd1Amount, 0) + ifnull(CustomerCallOrderOrderView.RequestAdd2Amount, 0) + ifnull(CustomerCallOrderOrderView.RequestAddOtherAmount, 0) + ifnull(CustomerCallOrderOrderView.RequestChargeAmount, 0) + ifnull(CustomerCallOrderOrderView.RequestTaxAmount, 0) ) AS RemAmount,
                      CustomerCallOrderOrderView.CustomerUniqueId AS CustomerId,
                      CustomerCallOrderOrderView.SaleDate AS InvoiceDate,
                      CustomerCallOrderOrderView.OrderPaymentTypeUniqueId AS PaymentTypeOrderUniqueId
                 FROM CustomerCallOrderOrderView
                WHERE CustomerCallOrderOrderView.IsPromoLine = 0
                GROUP BY CustomerCallOrderOrderView.OrderUniqueId
           )
           AS a
           LEFT JOIN
           (
               SELECT sum(InvoicePaymentInfo.Amount) AS Amount,
                      InvoicePaymentInfo.InvoiceId AS InvoiceId
                 FROM InvoicePaymentInfo
                GROUP BY InvoicePaymentInfo.InvoiceId
           )
           AS b ON b.InvoiceId = a.UniqueId
           LEFT JOIN
           InvoicePaymentInfo ON InvoicePaymentInfo.InvoiceId = a.UniqueId
           LEFT JOIN
           Payment ON Payment.UniqueId = InvoicePaymentInfo.PaymentId
     GROUP BY a.UniqueId,
              PaymentType,
              PaymentId
     ORDER BY IsOldInvoice,
              InvoiceDate;

DROP VIEW IF EXISTS RequestReportView;
CREATE VIEW RequestReportView AS
    SELECT Customer.UniqueId,
           Customer.CustomerName,
           Customer.CustomerCode,
           Customer.StoreName,
           CustomerCallOrder.UniqueId AS OrderUniqueId,
           PaymentTypeOrder.PaymentTypeOrderName AS PaymentTypeBaseName,
           SUM(CASE WHEN CustomerCall.ExtraField1 = CustomerCallOrder.UniqueId THEN (IFNULL(CustomerCallOrderOrderView.RequestAmount, 0) - IFNULL(CustomerCallOrderOrderView.RequestDis1Amount, 0) - IFNULL(CustomerCallOrderOrderView.RequestDis2Amount, 0) - IFNULL(CustomerCallOrderOrderView.RequestDis3Amount, 0) - IFNULL(CustomerCallOrderOrderView.RequestOtherDiscountAmount, 0) + IFNULL(CustomerCallOrderOrderView.RequestAdd1Amount, 0) + IFNULL(CustomerCallOrderOrderView.RequestAdd2Amount, 0) + IFNULL(CustomerCallOrderOrderView.RequestAddOtherAmount, 0) + IFNULL(CustomerCallOrderOrderView.RequestChargeAmount, 0) + IFNULL(CustomerCallOrderOrderView.RequestTaxAmount, 0) ) ELSE 0 END) AS TotalOrderNetAmount,
           CustomerCallOrder.LocalPaperNo AS LocalPaperNo,
           group_concat(CustomerCall.CallType, ':') AS CallType,
           group_concat(CustomerCall.ConfirmStatus, ':') AS ConfirmStatus,
           (EXISTS (
               SELECT 1
                 FROM CustomerCallReturn
                WHERE CustomerCallReturn.CustomerUniqueId = Customer.UniqueId
           )
           ) AS HasReturn,
           ifnull(CustomerCallOrderOrderView.RequestDis1Amount, 0) + ifnull(CustomerCallOrderOrderView.RequestDis2Amount, 0) + ifnull(CustomerCallOrderOrderView.RequestDis3Amount, 0) + ifnull(CustomerCallOrderOrderView.RequestOtherDiscountAmount, 0) AS Discount
      FROM Customer
           JOIN
           CustomerCallOrder ON CustomerCallOrder.CustomerUniqueId = Customer.UniqueId
           JOIN
           CustomerCallOrderOrderView ON CustomerCallOrderOrderView.OrderUniqueId = CustomerCallOrder.UniqueId
           JOIN
           PaymentTypeOrder ON CustomerCallOrder.OrderPaymentTypeUniqueId = PaymentTypeOrder.UniqueId
           LEFT JOIN
           CustomerCall ON CustomerCall.CustomerId = Customer.UniqueId
     GROUP BY Customer.UniqueId,
              CustomerCallOrder.UniqueId;

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
                      sum(CASE WHEN CustomerCallReturnLinesView.IsPromoLine THEN ifnull(CustomerCallReturnLinesView.TotalRequestNetAmount, 0) ELSE ifnull(CustomerCallReturnLinesView.TotalRequestAmount, 0) - ifnull(CustomerCallReturnLinesView.TotalRequestDis1Amount, 0) - ifnull(CustomerCallReturnLinesView.TotalRequestDis2Amount, 0) - ifnull(CustomerCallReturnLinesView.TotalRequestDis3Amount, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestDisOtherAmount, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestAdd1Amount, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestAdd2Amount, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestAddOtherAmount, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestTax, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestCharge, 0) END) AS TotalRequestNetAmount
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
