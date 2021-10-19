-- ----------------------------
--  Alter view OldInvoiceDetailView. Related to issue: DMC-39432
-- ----------------------------
DROP VIEW IF EXISTS "main"."OldInvoiceDetailView";
CREATE VIEW "OldInvoiceDetailView" AS
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
CustomerOldInvoiceHeader.SalePDate,
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