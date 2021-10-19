-- ----------------------------
--  alter view PaymentReportView related to issue: DMC-64273 & DMC-64250
-- ----------------------------
DROP VIEW IF EXISTS "main"."PaymentReportView";
CREATE VIEW "PaymentReportView" AS
SELECT
	Customer.UniqueId AS CustomerId,
	Customer.CustomerCode AS CustomerCode,
	Customer.CustomerName AS CustomerName,
	a.PaymentId AS PaymentId,
	a.InvoiceId AS InvoiceId,
	a.CashAmount AS CashAmount,
	a.ChequeAmount AS ChequeAmount,
	a.CardAmount AS CardAmount,
	a.SettlementDiscountAmount AS SettlementDiscountAmount,
	a.CreditAmount AS CreditAmount,
	Payment.CheckNumber AS ChqNo,
	Payment.ChqDate AS ChqDate,
	Bank.BankName AS BankName,
	Payment.ChqBranchName AS ChqBranchName,
	Payment.ChqBranchCode AS ChqBranchCode,
	Payment.CheckAccountNumber AS ChqAccountNo,
	Payment.ChqAccountName AS ChqAccountName,
	Payment.Ref AS FollowNo,
	City.CityName AS CityName,
	a.PaidAmount AS PaidAmount,
	CustomerCallInvoice.DealerName AS DealerName,
	payment.PaymentType
FROM
	(
	SELECT
		Payment.UniqueId AS PaymentId,
		Payment.CustomerId,
		InvoicePaymentInfo.InvoiceId,
		CASE
			WHEN Payment.PaymentType = '837689e8-2115-4085-bf7f-0d0da86f3d71' THEN
			InvoicePaymentInfo.Amount ELSE 0
		END AS CashAmount,
		CASE
			WHEN Payment.PaymentType = 'e3a93634-ae20-4d57-8e27-eee7b768adfc' THEN
			InvoicePaymentInfo.Amount ELSE 0
		END AS ChequeAmount,
		CASE
			WHEN Payment.PaymentType = 'f1b06da6-122d-4427-abd0-84a7cf72b29c' THEN
			InvoicePaymentInfo.Amount ELSE 0
		END AS CardAmount,
		CASE
			WHEN Payment.PaymentType = 'df7e99c9-2ed9-436a-b9a3-8ec0f4e86651' THEN
			InvoicePaymentInfo.Amount ELSE 0
		END AS SettlementDiscountAmount,
		CASE
			WHEN Payment.PaymentType = '56c7d3ee-4d18-4c5c-bbbd-aacc6bebd862' THEN
			InvoicePaymentInfo.Amount ELSE 0
		END AS CreditAmount,
		InvoicePaymentInfo.Amount AS PaidAmount
	FROM
		Payment
		INNER JOIN InvoicePaymentInfo ON InvoicePaymentInfo.PaymentId = Payment.UniqueId
	UNION ALL
	SELECT
		Payment.UniqueId AS PaymentId,
		Payment.CustomerId,
		NULL AS InvoiceId,
		CASE
			WHEN Payment.PaymentType = '837689e8-2115-4085-bf7f-0d0da86f3d71' THEN
			( Payment.Amount - IFNULL(sum( InvoicePaymentInfo.Amount ), 0) ) ELSE 0
		END AS CashAmount,
		CASE
			WHEN Payment.PaymentType = 'e3a93634-ae20-4d57-8e27-eee7b768adfc' THEN
			( Payment.Amount - IFNULL(sum( InvoicePaymentInfo.Amount ), 0) ) ELSE 0
		END AS ChequeAmount,
		CASE
			WHEN Payment.PaymentType = 'f1b06da6-122d-4427-abd0-84a7cf72b29c' THEN
			( Payment.Amount - IFNULL(sum( InvoicePaymentInfo.Amount ), 0) ) ELSE 0
		END AS CardAmount,
		CASE
			WHEN Payment.PaymentType = 'df7e99c9-2ed9-436a-b9a3-8ec0f4e86651' THEN
			( Payment.Amount - IFNULL(sum( InvoicePaymentInfo.Amount ), 0) ) ELSE 0
		END AS SettlementDiscountAmount,
		CASE
			WHEN Payment.PaymentType = '56c7d3ee-4d18-4c5c-bbbd-aacc6bebd862' THEN
			( Payment.Amount - IFNULL(sum( InvoicePaymentInfo.Amount ), 0) ) ELSE 0
		END AS CreditAmount,
		( Payment.Amount - IFNULL(sum( InvoicePaymentInfo.Amount ), 0) ) AS PaidAmount
	FROM
		Payment
		LEFT JOIN InvoicePaymentInfo ON InvoicePaymentInfo.PaymentId = Payment.UniqueId
	GROUP BY
		Payment.UniqueId
	) AS a
	INNER JOIN customer ON Customer.UniqueId = a.CustomerId
	LEFT JOIN Payment ON Payment.UniqueId = a.PaymentId
	LEFT JOIN Bank ON Bank.UniqueId = Payment.BankId
	LEFT JOIN City ON City.UniqueId = Payment.CityId
	LEFT JOIN CustomerCallInvoice ON CustomerCallInvoice.UniqueId = a.InvoiceId
WHERE
	PaidAmount >0;
