-- ----------------------------
-- Alter Table structure for OnHandQty
-- ----------------------------
alter table OrderPrize add column CallOrderId TEXT;

-- ----------------------------
-- View structure for OrderPrizeView
-- ----------------------------
DROP VIEW IF EXISTS "main"."OrderPrizeView";
CREATE VIEW "OrderPrizeView" AS
SELECT
OrderPrize.UniqueId AS UniqueId,
OrderPrize.DiscountId AS DiscountId,
OrderPrize.ProductId AS ProductId,
OrderPrize.TotalQty AS TotalQty,
OrderPrize.DisRef AS DisRef,
OrderPrize.CustomerId AS CustomerId,
Product.ProductName AS ProductName,
Product.ProductCode AS ProductCode,
Product.BackOfficeId AS GoodsRef,
OrderPrize.CallOrderId AS CallOrderId
FROM
OrderPrize
INNER JOIN Product ON OrderPrize.ProductId = Product.UniqueId;