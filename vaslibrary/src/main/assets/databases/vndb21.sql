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
GROUP BY Product.UniqueId;