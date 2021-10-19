-- ----------------------------
--  Alter CustomerModel related to issue: NGT-3836
-- ----------------------------
alter table CustomerCallInvoice add column DealerMobile TEXT;
alter table CustomerCallOrder add column DealerMobile TEXT;


-- ----------------------------
--  Alter OnHandQty related to issue:NGT-3861
-- ----------------------------
ALTER TABLE OnHandQty ADD COLUMN ReservedQty REAL;
DROP VIEW IF EXISTS "main"."WarehouseProductQtyView";
CREATE VIEW "WarehouseProductQtyView" AS
    SELECT Product.UniqueId AS UniqueId,
           Product.ProductCode,
           Product.ProductName,
           Product.ProductTypeId,
           ProductUnitsView.UnitName AS UnitName,
           ProductUnitsView.ConvertFactor AS ConvertFactor,
           ProductUnitsView.ProductUnitId AS ProductUnitId,
           group_concat(ProductBatchOnHandQty.BatchNo, ':') AS BatchNo,
           CAST (OnHandQty.OnHandQty AS REAL) AS OnHandQty,
           CAST (IFNULL(OnHandQty.RenewQty, 0) AS REAL) AS RenewQty,
           CAST (IFNULL(OnHandQty.ReservedQty, 0) AS REAL) AS ReservedQty,
           CASE WHEN CAST (IFNULL(OnHandQty.OnHandQty, 0) - IFNULL(OnHandQty.ReservedQty, 0) AS REAL) > 0 THEN CAST (IFNULL(OnHandQty.OnHandQty, 0) - IFNULL(OnHandQty.ReservedQty, 0) AS REAL) ELSE 0 END AS RemainedAfterReservedQty,
           CAST (IFNULL(OnHandQty.OnHandQty, 0) - IFNULL(TotalProductOrderQtyView.TotalQty, 0) AS REAL) AS RemainedQty,
           CAST (IFNULL(TotalProductOrderQtyView.TotalQty, 0) AS REAL) AS TotalQty,
           CAST (PR.SalePrice AS REAL) AS SalePrice,
           CAST (ifnull(PR.SalePrice, 0) * (IFNULL(OnHandQty.OnHandQty, 0) - IFNULL(TotalProductOrderQtyView.TotalQty, 0) ) AS REAL) AS RemainedPriceQty,
           ProductReturn.TotalReturnQty
      FROM OnHandQty
           JOIN
           ProductUnitsView ON ProductUnitsView.UniqueId = OnHandQty.ProductId
           JOIN
           Product ON Product.UniqueId = OnHandQty.ProductId
           JOIN
           PriceHistory PR ON PR.GoodsRef = Product.BackOfficeId AND
                              pr.BackOfficeId = (
                                                    SELECT tP2.BackOfficeId
                                                      FROM PriceHistory tP2
                                                     WHERE tP2.GoodsRef = Product.BackOfficeId AND
                                                           ( (
                                                               SELECT Value
                                                                 FROM SysConfig
                                                                WHERE UniqueId = '94E76D64-62B7-430D-B5F9-420695C8DB66'
                                                           )
                                                           BETWEEN tP2.StartDate AND IFNULL(tP2.EndDate, (
                                                                                                SELECT Value
                                                                                                  FROM SysConfig
                                                                                                 WHERE UniqueId = '94E76D64-62B7-430D-B5F9-420695C8DB66'
                                                                                            )
                                                                                     ) )
                                                     LIMIT 1
                                                )
           LEFT JOIN
           TotalProductOrderQtyView ON TotalProductOrderQtyView.ProductId = OnHandQty.ProductId
           LEFT JOIN
           ProductBatchOnHandQty ON ProductBatchOnHandQty.ProductId = OnHandQty.ProductId
           LEFT JOIN
           (
               SELECT SUM(TotalReturnQty) AS TotalReturnQty,
                      ProductId
                 FROM CustomerCallReturnView
                GROUP BY CustomerCallReturnView.ProductId
           )
           AS ProductReturn ON ProductReturn.ProductId = OnHandQty.ProductId
     GROUP BY Product.UniqueId;

