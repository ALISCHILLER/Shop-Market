-- ----------------------------
--  Alter view CustomerPathView related to issue: NGT-2663
-- ----------------------------
DROP VIEW IF EXISTS "main"."CustomerPathView";
CREATE VIEW "CustomerPathView" AS
SELECT
	c.*, VisitTemplatePathCustomer.PathRowId, VisitTemplatePathCustomer.VisitTemplatePathId AS VisitTemplatePathId,
group_concat(CustomerCall.CallType) as CallType
FROM
	(
		SELECT
			Customer.*, SUM(
				CustomerCallOrderPreview.TotalPrice
			) AS TotalOrderAmount
		FROM
			Customer
		LEFT JOIN CustomerCallOrderPreview ON CustomerCallOrderPreview.CustomerUniqueId = Customer.UniqueId
		GROUP BY
			Customer.UniqueId
	) c
JOIN VisitTemplatePathCustomer ON c.UniqueId = VisitTemplatePathCustomer.CustomerId
LEFT JOIN CustomerCall ON CustomerCall.ConfirmStatus == 1 AND CustomerCall.CustomerId = c.UniqueId
GROUP BY c.UniqueId , CustomerCall.CustomerId;