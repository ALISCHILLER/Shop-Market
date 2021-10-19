DROP VIEW IF EXISTS "main"."TotalQtyView";
CREATE VIEW TotalQtyView AS
SELECT
	CustomerCallOrderLines.OrderUniqueId,
	CustomerCallOrderLines.ProductUniqueId,
	CustomerCallOrderLines.IsRequestFreeItem,
	case
		when CustomerCallOrderLines.RequestBulkQtyUnitUniqueId is null then
			sum(ifnull(Qty,0) * ifnull(ConvertFactor,0))
		else
			CustomerCallOrderLines.RequestBulkQty end as TotalQty
FROM
	CustomerCallOrderLinesOrderQtyDetail
INNER JOIN ProductUnit ON ProductUnit.UniqueId = ProductUnitId
JOIN CustomerCallOrderLines On CustomerCallOrderLines.UniqueId = CustomerCallOrderLinesOrderQtyDetail.OrderLineUniqueId
GROUP BY
	CustomerCallOrderLines.OrderUniqueId , CustomerCallOrderLines.ProductUniqueId, CustomerCallOrderLines.IsRequestFreeItem;