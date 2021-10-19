-- ----------------------------
-- alter view structure for OldInvoiceHeaderView related to issue: DMC-64143
-- ----------------------------
DROP VIEW IF EXISTS "main"."OldInvoiceHeaderView";
CREATE VIEW "OldInvoiceHeaderView" AS
SELECT
	CustomerOldInvoiceHeader.UniqueId AS UniqueId,
	Customer.UniqueId AS CustomerUniqueId,
	CustomerOldInvoiceHeader.TotalAmount AS Amount,
	CustomerOldInvoiceHeader.PayAmount AS PayAmount,
	(
		CustomerOldInvoiceHeader.Dis1Amount + CustomerOldInvoiceHeader.Dis2Amount
	) + CustomerOldInvoiceHeader.Dis3Amount AS DiscountAmount,
	CustomerOldInvoiceHeader.TaxAmount AS TaxAmount,
	CustomerOldInvoiceHeader.CustomerId AS CustomerId,
	Customer.Address AS Address,
	Customer.CustomerName AS CustomerName,
	Customer.CustomerCode AS CustomerCode,
	CustomerOldInvoiceHeader.SaleNo AS InvoiceNo,
	CustomerOldInvoiceHeader.SaleRef AS InvoiceRef,
	CustomerOldInvoiceHeader.SalePDate AS InvoiceDate,
	CustomerOldInvoiceHeader.DealerId AS DealerId,
	CustomerOldInvoiceHeader.PaymentTypeOrderUniqueId as PaymentTypeOrderUniqueId,
	CAST (
		CASE
		WHEN CustomerInvoicePayment.UniqueId IS NULL THEN
			0
		ELSE
			1
		END AS INT
	) AS HasPayment,
	CAST (
		CustomerOldInvoiceHeader.TotalAmount - CustomerOldInvoiceHeader.PayAmount AS REAL
	) AS RemAmount
FROM
	CustomerOldInvoiceHeader,
	Customer ON CustomerOldInvoiceHeader.CustomerId = Customer.UniqueId
LEFT JOIN CustomerInvoicePayment ON CustomerInvoicePayment.InvoiceId = CustomerOldInvoiceHeader.UniqueId;

-- ----------------------------
-- create view structure for OldInvoiceHeaderTempView related to issue: DMC-64143
-- ----------------------------
DROP VIEW IF EXISTS "main"."OldInvoiceHeaderTempView";
CREATE VIEW "OldInvoiceHeaderTempView" AS
SELECT
	CustomerOldInvoiceHeader.UniqueId AS UniqueId,
	Customer.UniqueId AS CustomerUniqueId,
	CustomerOldInvoiceHeader.TotalAmount AS Amount,
	CustomerOldInvoiceHeader.PayAmount AS PayAmount,
	(
		CustomerOldInvoiceHeader.Dis1Amount + CustomerOldInvoiceHeader.Dis2Amount
	) + CustomerOldInvoiceHeader.Dis3Amount AS DiscountAmount,
	CustomerOldInvoiceHeader.TaxAmount AS TaxAmount,
	CustomerOldInvoiceHeader.CustomerId AS CustomerId,
	Customer.Address AS Address,
	Customer.CustomerName AS CustomerName,
	Customer.CustomerCode AS CustomerCode,
	CustomerOldInvoiceHeader.SaleNo AS InvoiceNo,
	CustomerOldInvoiceHeader.SaleRef AS InvoiceRef,
	CustomerOldInvoiceHeader.SalePDate AS InvoiceDate,
	CustomerOldInvoiceHeader.DealerId AS DealerId,
	CustomerOldInvoiceHeader.PaymentTypeOrderUniqueId as PaymentTypeOrderUniqueId,
	CAST (
		CASE
		WHEN CustomerInvoicePayment.UniqueId IS NULL THEN
			0
		ELSE
			1
		END AS INT
	) AS HasPayment,
	CAST (
		CustomerOldInvoiceHeader.TotalAmount - CustomerOldInvoiceHeader.PayAmount AS REAL
	) AS RemAmount
FROM
	CustomerOldInvoiceHeader,
	Customer ON CustomerOldInvoiceHeader.CustomerId = Customer.UniqueId
LEFT JOIN CustomerInvoicePayment ON CustomerInvoicePayment.InvoiceId = CustomerOldInvoiceHeader.UniqueId
WHERE (not exists (SELECT 1 FROM CustomerOldInvoiceHeaderTemp))
UNION
SELECT
	CustomerOldInvoiceHeaderTemp.UniqueId AS UniqueId,
	Customer.UniqueId AS CustomerUniqueId,
	CustomerOldInvoiceHeaderTemp.TotalAmount AS Amount,
	CustomerOldInvoiceHeaderTemp.PayAmount AS PayAmount,
	(
		CustomerOldInvoiceHeaderTemp.Dis1Amount + CustomerOldInvoiceHeaderTemp.Dis2Amount
	) + CustomerOldInvoiceHeaderTemp.Dis3Amount AS DiscountAmount,
	CustomerOldInvoiceHeaderTemp.TaxAmount AS TaxAmount,
	CustomerOldInvoiceHeaderTemp.CustomerId AS CustomerId,
	Customer.Address AS Address,
	Customer.CustomerName AS CustomerName,
	Customer.CustomerCode AS CustomerCode,
	CustomerOldInvoiceHeaderTemp.SaleNo AS InvoiceNo,
	CustomerOldInvoiceHeaderTemp.SaleRef AS InvoiceRef,
	CustomerOldInvoiceHeaderTemp.SalePDate AS InvoiceDate,
	CustomerOldInvoiceHeaderTemp.DealerId AS DealerId,
	CustomerOldInvoiceHeaderTemp.PaymentTypeOrderUniqueId as PaymentTypeOrderUniqueId,
	CAST (
		CASE
		WHEN CustomerInvoicePayment.UniqueId IS NULL THEN
			0
		ELSE
			1
		END AS INT
	) AS HasPayment,
	CAST (
		CustomerOldInvoiceHeaderTemp.TotalAmount - CustomerOldInvoiceHeaderTemp.PayAmount AS REAL
	) AS RemAmount
FROM
	CustomerOldInvoiceHeaderTemp,
	Customer ON CustomerOldInvoiceHeaderTemp.CustomerId = Customer.UniqueId
LEFT JOIN CustomerInvoicePayment ON CustomerInvoicePayment.InvoiceId = CustomerOldInvoiceHeaderTemp.UniqueId;