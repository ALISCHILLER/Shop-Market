-- ----------------------------
--  Alter view CustomerPathView for SAP
-- ----------------------------
DROP VIEW IF EXISTS "CustomerPathView";
CREATE VIEW "CustomerPathView" AS
SELECT
	c.*,
	VisitTemplatePathCustomer.PathRowId,
	VisitTemplatePathCustomer.VisitTemplatePathId AS VisitTemplatePathId,
	group_concat( CustomerCall.CallType ) AS CallType,
	CustomerCategory.CustomerCategoryName AS CustomerCategoryName
FROM
	(
	SELECT
		Customer.*,
		SUM( CustomerCallOrderPreview.TotalPrice ) AS TotalOrderAmount,
		group_concat( CustomerCallOrderPreview.Comment, ',' ) AS Comments
	FROM
		Customer
		LEFT JOIN CustomerCallOrderPreview ON CustomerCallOrderPreview.CustomerUniqueId = Customer.UniqueId
	GROUP BY
		Customer.UniqueId
	) c
	JOIN VisitTemplatePathCustomer ON c.UniqueId = VisitTemplatePathCustomer.CustomerId
	LEFT JOIN CustomerCall ON CustomerCall.ConfirmStatus == 1
	AND CustomerCall.CustomerId = c.UniqueId
	LEFT JOIN CustomerCategory ON c.CustomerCategoryRef = CustomerCategory.BackOfficeId
GROUP BY
	c.UniqueId,
	CustomerCall.CustomerId,
	VisitTemplatePathId;