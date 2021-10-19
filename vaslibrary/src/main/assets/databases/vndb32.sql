-- ----------------------------
-- Table structure for OnHandQty
-- ----------------------------
DROP TABLE IF EXISTS "main"."OnHandQty";
CREATE TABLE "OnHandQty" (
"UniqueId"  TEXT ,
"ProductId"  TEXT,
"OnHandQty"  REAL,
"StockId"  TEXT,
"RenewQty"  REAL,
"IsBatch"  INTEGER,
PRIMARY KEY ("UniqueId"),
CONSTRAINT "productId" UNIQUE ("ProductId")
);

-- ----------------------------
-- Table structure for ProductBatchOnHandQty
-- ----------------------------
DROP TABLE IF EXISTS "main"."ProductBatchOnHandQty";
CREATE TABLE "ProductBatchOnHandQty" (
"UniqueId"  TEXT,
"ProductId"  TEXT,
"BatchRef"  INTEGER,
"BatchNo"  TEXT,
"ExpDate"  TEXT,
"OnHandQty" REAL,
"InsDate"  TEXT,
"ProDate"  TEXT,
"ItemRef" INTEGER,
"DateTimeExpDate" DateTimeExpDate,
PRIMARY KEY ("UniqueId"),
CONSTRAINT "product" FOREIGN KEY ("ProductId") REFERENCES "OnHandQty" ("ProductId") ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- add view VisitDayView
-- ----------------------------
DROP VIEW IF EXISTS VisitDayView;
CREATE VIEW VisitDayView AS
SELECT VisitDay.* , count(*) as CustomerCount FROM VisitDay LEFT JOIN VisitTemplatePathCustomer
ON VisitDay.UniqueId = VisitTemplatePathCustomer.VisitTemplatePathId
GROUP BY VisitDay.UniqueId;

-- ----------------------------
-- alter table Location
-- ----------------------------
ALTER TABLE Location ADD COLUMN CustomerId TEXT NULL;

-- ----------------------------
-- View structure for ProductBatchView
-- ----------------------------
DROP VIEW IF EXISTS "main"."ProductBatchView";
CREATE VIEW "ProductBatchView" AS
SELECT
ProductBatchOnHandQty.ProductId AS ProductId,
group_concat(BatchNo, ':') AS BatchNo,
group_concat(ExpDate, ':') AS ExpDate,
group_concat(OnHandQty, ':') AS OnHandQty,
group_concat(BatchRef, ':') AS BatchRef,
group_concat(ItemRef, ':') AS ItemRef
from ProductBatchOnHandQty
JOIN Product ON Product.UniqueId = ProductBatchOnHandQty.ProductId
GROUP BY ProductBatchOnHandQty.ProductId;

-- ----------------------------
-- Table structure for CallOrderLineBatchQtyDetail
-- ----------------------------
DROP TABLE IF EXISTS "main"."CallOrderLineBatchQtyDetail";
CREATE TABLE "CallOrderLineBatchQtyDetail" (
"UniqueId"  TEXT NOT NULL,
"BatchRef"  INTEGER,
"ItemRef"  INTEGER,
"BatchNo"  TEXT,
"Qty"  REAL,
"CustomerCallOrderLineUniqueId"  TEXT,
PRIMARY KEY ("UniqueId" ASC),
CONSTRAINT "calllines" FOREIGN KEY ("CustomerCallOrderLineUniqueId") REFERENCES "CustomerCallOrderLines" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE
);


-- ----------------------------
-- View structure for CustomerCallOrderOrderView
-- ----------------------------
DROP VIEW IF EXISTS "main"."CustomerCallOrderOrderView";
CREATE VIEW "CustomerCallOrderOrderView" AS
SELECT
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
 ProductBatchView.BatchRef AS BatchRef
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
			CustomerCallOrderLines.SortId AS SortId,
			CustomerCallOrderLines.RequestBulkQtyUnitUniqueId AS RequestBulkQtyUnitUniqueId,
			CustomerCallOrderLines.IsPromoLine AS IsPromoLine,
			CustomerCallOrderLines.PromotionPrice AS PromotionPrice,
			Product.ProductName AS ProductName,
			Product.ProductCode AS ProductCode,
			Product.IsFreeItem AS IsFreeItem,
			Product.OrderPoint AS OrderPoint,
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
			CustomerCallOrder.SaleDate AS SaleDate
		FROM
			CustomerCallOrderLines,
			ProductUnit ON CustomerCallOrderLinesOrderQtyDetail.ProductUnitId = ProductUnit.UniqueId,
			CustomerCallOrderLinesOrderQtyDetail ON CustomerCallOrderLinesOrderQtyDetail.OrderLineUniqueId = CustomerCallOrderLines.UniqueId,
			Product ON Product.UniqueId = CustomerCallOrderLines.ProductUniqueId,
			Unit ON Unit.UniqueId = ProductUnit.UnitId,
			CustomerCallOrder ON CustomerCallOrder.UniqueId = CustomerCallOrderLines.OrderUniqueId
		LEFT JOIN CustomerEmphaticProduct ON CustomerEmphaticProduct.ProductId = Product.UniqueId
		LEFT JOIN CustomerPrice ON (
			CustomerPrice.CustomerUniqueId = CustomerCallOrder.CustomerUniqueId
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
LEFT JOIN ProductBatchView ON a.ProductId = ProductBatchView.ProductId;

-- ----------------------------
-- Table structure for CustomerBatchPrice
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerBatchPrice";
CREATE TABLE "CustomerBatchPrice" (
"UniqueId"  TEXT NOT NULL,
"CustomerUniqueId"  TEXT,
"ProductUniqueId"  TEXT,
"BatchRef"  INTEGER,
"UserPrice"  REAL,
"PriceId"  INTEGER,
"Price"  REAL,
PRIMARY KEY ("UniqueId")
);

-- ----------------------------
-- View structure for WarehouseProductQtyView
-- ----------------------------
DROP VIEW IF EXISTS "main"."WarehouseProductQtyView";
CREATE VIEW "WarehouseProductQtyView" AS
SELECT
Product.UniqueId AS UniqueId,
Product.ProductCode,
Product.ProductName,
Product.ProductTypeId,
ProductUnitsView.UnitName AS UnitName,
ProductUnitsView.ConvertFactor as ConvertFactor,
ProductUnitsView.ProductUnitId as ProductUnitId,
group_concat(ProductBatchOnHandQty.BatchNo, ':') as BatchNo,
CAST(OnHandQty.OnHandQty as REAL) as OnHandQty,
CAST(IFNULL(OnHandQty.RenewQty,0) as REAL) as RenewQty,
CAST(IFNULL(OnHandQty.OnHandQty,0) - IFNULL(TotalProductOrderQtyView.TotalQty,0) as REAL ) as RemainedQty,
CAST(IFNULL(TotalProductOrderQtyView.TotalQty,0) as REAL) as TotalQty,
CAST (PR.SalePrice as REAL) as SalePrice,
CAST(ifnull(PR.SalePrice,0) * (IFNULL(OnHandQty.OnHandQty,0) - IFNULL(TotalProductOrderQtyView.TotalQty,0)) as REAL) as RemainedPriceQty
FROM
OnHandQty
JOIN ProductUnitsView ON ProductUnitsView.UniqueId = OnHandQty.ProductId
JOIN Product On Product.UniqueId = OnHandQty.ProductId
JOIN PriceHistory PR
     on PR.GoodsRef= Product.BackOfficeId
      AND pr.BackOfficeId = (
          Select tP2.BackOfficeId
          From PriceHistory tP2
         Where tP2.GoodsRef=Product.BackOfficeId
         AND ((SELECT Value FROM SysConfig WHERE UniqueId = '94E76D64-62B7-430D-B5F9-420695C8DB66') BETWEEN tP2.StartDate AND IFNULL(tP2.EndDate, (SELECT Value FROM SysConfig WHERE UniqueId = '94E76D64-62B7-430D-B5F9-420695C8DB66')))
         LIMIT 1
      )
LEFT JOIN TotalProductOrderQtyView ON TotalProductOrderQtyView.ProductId = OnHandQty.ProductId
LEFT JOIN ProductBatchOnHandQty ON ProductBatchOnHandQty.ProductId = OnHandQty.ProductId
GROUP BY Product.UniqueId;