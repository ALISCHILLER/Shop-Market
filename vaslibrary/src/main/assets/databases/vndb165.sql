-- ----------------------------
--  Alter Table Customer Add Description Field
-- ----------------------------
Alter table Customer Add COLUMN Description Text;

-- ----------------------------
--  Alter Table CustomerCallInvoice and CustomerCallOrder Add PinCode3 and PinCode4 Field
-- ----------------------------
ALTER TABLE CustomerCallInvoice ADD COLUMN [PinCode3]  TEXT  "";
ALTER TABLE CustomerCallInvoice ADD COLUMN [PinCode4]  TEXT  "";

ALTER TABLE CustomerCallOrder ADD COLUMN [PinCode3]  TEXT  "";
ALTER TABLE CustomerCallOrder ADD COLUMN [PinCode4]  TEXT  "";


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
		group_concat( CustomerCallOrderPreview.Comment, ',' ) AS Comments,
		group_concat( CustomerCallInvoicePreview.Comment, ',' ) AS InvoiceComments
	FROM
		Customer
		LEFT JOIN CustomerCallOrderPreview ON CustomerCallOrderPreview.CustomerUniqueId = Customer.UniqueId
		LEFT JOIN CustomerCallInvoicePreview ON CustomerCallInvoicePreview.CustomerUniqueId = Customer.UniqueId
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


-- ----------------------------
--  Alter Table CustomerCallInvoice and CustomerCallOrder Add [TotalAmountNutCash] and [TotalAmountNutCheque] Field
-- ----------------------------

ALTER TABLE CustomerCallInvoice ADD COLUMN [TotalAmountNutCash]  REAL  NULL;
ALTER TABLE CustomerCallInvoice ADD COLUMN [TotalAmountNutCheque]  REAL  NULL;


ALTER TABLE CustomerCallOrder ADD COLUMN [TotalAmountNutCash]  REAL  NULL;
ALTER TABLE CustomerCallOrder ADD COLUMN [TotalAmountNutCheque]  REAL  NULL;







