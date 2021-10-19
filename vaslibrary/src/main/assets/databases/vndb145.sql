-- ----------------------------
--  Alter view DistWarehouseProductQtyView related to issue: DMC-62540
-- ----------------------------
DROP VIEW IF EXISTS "main"."DistWarehouseProductQtyView";
CREATE VIEW "DistWarehouseProductQtyView" AS
SELECT
	Product.UniqueId AS UniqueId,
	Product.ProductCode,
	Product.ProductName,
	Product.ProductTypeId,
	ProductUnitsView.UnitName AS UnitName,
	ProductUnitsView.ConvertFactor AS ConvertFactor,
	ProductUnitsView.ProductUnitId AS ProductUnitId,
	CAST ( IFNULL( OnHandQty.OnHandQty, 0 ) AS REAL ) AS OnHandQty,
	CAST ( IFNULL( TotalProductOrderQtyView.TotalQty, 0 ) AS REAL ) AS TotalOrderQty,
	CAST ( IFNULL( ReturnFromDistView.TotalQty, 0 ) AS REAL ) AS TotalReturnedQty,
	CAST ( IFNULL( WellReturnQtyView.WellReturnQty, 0 ) AS REAL ) AS WellReturnQty,
	CAST ( IFNULL( WasteReturnQtyView.WasteReturnQty, 0 ) AS REAL ) AS WasteReturnQty,
	CAST (
		IFNULL( OnHandQty.OnHandQty, 0 ) - IFNULL( TotalProductOrderQtyView.TotalQty, 0 ) + IFNULL( WellReturnQtyView.WellReturnQty, 0 ) + IFNULL( WasteReturnQtyView.WasteReturnQty, 0 ) AS REAL
	) AS WarehouseProductQty
FROM
	Product
	JOIN ProductUnitsView ON Product.UniqueId = ProductUnitsView.UniqueId
	LEFT JOIN OnHandQty ON Product.UniqueId = OnHandQty.ProductId
	LEFT JOIN WellReturnQtyView ON WellReturnQtyView.ProductUniqueId = Product.UniqueId
	LEFT JOIN WasteReturnQtyView ON WasteReturnQtyView.ProductUniqueId = Product.UniqueId
	LEFT JOIN ReturnFromDistView ON ReturnFromDistView.ProductId = Product.UniqueId
	LEFT JOIN TotalProductOrderQtyView ON TotalProductOrderQtyView.ProductId = Product.UniqueId
GROUP BY
	Product.UniqueId;