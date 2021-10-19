-- ----------------------------
-- Alter View structure for OperationReportView .
-- Related to issue DMC-43039
-- We add the records for the customers who just have payment without any order and who don't have payments for their orders // CustomerCall.CallType == 0 OR CustomerCall.CallType=11
-- ----------------------------
DROP VIEW IF EXISTS "OperationReportView";
CREATE VIEW "OperationReportView" AS
SELECT
	Customer.UniqueId AS UniqueId,
	Customer.CustomerName AS CustomerName,
	Customer.CustomerCode AS CustomerCode,
	Customer.StoreName AS StoreName,
	ifnull(SellAmount.TotalAmount,0) as TotalAmount,
	ifnull(SellAmount.OrderDiscountAmount,0) as OrderDiscountAmount,
	ifnull(SellAmount.OrderAddAmount,0) as OrderAddAmount,
	ifnull(SellAmount.TotalNetAmount,0) as TotalNetAmount,
	ifnull(CustomerPayment.Amount,0) as TotalPaidAmount,
	ifnull(CustomerPayment.AmountCheque,0) as AmountCheque,
	ifnull(CustomerPayment.AmountCard,0) as AmountCard,
	ifnull(CustomerPayment.AmountCash,0) as AmountCash,
	ifnull(CustomerPayment.AmountDiscount,0) as AmountDiscount,
	ifnull(CustomerPayment.AmountCredit,0) as AmountCredit,
	ifnull(SellAmount.TotalNetAmount,0) - ifnull(CallReturn.RequestAmount,0) as PayableAmount,
	CASE WHEN (ifnull(SellAmount.TotalNetAmount,0) - ifnull(CallReturn.RequestAmount,0) - ifnull(CustomerPayment.AmountCheque,0) - ifnull(CustomerPayment.AmountCard,0) - ifnull(CustomerPayment.AmountCash,0) - ifnull(CustomerPayment.AmountDiscount,0) - ifnull(CustomerPayment.AmountCredit,0)) > 0 then (ifnull(SellAmount.TotalNetAmount,0) - ifnull(CallReturn.RequestAmount,0) - ifnull(CustomerPayment.AmountCheque,0) - ifnull(CustomerPayment.AmountCard,0) - ifnull(CustomerPayment.AmountCash,0) - ifnull(CustomerPayment.AmountDiscount,0) - ifnull(CustomerPayment.AmountCredit,0)) else 0 end as Recipe,
	ifnull(CallReturn.AddAmount,0) as ReturnAddAmount,
	ifnull(CallReturn.DisAmount,0) as ReturnDiscountAmount,
	ifnull(CallReturn.RequestAmount,0) as ReturnRequestAmount
FROM
	Customer
LEFT JOIN (
	SELECT
		CustomerCallOrderOrderView.CustomerUniqueId,
		SUM(CASE WHEN(CustomerCallOrderOrderView.IsPromoLine != 1) THEN	ifnull(CustomerCallOrderOrderView.RequestAmount,0) ELSE	0	END) AS TotalAmount,
		SUM(ifnull(CustomerCallOrderOrderView.RequestDis1Amount,0) + ifnull(CustomerCallOrderOrderView.RequestDis2Amount,0) + ifnull(CustomerCallOrderOrderView.RequestDis3Amount,0)) AS OrderDiscountAmount,
		SUM(ifnull(CustomerCallOrderOrderView.RequestTaxAmount,0) + ifnull(CustomerCallOrderOrderView.RequestChargeAmount,0) + ifnull(CustomerCallOrderOrderView.RequestAdd1Amount,0) + ifnull(CustomerCallOrderOrderView.RequestAdd2Amount,0)) AS OrderAddAmount,
		SUM((CASE	WHEN (CustomerCallOrderOrderView.IsPromoLine != 1) THEN	ifnull(CustomerCallOrderOrderView.RequestAmount,0) ELSE	0	END) - ifnull(CustomerCallOrderOrderView.RequestDis1Amount,0) - ifnull(CustomerCallOrderOrderView.RequestDis2Amount,0) - ifnull(CustomerCallOrderOrderView.RequestDis3Amount,0) + ifnull(CustomerCallOrderOrderView.RequestAdd1Amount,0) + ifnull(CustomerCallOrderOrderView.RequestAdd2Amount,0) + ifnull(CustomerCallOrderOrderView.RequestTaxAmount,0) + ifnull(CustomerCallOrderOrderView.RequestChargeAmount,0)) AS TotalNetAmount
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
		sum(ifnull(CustomerCallReturnLinesView.TotalRequestAmount,0)) as RequestAmount,
		sum(ifnull(CustomerCallReturnLinesView.TotalRequestAdd1Amount,0) + ifnull(CustomerCallReturnLinesView.TotalRequestAdd2Amount,0) + ifnull(CustomerCallReturnLinesView.TotalRequestTax,0) + ifnull(CustomerCallReturnLinesView.TotalRequestCharge,0)) as AddAmount,
		sum(ifnull(CustomerCallReturnLinesView.TotalRequestDiscount,0))as DisAmount
	FROM
		CustomerCallReturnLinesView
	GROUP BY CustomerCallReturnLinesView.CustomerUniqueId
) CallReturn ON CallReturn.CustomerUniqueId = Customer.UniqueId
JOIN CustomerCall On (CustomerCall.CallType == 0 OR CustomerCall.CallType=11) AND CustomerCall.ConfirmStatus == 1 AND CustomerCall.CustomerId = Customer.UniqueId
GROUP BY CustomerCall.CustomerId;