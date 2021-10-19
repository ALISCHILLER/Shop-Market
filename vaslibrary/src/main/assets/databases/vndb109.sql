-- ----------------------------
--  Alter view RequestReportView related to issue: NGT-2764 and DMC-53808
-- ----------------------------
DROP VIEW IF EXISTS "main"."RequestReportView";
CREATE VIEW "RequestReportView" AS
SELECT
	Customer.UniqueId,
	Customer.CustomerName,
	Customer.CustomerCode,
	Customer.StoreName,
	CustomerCallOrder.UniqueId AS OrderUniqueId,
	PaymentTypeOrder.PaymentTypeOrderName AS PaymentTypeBaseName,
	SUM(
	CASE
			WHEN CustomerCall.ExtraField1 = CustomerCallOrder.UniqueId THEN
			(IFNULL( CustomerCallOrderOrderView.RequestAmount, 0 ) - IFNULL( CustomerCallOrderOrderView.RequestDis1Amount, 0 ) - IFNULL( CustomerCallOrderOrderView.RequestDis2Amount, 0 ) - IFNULL( CustomerCallOrderOrderView.RequestDis3Amount, 0 ) + IFNULL( CustomerCallOrderOrderView.RequestAdd1Amount, 0 ) + IFNULL( CustomerCallOrderOrderView.RequestAdd2Amount, 0 ) + IFNULL( CustomerCallOrderOrderView.RequestChargeAmount, 0 ) + IFNULL( CustomerCallOrderOrderView.RequestTaxAmount, 0 )
			) ELSE 0
		END
		) AS TotalOrderNetAmount,
    CustomerCallOrder.LocalPaperNo AS LocalPaperNo,
	group_concat(CustomerCall.CallType , ':') as CallType,
	group_concat(CustomerCall.ConfirmStatus , ':') as ConfirmStatus,
	(
 EXISTS (select 1 from CustomerCallReturn WHERE CustomerCallReturn.CustomerUniqueId = Customer.UniqueId)
	) AS HasReturn,
ifnull(CustomerCallOrderOrderView.RequestDis1Amount,0) + ifnull(CustomerCallOrderOrderView.RequestDis2Amount,0) + ifnull(CustomerCallOrderOrderView.RequestDis3Amount,0) as Discount
FROM
	Customer
JOIN CustomerCallOrder ON CustomerCallOrder.CustomerUniqueId = Customer.UniqueId
JOIN CustomerCallOrderOrderView ON CustomerCallOrderOrderView.OrderUniqueId = CustomerCallOrder.UniqueId
JOIN PaymentTypeOrder ON CustomerCallOrder.OrderPaymentTypeUniqueId = PaymentTypeOrder.UniqueId
LEFT JOIN CustomerCall ON CustomerCall.CustomerId = Customer.UniqueId
GROUP BY
	Customer.UniqueId, CustomerCallOrder.UniqueId;