DROP VIEW IF EXISTS CustomerCallOrderOrderView;
CREATE VIEW CustomerCallOrderOrderView AS
    SELECT CustomerCallInvoiceLinesView.TotalQty AS OriginalTotalQty,
           a.*,
           CASE WHEN a.RequestBulkQtyUnitUniqueId IS NULL THEN (a.TotalQty * a.UnitPrice) ELSE (a.RequestBulkQty * a.UnitPrice) END AS RequestAmount,
           FreeReason.FreeReasonName AS FreeReasonName,
           OnHandQty.OnHandQty AS OnHandQty,
           CASE WHEN CAST (IFNULL(OnHandQty.OnHandQty, 0) - IFNULL(OnHandQty.ReservedQty, 0) AS REAL) > 0 THEN CAST (IFNULL(OnHandQty.OnHandQty, 0) - IFNULL(OnHandQty.ReservedQty, 0) AS REAL) ELSE 0 END AS RemainedAfterReservedQty,
           TotalProductOrderQtyView.TotalQty AS ProductTotalOrderedQty,
           ProductBatchView.BatchNo AS BatchNo,
           ProductBatchView.ExpDate AS ExpDate,
           ProductBatchView.OnHandQty AS BatchOnHandQty,
           ProductBatchView.ItemRef AS ItemRef,
           ProductBatchView.BatchRef AS BatchRef,
           OnHandQty.HasAllocation AS HasAllocation,
           CASE WHEN a.RequestBulkQtyUnitUniqueId IS NULL THEN (a.TotalQty * (productWeight * 0.001) ) ELSE (a.RequestBulkQty) END AS TotalWeight
      FROM (
               SELECT CustomerCallOrderLines.UniqueId AS UniqueId,
                      CustomerCallOrderLines.OrderUniqueId AS OrderUniqueId,
                      CustomerCallOrder.CustomerUniqueId AS CustomerUniqueId,
                      CustomerCallOrderLines.FreeReasonId AS FreeReasonId,
                      CustomerCallOrderLines.RequestAdd1Amount AS RequestAdd1Amount,
                      CustomerCallOrderLines.RequestAdd2Amount AS RequestAdd2Amount,
                      CustomerCallOrderLines.RequestOtherAddAmount AS RequestAddOtherAmount,
                      CustomerCallOrderLines.RequestTaxAmount AS RequestTaxAmount,
                      CustomerCallOrderLines.RequestChargeAmount AS RequestChargeAmount,
                      CustomerCallOrderLines.RequestDis1Amount AS RequestDis1Amount,
                      CustomerCallOrderLines.RequestDis2Amount AS RequestDis2Amount,
                      CustomerCallOrderLines.RequestDis3Amount AS RequestDis3Amount,
                      CustomerCallOrderLines.RequestOtherDiscountAmount AS RequestOtherDiscountAmount,
                      CustomerCallOrderLines.InvoiceAmount AS InvoiceAmount,
                      CustomerCallOrderLines.InvoiceAdd1Amount AS InvoiceAdd1Amount,
                      CustomerCallOrderLines.InvoiceAdd2Amount AS InvoiceAdd2Amount,
                      CustomerCallOrderLines.InvoiceTaxAmount AS InvoiceTaxAmount,
                      CustomerCallOrderLines.InvoiceChargeAmount AS InvoiceChargeAmount,
                      CustomerCallOrderLines.InvoiceOtherDiscountAmount AS InvoiceOtherDiscountAmount,
                      CustomerCallOrderLines.InvoiceDis1Amount AS InvoiceDis1Amount,
                      CustomerCallOrderLines.InvoiceDis2Amount AS InvoiceDis2Amount,
                      CustomerCallOrderLines.InvoiceDis3Amount AS InvoiceDis3Amount,
                      CustomerCallOrderLines.RequestBulkQty AS RequestBulkQty,
                      Qty * ConvertFactor AS TotalQtyBulk,
                      CustomerCallOrderLines.SortId AS SortId,
                      CustomerCallOrderLines.RequestBulkQtyUnitUniqueId AS RequestBulkQtyUnitUniqueId,
                      CustomerCallOrderLines.IsPromoLine AS IsPromoLine,
                      CASE WHEN CustomerCallOrderLines.RequestBulkQtyUnitUniqueId IS NULL THEN CASE WHEN sum(Qty * ConvertFactor) > 0 THEN CustomerCallOrderLines.PromotionPrice ELSE 0 END ELSE CASE WHEN CustomerCallOrderLines.RequestBulkQty > 0 THEN CustomerCallOrderLines.PromotionPrice ELSE 0 END END AS PromotionPrice,
                      CustomerCallOrderLines.PayDuration AS PayDuration,
                      CustomerCallOrderLines.RuleNo AS RuleNo,
                      Product.ProductName AS ProductName,
                      Product.ProductCode AS ProductCode,
                      Product.IsFreeItem AS IsFreeItem,
                      Product.OrderPoint AS OrderPoint,
                      Product.PayDuration AS ProductPayDuration,
                      CustomerPrice.Price AS UnitPrice,
                      CustomerPrice.UserPrice AS UserPrice,
                      CustomerPrice.PriceId AS PriceId,
                      CustomerCallOrderLines.ProductUniqueId AS ProductId,
                      group_concat(Qty, ':') AS Qty,
                      group_concat(ConvertFactor, ':') AS ConvertFactor,
                      group_concat(CustomerCallOrderLinesOrderQtyDetail.ProductUnitId, ':') AS ProductUnitId,
                      group_concat(Unit.UniqueId, ':') AS UnitId,
                      group_concat(UnitName, ':') AS UnitName,
                      CASE WHEN CustomerCallOrderLines.RequestBulkQtyUnitUniqueId IS NULL THEN sum(Qty * ConvertFactor) ELSE CustomerCallOrderLines.RequestBulkQty END AS TotalQty,
                      CustomerCallOrderLines.IsRequestFreeItem,
                      CustomerEmphaticProduct.Type AS EmphaticType,
                      CustomerEmphaticProduct.ProductCount AS EmphaticProductCount,
                      CustomerEmphaticProduct.TypeLawUniqueId AS TypeLawUniqueId,
                      CustomerEmphaticProduct.EmphasisRuleId AS EmphasisRuleId,
                      CustomerCallOrder.SaleDate AS SaleDate,
                      CustomerCallOrder.LocalPaperNo AS LocalPaperNo,
                      CustomerCallOrder.OrderPaymentTypeUniqueId AS OrderPaymentTypeUniqueId,
                      CustomerCallOrder.OrderTypeUniqueId AS OrderTypeUniqueId,
                      IFNULL(Product.productWeight, 0) AS productWeight
                 FROM CustomerCallOrderLines,
                      ProductUnit ON CustomerCallOrderLinesOrderQtyDetail.ProductUnitId = ProductUnit.UniqueId,
                      CustomerCallOrderLinesOrderQtyDetail ON CustomerCallOrderLinesOrderQtyDetail.OrderLineUniqueId = CustomerCallOrderLines.UniqueId,
                      Product ON Product.UniqueId = CustomerCallOrderLines.ProductUniqueId,
                      Unit ON Unit.UniqueId = ProductUnit.UnitId,
                      CustomerCallOrder ON CustomerCallOrder.UniqueId = CustomerCallOrderLines.OrderUniqueId
                      LEFT JOIN
                      CustomerEmphaticProduct ON CustomerEmphaticProduct.ProductId = Product.UniqueId
                      LEFT JOIN
                      CustomerPrice ON (CustomerPrice.CallOrderId = CustomerCallOrder.UniqueId) AND
                                       (CustomerPrice.ProductUniqueId = CustomerCallOrderLines.ProductUniqueId)
                GROUP BY OrderLineUniqueId
           )
           AS a
           LEFT JOIN
           FreeReason ON FreeReason.UniqueId = a.FreeReasonId
           LEFT JOIN
           OnHandQty ON OnHandQty.ProductId = a.ProductId
           LEFT JOIN
           TotalProductOrderQtyView ON TotalProductOrderQtyView.ProductId = a.ProductId
           LEFT JOIN
           ProductBatchView ON a.ProductId = ProductBatchView.ProductId
           LEFT JOIN
           CustomerCallInvoiceLinesView ON CustomerCallInvoiceLinesView.UniqueId = a.UniqueId;

