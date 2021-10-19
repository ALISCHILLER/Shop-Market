-- ----------------------------
--  Alter view CustomerCallOrderOrderView related to issue: NGT-3427
-- ----------------------------
alter table CustomerEmphaticProduct add column TypeLawUniqueId TEXT COLLATE NOCASE;
alter table CustomerEmphaticProduct add column EmphasisRuleId TEXT COLLATE NOCASE;
alter table EmphaticProduct add column TypeLawUniqueId TEXT COLLATE NOCASE;
alter table EmphaticProduct add column PackageCount INTEGER;
alter table EmphaticProduct add column Title TEXT;

-- ----------------------------
--  Alter view CustomerCallReturn and CustomerCallReturnRequest related to issue: NGT-3419
-- ----------------------------
alter table CustomerCallReturn add column ReplacementRegistration INTEGER;
alter table CustomerCallReturnRequest add column ReplacementRegistration INTEGER;

-- ----------------------------
--  Alter view EmphaticProduct related to issue: DMC-45928
-- ----------------------------
alter table EmphaticProduct add column saleAreaUniqueId TEXT;

-- ----------------------------
--  Alter view customerCallOrderOrderView related to issue: DMC-45928
-- ----------------------------
DROP VIEW IF EXISTS "main"."customerCallOrderOrderView";
CREATE VIEW "customerCallOrderOrderView" AS
SELECT
CustomerCallInvoiceLinesView.TotalQty as OriginalTotalQty,
 a.*,
 CASE
  WHEN a.RequestBulkQtyUnitUniqueId IS NULL THEN
   (a.TotalQty * a.UnitPrice)
  ELSE
  (a.RequestBulkQty * a.UnitPrice)
 END AS RequestAmount,
 FreeReason.FreeReasonName AS FreeReasonName,
 OnHandQty.OnHandQty AS OnHandQty,
 TotalProductOrderQtyView.TotalQty AS ProductTotalOrderedQty,
 ProductBatchView.BatchNo AS BatchNo,
 ProductBatchView.ExpDate AS ExpDate,
 ProductBatchView.OnHandQty AS BatchOnHandQty,
 ProductBatchView.ItemRef AS ItemRef,
 ProductBatchView.BatchRef AS BatchRef,
 OnHandQty.HasAllocation AS HasAllocation,
 CASE
  WHEN a.RequestBulkQtyUnitUniqueId IS NULL THEN
   (a.TotalQty * (productWeight*0.001))
  ELSE
  (a.RequestBulkQty)
 END AS TotalWeight
FROM
	(
		SELECT
			CustomerCallOrderLines.UniqueId AS UniqueId,
			CustomerCallOrderLines.OrderUniqueId AS OrderUniqueId,
			CustomerCallOrder.CustomerUniqueId AS CustomerUniqueId,
			CustomerCallOrderLines.FreeReasonId AS FreeReasonId,
			CustomerCallOrderLines.RequestAdd1Amount AS RequestAdd1Amount,
			CustomerCallOrderLines.RequestAdd2Amount AS RequestAdd2Amount,
			CustomerCallOrderLines.RequestTaxAmount AS RequestTaxAmount,
			CustomerCallOrderLines.RequestChargeAmount AS RequestChargeAmount,
			CustomerCallOrderLines.RequestDis1Amount AS RequestDis1Amount,
			CustomerCallOrderLines.RequestDis2Amount AS RequestDis2Amount,
			CustomerCallOrderLines.RequestDis3Amount AS RequestDis3Amount,
			CustomerCallOrderLines.RequestOtherDiscountAmount AS RequestOtherDiscountAmount,
			CustomerCallOrderLines.InvoiceAmount AS InvoiceAmount,
			CustomerCallOrderLines.InvoiceAdd1Amount AS InvoiceAdd1Amount,
			CustomerCallOrderLines.InvoiceAdd2Amount AS InvoiceAdd2Amount,
			CustomerCallOrderLines.InvoiceTaxAmount AS InvoiceTaxAmount,
			CustomerCallOrderLines.InvoiceChargeAmount AS InvoiceChargeAmount,
			CustomerCallOrderLines.InvoiceOtherDiscountAmount AS InvoiceOtherDiscountAmount,
			CustomerCallOrderLines.InvoiceDis1Amount AS InvoiceDis1Amount,
			CustomerCallOrderLines.InvoiceDis2Amount AS InvoiceDis2Amount,
			CustomerCallOrderLines.InvoiceDis3Amount AS InvoiceDis3Amount,
			CustomerCallOrderLines.RequestBulkQty AS RequestBulkQty,
			Qty * ConvertFactor As TotalQtyBulk,
			CustomerCallOrderLines.SortId AS SortId,
			CustomerCallOrderLines.RequestBulkQtyUnitUniqueId AS RequestBulkQtyUnitUniqueId,
			CustomerCallOrderLines.IsPromoLine AS IsPromoLine,
			CustomerCallOrderLines.PromotionPrice AS PromotionPrice,
			CustomerCallOrderLines.PayDuration AS PayDuration,
			CustomerCallOrderLines.RuleNo AS RuleNo,
			Product.ProductName AS ProductName,
			Product.ProductCode AS ProductCode,
			Product.IsFreeItem AS IsFreeItem,
			Product.OrderPoint AS OrderPoint,
			Product.PayDuration AS ProductPayDuration,
			CustomerPrice.Price AS UnitPrice,
			CustomerPrice.UserPrice AS UserPrice,
			CustomerPrice.PriceId AS PriceId,
			ProductUnit.ProductId AS ProductId,
			group_concat(Qty, ':') AS Qty,
			group_concat(ConvertFactor, ':') AS ConvertFactor,
			group_concat(
				CustomerCallOrderLinesOrderQtyDetail.ProductUnitId,
				':'
			) AS ProductUnitId,
			group_concat(UnitName, ':') AS UnitName,
      CASE
       WHEN CustomerCallOrderLines.RequestBulkQtyUnitUniqueId IS NULL THEN
        sum(Qty * ConvertFactor)
       ELSE
        CustomerCallOrderLines.RequestBulkQty
      END AS TotalQty,
			CustomerCallOrderLines.IsRequestFreeItem,
			CustomerEmphaticProduct.Type AS EmphaticType,
			CustomerEmphaticProduct.ProductCount AS EmphaticProductCount,
			CustomerEmphaticProduct.TypeLawUniqueId AS TypeLawUniqueId,
			CustomerEmphaticProduct.EmphasisRuleId AS EmphasisRuleId,
			CustomerCallOrder.SaleDate AS SaleDate,
			CustomerCallOrder.LocalPaperNo AS LocalPaperNo,
			CustomerCallOrder.OrderPaymentTypeUniqueId AS OrderPaymentTypeUniqueId,
			CustomerCallOrder.OrderTypeUniqueId AS OrderTypeUniqueId,
			IFNULL(Product.productWeight,0) As productWeight
		FROM
			CustomerCallOrderLines,
			ProductUnit ON CustomerCallOrderLinesOrderQtyDetail.ProductUnitId = ProductUnit.UniqueId,
			CustomerCallOrderLinesOrderQtyDetail ON CustomerCallOrderLinesOrderQtyDetail.OrderLineUniqueId = CustomerCallOrderLines.UniqueId,
			Product ON Product.UniqueId = CustomerCallOrderLines.ProductUniqueId,
			Unit ON Unit.UniqueId = ProductUnit.UnitId,
			CustomerCallOrder ON CustomerCallOrder.UniqueId = CustomerCallOrderLines.OrderUniqueId
		LEFT JOIN CustomerEmphaticProduct ON CustomerEmphaticProduct.ProductId = Product.UniqueId
		LEFT JOIN CustomerPrice ON (
			CustomerPrice.CallOrderId = CustomerCallOrder.UniqueId
		)
		AND (
			CustomerPrice.ProductUniqueId = CustomerCallOrderLines.ProductUniqueId
		)
		GROUP BY
			OrderLineUniqueId
	) AS a
