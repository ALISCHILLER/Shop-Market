-- ----------------------------
--  Alter ContractPriceVnLite related to issue:DMC-58418
-- ----------------------------
ALTER TABLE ContractPriceVnLite ADD COLUMN BackOfficeNumberId INTEGER;

-- ----------------------------
--  Alter PictureCustomer related to issue: NGT-3937
-- ----------------------------
ALTER TABLE PictureCustomer ADD COLUMN FileId TEXT COLLATE NOCASE;

-- ----------------------------
--  Alter QuestionnaireLineOption related to issue: NGT-4067
-- ----------------------------
ALTER TABLE QuestionnaireLineOption ADD COLUMN AnswerAttachmentUniqueId TEXT COLLATE NOCASE;

-- ----------------------------
--  Alter view CustomerPathView related to issue: NGT-4074
-- ----------------------------
DROP VIEW IF EXISTS "CustomerPathView";
CREATE VIEW "CustomerPathView" AS
SELECT c.*,
       VisitTemplatePathCustomer.PathRowId,
       VisitTemplatePathCustomer.VisitTemplatePathId AS VisitTemplatePathId,
       group_concat(CustomerCall.CallType) AS CallType,
       CustomerCategory.CustomerCategoryName AS CustomerCategoryName
  FROM (
           SELECT Customer.*,
                  SUM(CustomerCallOrderPreview.TotalPrice) AS TotalOrderAmount
             FROM Customer
                  LEFT JOIN
                  CustomerCallOrderPreview ON CustomerCallOrderPreview.CustomerUniqueId = Customer.UniqueId
            GROUP BY Customer.UniqueId
       )
       c
       JOIN
       VisitTemplatePathCustomer ON c.UniqueId = VisitTemplatePathCustomer.CustomerId
       LEFT JOIN
       CustomerCall ON CustomerCall.ConfirmStatus == 1 AND
                       CustomerCall.CustomerId = c.UniqueId
       LEFT JOIN
       CustomerCategory ON c.CustomerCategoryRef = CustomerCategory.BackOfficeId
 GROUP BY c.UniqueId,
          CustomerCall.CustomerId,
          VisitTemplatePathId;
