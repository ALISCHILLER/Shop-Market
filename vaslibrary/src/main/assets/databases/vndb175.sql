
-- ----------------------------
-- Table structure for CustomerInventory
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerInventory";
CREATE TABLE "CustomerInventory" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"ProductId"  TEXT COLLATE NOCASE ,
"CustomerId"  TEXT COLLATE NOCASE ,
"IsAvailable"  INTEGER,
"IsSold"  INTEGER,
"FactoryDate" Text,
PRIMARY KEY ("UniqueId" ASC),
CONSTRAINT "fkey0" FOREIGN KEY ("ProductId") REFERENCES "Product" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT "fkey1" FOREIGN KEY ("CustomerId") REFERENCES "Customer" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- View structure for CustomerInventoryView
-- ----------------------------
DROP VIEW IF EXISTS "main"."CustomerInventoryView";
CREATE VIEW "CustomerInventoryView" AS
SELECT
CustomerInventory.UniqueId,
CustomerInventory.CustomerId,
CustomerInventory.ProductId,
CustomerInventory.FactoryDate,
Product.ProductName,
Product.ProductCode,
group_concat(CustomerInventoryQty.Qty,':') as Qty,
group_concat(Unit.UnitName,':') as UnitName,
sum(CustomerInventoryQty.Qty * ProductUnit.ConvertFactor) as TotalQty,
CustomerInventory.IsAvailable,
CustomerInventory.IsSold
from CustomerInventory
LEFT JOIN CustomerInventoryQty ON CustomerInventory.UniqueId = CustomerInventoryQty.CustomerInventoryId
LEFT JOIN ProductUnit ON ProductUnit.UniqueId = CustomerInventoryQty.ProductUnitId
LEFT JOIN Unit ON ProductUnit.UnitId = Unit.UniqueId
JOIN Product On Product.UniqueId = CustomerInventory.ProductId
GROUP BY CustomerInventory.UniqueId;