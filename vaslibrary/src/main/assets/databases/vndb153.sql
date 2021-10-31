-- ----------------------------
--  Alter views related to issue: DMC-70075
-- ----------------------------
DROP VIEW IF EXISTS "main"."RequestLineView";
CREATE VIEW RequestLineView AS
SELECT
	a.UniqueId,
	a.ProductCode,
	a.ProductName,
	a.ProductGroupId,
	a.RowIndex,
	a.BulkQty,
	a.BulkQtyUnitUniqueId,
	a.RequestLineUniqueId,
	group_concat( a.Qty, ':' ) AS Qty,
	group_concat( a.ProductUnitId, ':' ) AS ProductUnitId,
	group_concat(a.UnitName,':') as UnitName,
	CAST(group_concat(a.ConvertFactor,':') as real) as ConvertFactor,
	CAST(CASE WHEN a.BulkQtyUnitUniqueId is null
	then
		SUM(a.Qty * a.ConvertFactor)
	else
		a.BulkQty
	end as real) as TotalQty,
	a.UnitPrice,
	CAST(CASE WHEN a.BulkQtyUnitUniqueId is null
	then
		SUM(a.Qty * a.ConvertFactor) * a.UnitPrice
	else
		a.BulkQty * a.UnitPrice
	end as real)  as TotalPrice
FROM
	(
	SELECT
		Product.UniqueId,
		Product.ProductCode,
		Product.ProductName,
		Product.ProductGroupId,
		RequestLine.RowIndex,
		RequestLine.BulkQty,
		RequestLine.BulkQtyUnitUniqueId,
		RequestLine.UniqueId AS RequestLineUniqueId,
		RequestLineQty.Qty AS Qty,
		RequestLineQty.ProductUnitId AS ProductUnitId,
		ProductUnitView.UnitName AS UnitName,
		ProductUnitView.ConvertFactor AS ConvertFactor,
		PR.SalePrice AS UnitPrice
	FROM
		Product
		JOIN (
		SELECT
			PR.SalePrice,
			Product.UniqueId AS ProductUniqueId
		FROM
			Product
			JOIN PriceHistory PR ON PR.GoodsRef = Product.BackOfficeId
			AND pr.BackOfficeId = (
			SELECT
				tP2.BackOfficeId
			FROM
				PriceHistory tP2
			WHERE
				tP2.GoodsRef = Product.BackOfficeId
				AND (
					( SELECT Value FROM SysConfig WHERE UniqueId = '94E76D64-62B7-430D-B5F9-420695C8DB66' ) BETWEEN tP2.StartDate
					AND IFNULL( tP2.EndDate, ( SELECT Value FROM SysConfig WHERE UniqueId = '94E76D64-62B7-430D-B5F9-420695C8DB66' ) )
				)
				LIMIT 1
			)
		) AS PR ON ProductUniqueId = Product.UniqueId
		LEFT JOIN RequestLine ON Product.UniqueId = RequestLine.ProductId
		LEFT JOIN RequestLineQty ON RequestLine.UniqueId = RequestLineQty.RequestLineUniqueId
		LEFT JOIN ProductUnitView ON ProductUnitView.UniqueId = RequestLineQty.ProductUnitId
	WHERE
		Product.IsForRequest = 1
	ORDER BY
		Product.UniqueId,
		ConvertFactor DESC
	) AS a
GROUP BY
	a.UniqueId;