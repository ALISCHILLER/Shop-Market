
-- ----------------------------
-- OldInvoiceDetailView
-- ----------------------------
DROP VIEW IF EXISTS "main"."OldInvoiceDetailView";
CREATE VIEW OldInvoiceDetailView AS
SELECT
CustomerOldInvoiceHeader.CustomerId,
CustomerOldInvoiceHeader.StockId,
CustomerOldInvoiceDetail.SaleId AS SaleId,
CustomerOldInvoiceHeader.SaleNo,
CustomerOldInvoiceDetail.ProductId ,
CustomerOldInvoiceDetail.AddAmount,
CustomerOldInvoiceDetail.UnitQty,
CustomerOldInvoiceDetail.TotalQty,
CustomerOldInvoiceDetail.UnitName,
CustomerOldInvoiceDetail.UnitPrice,
Product.ProductName,
Product.ProductCode,
Product.ProductGroupId,
CustomerOldInvoiceDetail.UnitPrice * CustomerOldInvoiceDetail.TotalQty AS TotalAmount,
CustomerCallReturnView.TotalReturnQty as TotalReturnQty,
CustomerCallReturnView.TotalRequestAmount as TotalRequestAmount
FROM CustomerOldInvoiceDetail
JOIN Product ON CustomerOldInvoiceDetail.ProductId = Product.UniqueId
LEFT JOIN CustomerCallReturnView ON CustomerCallReturnView.ProductId = Product.UniqueId AND
CustomerCallReturnView.InvoiceId = SaleId
JOIN CustomerOldInvoiceHeader ON CustomerOldInvoiceHeader.UniqueId = CustomerOldInvoiceDetail.SaleId;

-- ----------------------------
-- Table structure for EVCItemStatuesSDSCustomers
-- ----------------------------
DROP TABLE IF EXISTS "main"."EVCItemStatuesCustomers";
CREATE TABLE "EVCItemStatuesCustomers" (
"UniqueId"  TEXT NOT NULL,
"EVCItemRef"  INTEGER,
"RowOrder"  INTEGER,
"DisRef"  INTEGER,
"DisGroup"  INTEGER,
"AddAmount"  REAL,
"SupAmount"  REAL,
"Discount"  REAL,
"EvcId"  INTEGER,
"CustomerId"  TEXT
--"OrderLineId"  TEXT NOT NULL,
--PRIMARY KEY ("UniqueId" ASC)
--FOREIGN KEY ("OrderLineId") REFERENCES "CustomerCallOrderLines" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE
);