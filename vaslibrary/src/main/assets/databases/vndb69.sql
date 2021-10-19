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