-- ----------------------------
-- View structure for OperationReportView (OperationReport -> OperationReportView)
-- ----------------------------
DROP VIEW IF EXISTS "main"."OperationReport";
CREATE VIEW "OperationReportView" AS
SELECT
	Customer.UniqueId AS UniqueId,
	Customer.CustomerName AS CustomerName,
	Customer.CustomerCode AS CustomerCode,
	Customer.StoreName AS StoreName,
	SellAmount.TotalAmount,
	SellAmount.OrderDiscountAmount,
	SellAmount.OrderAddAmount,
	SellAmount.TotalNetAmount,
	CustomerPayment.Amount as TotalPaidAmount,
	CustomerPayment.AmountCheque as AmountCheque,
	CustomerPayment.AmountCard as AmountCard,
	CustomerPayment.AmountCash as AmountCash,
	CustomerPayment.AmountDiscount as AmountDiscount,
	CustomerPayment.AmountCredit as AmountCredit,
	CASE WHEN (SellAmount.TotalNetAmount - CustomerPayment.AmountCheque - CustomerPayment.AmountCard - CustomerPayment.AmountCash - CustomerPayment.AmountDiscount - CustomerPayment.AmountCredit) > 0 then (SellAmount.TotalNetAmount - CustomerPayment.AmountCheque - CustomerPayment.AmountCard - CustomerPayment.AmountCash - CustomerPayment.AmountDiscount - CustomerPayment.AmountCredit) else 0 end as Recipe,
	CallReturn.AddAmount as ReturnAddAmount,
	CallReturn.DisAmount as ReturnDiscountAmount
FROM
	Customer
LEFT JOIN (
	SELECT
		CustomerCallOrderOrderView.CustomerUniqueId,
		SUM(CASE WHEN(CustomerCallOrderOrderView.IsPromoLine != 1) THEN	ifnull(CustomerCallOrderOrderView.RequestAmount,0) ELSE	0	END) AS TotalAmount,
		SUM(ifnull(CustomerCallOrderOrderView.RequestDis1Amount,0) + ifnull(CustomerCallOrderOrderView.RequestDis2Amount,0) + ifnull(CustomerCallOrderOrderView.RequestDis3Amount,0)) AS OrderDiscountAmount,
		SUM(ifnull(CustomerCallOrderOrderView.RequestAdd1Amount,0) + ifnull(CustomerCallOrderOrderView.RequestAdd2Amount,0)) AS OrderAddAmount,
		SUM((CASE	WHEN (CustomerCallOrderOrderView.IsPromoLine != 1) THEN	ifnull(CustomerCallOrderOrderView.RequestAmount,0) ELSE	0	END) - ifnull(CustomerCallOrderOrderView.RequestDis1Amount,0) - ifnull(CustomerCallOrderOrderView.RequestDis2Amount,0) - ifnull(CustomerCallOrderOrderView.RequestDis3Amount,0) + ifnull(CustomerCallOrderOrderView.RequestAdd1Amount,0) + ifnull(CustomerCallOrderOrderView.RequestAdd2Amount,0)) AS TotalNetAmount
	FROM
		CustomerCallOrderOrderView
	GROUP BY
		CustomerCallOrderOrderView.CustomerUniqueId
) AS SellAmount ON SellAmount.CustomerUniqueId = Customer.UniqueId
LEFT JOIN (
	SELECT
		CustomerId,
		SUM(Amount) as Amount,
		SUM(case when PaymentType = 'e3a93634-ae20-4d57-8e27-eee7b768adfc' then Amount else 0 end) as AmountCheque,
		SUM(case when PaymentType = 'f1b06da6-122d-4427-abd0-84a7cf72b29c' then Amount else 0 end) as AmountCard,
		SUM(case when PaymentType = '837689e8-2115-4085-bf7f-0d0da86f3d71' then Amount else 0 end) as AmountCash,
		SUM(case when PaymentType = 'df7e99c9-2ed9-436a-b9a3-8ec0f4e86651' then Amount else 0 end) as AmountDiscount,
		SUM(case when PaymentType = '56c7d3ee-4d18-4c5c-bbbd-aacc6bebd862' then Amount else 0 end) as AmountCredit
	FROM
		Payment
	GROUP BY Payment.CustomerId
) CustomerPayment ON CustomerPayment.CustomerId = Customer.UniqueId
LEFT JOIN (
	SELECT
		CustomerCallReturnLinesView.CustomerUniqueId,
		ifnull(CustomerCallReturnLinesView.TotalRequestAdd1Amount,0) + ifnull(CustomerCallReturnLinesView.TotalRequestAdd2Amount,0) as AddAmount,
		ifnull(CustomerCallReturnLinesView.TotalReturnDis1,0) + ifnull(CustomerCallReturnLinesView.TotalReturnDis2,0) + ifnull(CustomerCallReturnLinesView.TotalReturnDis3,0) as DisAmount
	FROM
		CustomerCallReturnLinesView
	GROUP BY CustomerCallReturnLinesView.CustomerUniqueId
) CallReturn ON CallReturn.CustomerUniqueId = Customer.UniqueId;