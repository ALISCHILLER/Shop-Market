DROP VIEW IF EXISTS "main"."CustomerEmphaticProductView";
CREATE VIEW "CustomerEmphaticProductView" AS
SELECT
	EmphaticProduct.UniqueId AS RuleId,
	EmphaticProduct.TypeLawUniqueId AS TypeLawUniqueId,
	EmphaticProduct.EmphasisProductErrorTypeId AS TypeId,
	EmphaticProductCount.ProductId AS ProductId,
	EmphaticProductCount.ProductCount AS ProductCount,
	EmphaticProduct.FromDate AS FromDate,
	EmphaticProduct.ToDate AS ToDate,
	((strftime('%s','now')- (EmphaticProduct.WarningDay * 86400.0))*1000) AS WarningDate,
	((strftime('%s','now')- (EmphaticProduct.DangerDay * 86400.0))*1000) AS DangerDate,
	Customer.UniqueId AS CustomerId
FROM
	EmphaticProduct
INNER JOIN EmphaticProductCount ON EmphaticProduct.UniqueId == EmphaticProductCount.RuleId
LEFT JOIN Customer
WHERE
((EmphaticProduct.StateId IS NULL) OR (EmphaticProduct.StateId = IFNULL(Customer.StateId, NULL)))
AND ((EmphaticProduct.CityId IS NULL) OR (EmphaticProduct.CityId = IFNULL(Customer.CityId, NULL)))
AND ((EmphaticProduct.CustomerActivityId IS NULL) OR (EmphaticProduct.CustomerActivityId = IFNULL(Customer.CustomerActivityId,NULL)))
AND ((EmphaticProduct.CustomerCategoryId IS NULL) OR (EmphaticProduct.CustomerCategoryId = IFNULL(Customer.CustomerCategoryId,NULL)))
AND ((EmphaticProduct.CustomerLevelId IS NULL) OR (EmphaticProduct.CustomerLevelId = IFNULL( Customer.CustomerLevelId, NULL)))
AND ((EmphaticProduct.SaleZoneId is null) or (EmphaticProduct.SaleZoneId = IFNULL(Customer.ZoneId,0)))
AND ((EmphaticProduct.saleAreaUniqueId is null) or (EmphaticProduct.saleAreaUniqueId = IFNULL(Customer.AreaId ,0)));

alter table CustomerEmphaticProduct add column TypeLawUniqueId TEXT COLLATE NOCASE;

CREATE VIEW "CustomerEmphaticPackageView" AS
SELECT
	EmphaticProduct.UniqueId AS RuleId,
	EmphaticProduct.PackageCount AS PackageCount,
	EmphaticProduct.EmphasisProductErrorTypeId AS TypeId,
	EmphaticProduct.Title AS Title,
	EmphaticProduct.FromDate AS FromDate,
	EmphaticProduct.ToDate AS ToDate,
	((strftime('%s','now')- (EmphaticProduct.WarningDay * 86400))*1000) AS WarningDate,
	((strftime('%s','now')- (EmphaticProduct.DangerDay * 86400))*1000) AS DangerDate,
	Customer.UniqueId AS CustomerId
FROM
	EmphaticProduct
LEFT JOIN Customer
WHERE
EmphaticProduct.TypeLawUniqueId == '7264025D-4001-48ED-A709-C290AB8F1E9C' AND
((EmphaticProduct.StateId IS NULL) OR ( EmphaticProduct.StateId = IFNULL(Customer.StateId, NULL)))
AND ((EmphaticProduct.CityId IS NULL) OR (EmphaticProduct.CityId = IFNULL(Customer.CityId, NULL)))
AND ((EmphaticProduct.CustomerActivityId IS NULL) OR (EmphaticProduct.CustomerActivityId = IFNULL(Customer.CustomerActivityId,NULL)))
AND ((EmphaticProduct.CustomerCategoryId IS NULL) OR (EmphaticProduct.CustomerCategoryId = IFNULL(Customer.CustomerCategoryId,NULL)))
AND ((EmphaticProduct.CustomerLevelId IS NULL) OR (EmphaticProduct.CustomerLevelId = IFNULL(Customer.CustomerLevelId,NULL)))
AND ((EmphaticProduct.SaleZoneId is null) or (EmphaticProduct.SaleZoneId = IFNULL(Customer.ZoneId,0)))
AND ((EmphaticProduct.saleAreaUniqueId is null) or (EmphaticProduct.saleAreaUniqueId = IFNULL(Customer.AreaId ,0)));