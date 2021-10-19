-- ----------------------------
-- alter RequestReportView
-- ----------------------------
DROP VIEW IF EXISTS RequestReportView;
CREATE VIEW RequestReportView AS
SELECT
	Customer.UniqueId,
	Customer.CustomerName,
	Customer.CustomerCode,
	Customer.StoreName,
	CustomerCallOrder.UniqueId AS OrderUniqueId,
	PaymentTypeOrder.PaymentTypeOrderName AS PaymentTypeBaseName,
	SUM(case when CustomerCall.CallType = 0 then CustomerCallOrderOrderView.RequestAmount else 0 end) AS TotalOrderNetAmount,
    CustomerCallOrder.LocalPaperNo AS LocalPaperNo,
	group_concat(CustomerCall.CallType , ':') as CallType,
	group_concat(CustomerCall.ConfirmStatus , ':') as ConfirmStatus,
	(
		NOT CustomerCallReturn.UniqueId IS NULL
	) AS HasReturn,
ifnull(CustomerCallOrderOrderView.RequestDis1Amount,0) + ifnull(CustomerCallOrderOrderView.RequestDis2Amount,0) + ifnull(CustomerCallOrderOrderView.RequestDis3Amount,0) as Discount
FROM
	Customer
JOIN CustomerCallOrder ON CustomerCallOrder.CustomerUniqueId = Customer.UniqueId
JOIN CustomerCallOrderOrderView ON CustomerCallOrderOrderView.OrderUniqueId = CustomerCallOrder.UniqueId
JOIN PaymentTypeOrder ON CustomerCallOrder.OrderPaymentTypeUniqueId = PaymentTypeOrder.UniqueId
LEFT JOIN CustomerCallReturn ON CustomerCallReturn.CustomerUniqueId = Customer.UniqueId
LEFT JOIN CustomerCall ON CustomerCall.CustomerId = Customer.UniqueId
GROUP BY
	Customer.UniqueId, CustomerCallOrder.UniqueId;
