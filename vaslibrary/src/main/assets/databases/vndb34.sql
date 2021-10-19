-- ----------------------------
-- View structure for RequestLineView
-- ----------------------------
DROP VIEW IF EXISTS "main"."RequestLineView";
CREATE VIEW RequestLineView AS
SELECT
	Product.UniqueId,
	Product.ProductCode,
	Product.ProductName,
	Product.ProductGroupId,
	RequestLine.RowIndex,
	RequestLine.BulkQty,
	RequestLine.BulkQtyUnitUniqueId,
	RequestLine.UniqueId AS RequestLineUniqueId,
	group_concat(RequestLineQty.Qty,':') AS Qty,
	group_concat(RequestLineQty.ProductUnitId,':') AS ProductUnitId,
	group_concat(ProductUnitView.UnitName,':') as UnitName,
	CAST(group_concat(ProductUnitView.ConvertFactor,':') as real) as ConvertFactor,
	CAST(SUM(RequestLineQty.Qty * ProductUnitView.ConvertFactor) as real) as TotalQty,
	PR.SalePrice AS UnitPrice,
	CAST(SUM(RequestLineQty.Qty * ProductUnitView.ConvertFactor) * PR.SalePrice as real) as TotalPrice
FROM
	Product
	JOIN (
SELECT PR.SalePrice,
Product.UniqueId AS ProductUniqueId
FROM	Product
	JOIN PriceHistory PR
		 on PR.GoodsRef= Product.BackOfficeId
 		 AND pr.BackOfficeId = (
 				 Select tP2.BackOfficeId
 				 From PriceHistory tP2
				 Where tP2.GoodsRef=Product.BackOfficeId
				 AND ((SELECT Value FROM SysConfig WHERE UniqueId = '94E76D64-62B7-430D-B5F9-420695C8DB66') BETWEEN tP2.StartDate AND IFNULL(tP2.EndDate, (SELECT Value FROM SysConfig WHERE UniqueId = '94E76D64-62B7-430D-B5F9-420695C8DB66')))
				 LIMIT 1
 		 ) GROUP BY Product.UniqueId) as PR ON ProductUniqueId = Product.UniqueId
LEFT JOIN RequestLine ON Product.UniqueId = RequestLine.ProductId
LEFT JOIN RequestLineQty ON RequestLine.UniqueId = RequestLineQty.RequestLineUniqueId
LEFT JOIN ProductUnitView ON ProductUnitView.UniqueId = RequestLineQty.ProductUnitId
WHERE
Product.IsForRequest = 1
GROUP BY
	Product.UniqueId;