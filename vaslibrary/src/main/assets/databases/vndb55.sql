-- ----------------------------
--  ProductReportView
-- ----------------------------
DROP VIEW IF EXISTS "main"."ProductReportView";
CREATE VIEW ProductReportView AS
SELECT UniqueId, ProductCode, ProductName, sum(TotalQty) AS TotalQty, ConvertFactor, UnitName
FROM CustomerCallOrderOrderView GROUP BY ProductCode;