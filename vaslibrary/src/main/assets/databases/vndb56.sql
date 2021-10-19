-- ----------------------------
--  Alter view ProductGroupCatalogView. Related to issue: DMC-36007
-- ----------------------------
DROP VIEW IF EXISTS "main"."ProductGroupCatalogView";
CREATE VIEW "ProductGroupCatalogView" AS
SELECT ProductGroup.UniqueId , ProductGroup.ProductGroupParentId , ProductGroup.ProductGroupName , ProductGroup.LastUpdate , ProductGroupCatalog.RowIndex FROM
ProductGroup
INNER JOIN ProductGroupCatalog ON ProductGroupCatalog.ProductMainGroupId = ProductGroup.UniqueId
INNER JOIN ProductGroup AS SubProductGroup ON ProductGroup.UniqueId = SubProductGroup.ProductGroupParentId
INNER JOIN Product ON Product.ProductGroupId = SubProductGroup.UniqueId AND Product.IsForSale = 1
GROUP BY	ProductGroup.UniqueId;