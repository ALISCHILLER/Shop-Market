alter table City add column StateUniqueId INTEGER;

-- ----------------------------
--  Alter table Product related to issue: NGT-1922
-- ----------------------------
alter table Product add column IsActive INTEGER;
alter table Product add column productWeight REAL;

-- ----------------------------
--  Alter table DiscountSDS related to issue: NGT-3204
-- ----------------------------
alter table DiscountSDS add column DiscountPreventSaveOrder INTEGER;
alter table DiscountSDS add column DiscountPreventSaveSale INTEGER;
alter table DiscountSDS add column DiscountPreventSaveFollowVoucher INTEGER;
alter table DiscountSDS add column DiscountPreventSaveTablet INTEGER;
alter table DiscountSDS add column DiscountPreventMessage TEXT;
alter table DiscountSDS add column DiscountTypeRef INTEGER;
alter table DiscountSDS add column PreventSaleResultDesc TEXT;

-- ----------------------------
--  Alter table ProductGroup related to issue: DMC-47411
-- ----------------------------
alter table ProductGroup add column OrderOf INTEGER;

DROP VIEW IF EXISTS "main"."ProductGroupCatalogView";
CREATE VIEW "ProductGroupCatalogView" AS
SELECT ProductGroup.UniqueId , ProductGroup.ProductGroupParentId ,
ProductGroup.ProductGroupName, ProductGroup.OrderOf, ProductGroup.LastUpdate ,
ProductGroupCatalog.RowIndex FROM
ProductGroup
INNER JOIN ProductGroupCatalog ON ProductGroupCatalog.ProductMainGroupId = ProductGroup.UniqueId
INNER JOIN ProductGroup AS SubProductGroup ON ProductGroup.UniqueId = SubProductGroup.ProductGroupParentId
INNER JOIN Product ON Product.ProductGroupId = SubProductGroup.UniqueId AND Product.IsForSale = 1
GROUP BY	ProductGroup.UniqueId;