-- ----------------------------
--  Create CatalogueLog related to issue: NGT-3835
-- ----------------------------
CREATE TABLE CatalogueLog (
    UniqueId             TEXT    COLLATE NOCASE
                                 PRIMARY KEY,
    CustomerId           TEXT    COLLATE NOCASE,
    CatalogTypeUniqueId  TEXT    COLLATE NOCASE,
    EntityId             TEXT    COLLATE NOCASE,
    StartTime            INTEGER,
    EndTime              INTEGER
);

-- ----------------------------
--  Alter CustomerCallOrderLines related to issue: NGT-3754
-- ----------------------------
alter table CustomerCallOrderLines add column PresalesAdd1Amount REAL;
alter table CustomerCallOrderLines add column PresalesAdd2Amount REAL;
alter table CustomerCallOrderLines add column PresalesOtherAddAmount REAL;
alter table CustomerCallOrderLines add column PresalesTaxAmount REAL;
alter table CustomerCallOrderLines add column PresalesChargeAmount REAL;
alter table CustomerCallOrderLines add column PresalesDis1Amount REAL;
alter table CustomerCallOrderLines add column PresalesDis2Amount REAL;
alter table CustomerCallOrderLines add column PresalesDis3Amount REAL;
alter table CustomerCallOrderLines add column PresalesOtherDiscountAmount REAL;