LEFT JOIN FreeReason ON FreeReason.UniqueId = a.FreeReasonId
LEFT JOIN OnHandQty ON OnHandQty.ProductId = a.ProductId
LEFT JOIN TotalProductOrderQtyView ON TotalProductOrderQtyView.ProductId = a.ProductId
LEFT JOIN ProductBatchView ON a.ProductId = ProductBatchView.ProductId
LEFT JOIN CustomerCallInvoiceLinesView ON CustomerCallInvoiceLinesView.UniqueId = a.UniqueId;


DROP VIEW IF EXISTS "main"."CustomerEmphaticPackageView";
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
AND ((EmphaticProduct.CustomerLevelId IS NULL) OR (EmphaticProduct.CustomerLevelId = IFNULL(Customer.CustomerLevelId,NULL)));


-- ----------------------------
DROP VIEW IF EXISTS "main"."CustomerEmphaticProductView";
CREATE VIEW "CustomerEmphaticProductView" AS
SELECT
	EmphaticProduct.UniqueId AS RuleId,
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
	-- ((EmphaticProduct.SaleZoneId is null) or (EmphaticProduct.SaleZoneId = IFNULL(Customer.SaleZoneNo,0)))
	-- AND ((EmphaticProduct.DCId is null) or (EmphaticProduct.DCId = IFNULL(Customer.DCRef ,0)))
	(
		(
			EmphaticProduct.StateId IS NULL
		)
		OR (
			EmphaticProduct.StateId = IFNULL(Customer.StateId, NULL)
		)
	)
AND (
	(
		EmphaticProduct.CityId IS NULL
	)
	OR (
		EmphaticProduct.CityId = IFNULL(Customer.CityId, NULL)
	)
)
AND (
	(
		EmphaticProduct.CustomerActivityId IS NULL
	)
	OR (
		EmphaticProduct.CustomerActivityId = IFNULL(
			Customer.CustomerActivityId,
			NULL
		)
	)
)
AND (
	(
		EmphaticProduct.CustomerCategoryId IS NULL
	)
	OR (
		EmphaticProduct.CustomerCategoryId = IFNULL(
			Customer.CustomerCategoryId,
			NULL
		)
	)
)
AND (
	(
		EmphaticProduct.CustomerLevelId IS NULL
	)
	OR (
		EmphaticProduct.CustomerLevelId = IFNULL(
			Customer.CustomerLevelId,
			NULL
		)
	)
) AND ((EmphaticProduct.SaleZoneId is null) or (EmphaticProduct.SaleZoneId = IFNULL(Customer.ZoneId,0)))
	 AND ((EmphaticProduct.saleAreaUniqueId is null) or (EmphaticProduct.saleAreaUniqueId = IFNULL(Customer.AreaId ,0)));