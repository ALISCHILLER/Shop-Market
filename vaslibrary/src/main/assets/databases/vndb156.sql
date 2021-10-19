
-- ----------------------------
--  Add ReferenceNo to view CustomerCallReturnAfterDiscountView related to issue: NGT-4098
-- ----------------------------
DROP VIEW IF EXISTS CustomerCallReturnAfterDiscountView;
CREATE VIEW CustomerCallReturnAfterDiscountView AS
    SELECT CustomerCallReturnLinesView.*,
           CustomerOldInvoiceDetail.TotalQty AS InvoiceQty,
           CustomerOldInvoiceHeader.SaleNo AS SaleNo,
           TotalRequestNetAmount
      FROM (
               SELECT CustomerCallReturnLinesView.UniqueId AS UniqueId,
                      ProductId,
                      StockId,
                      CustomerUniqueId AS CustomerUniqueId,
                      ReturnUniqueId AS ReturnUniqueId,
                      RequestUnitPrice,
                      TotalRequestAmount AS TotalRequestAmount,
                      IsFromRequest AS IsFromRequest,
                      OriginalTotalReturnQty AS OriginalTotalReturnQty,
                      Comment AS Comment,
                      DealerUniqueId AS DealerUniqueId,
                      ProductName AS ProductName,
                      ProductCode AS ProductCode,
                      CAST (sum(TotalReturnQty) AS REAL) AS TotalReturnQty,
                      group_concat(ConvertFactor, '|') AS ConvertFactor,
                      group_concat(ProductUnitId, '|') AS ProductUnitId,
                      group_concat(Qty, '|') AS Qty,
                      group_concat(UnitName, '|') AS UnitName,
                      group_concat(ReturnProductTypeId, ':') AS ReturnProductTypeId,
                      group_concat(ReturnReasonId, ':') AS ReturnReasonId,
                      InvoiceId,
                      IsPromoLine,
                      IsCancelled,
                      ReferenceNo,
                      sum(CASE WHEN CustomerCallReturnLinesView.IsPromoLine THEN ifnull(CustomerCallReturnLinesView.TotalRequestNetAmount, 0) ELSE ifnull(CustomerCallReturnLinesView.TotalRequestAmount, 0) - ifnull(CustomerCallReturnLinesView.TotalRequestDis1Amount, 0) - ifnull(CustomerCallReturnLinesView.TotalRequestDis2Amount, 0) - ifnull(CustomerCallReturnLinesView.TotalRequestDis3Amount, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestDisOtherAmount, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestAdd1Amount, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestAdd2Amount, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestAddOtherAmount, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestTax, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestCharge, 0) END) AS TotalRequestNetAmount
                 FROM CustomerCallReturnLinesView
                      JOIN
                      ReturnReason ON ReturnReason.UniqueId = CustomerCallReturnLinesView.ReturnReasonId
                GROUP BY ProductId,
                         ReturnUniqueId,
                         InvoiceId,
                         IsPromoLine
           )
           AS CustomerCallReturnLinesView
           LEFT JOIN
           CustomerOldInvoiceDetail ON CustomerOldInvoiceDetail.ProductId = CustomerCallReturnLinesView.ProductId AND
                                       CustomerOldInvoiceDetail.SaleId = CustomerCallReturnLinesView.InvoiceId AND
                                       PrizeType = IsPromoLine
           LEFT JOIN
           CustomerOldInvoiceHeader ON CustomerOldInvoiceHeader.UniqueId = CustomerOldInvoiceDetail.SaleId AND
                                       CustomerOldInvoiceDetail.SaleId = CustomerCallReturnLinesView.InvoiceId;

-- ----------------------------
--  Alter table CustomerCallReturnLinesRequest, CustomerCallReturnLines, CustomerCallReturnLinesTemp for thirdParty
-- ----------------------------
@UNCHECKED
alter table CustomerCallReturnLinesRequest add column ItemRef TEXT;
@UNCHECKED
alter table CustomerCallReturnLines add column ItemRef TEXT;
@UNCHECKED
alter table CustomerCallReturnLinesTemp add column ItemRef TEXT;

-- ----------------------------
--  Add table CallInvoiceLineBatchQtyDetail , related to batch for dist
-- ----------------------------
@UNCHECKED
CREATE TABLE CallInvoiceLineBatchQtyDetail (
    UniqueId                      TEXT    NOT NULL,
    BatchRef                      INTEGER,
    ItemRef                       INTEGER,
    BatchNo                       TEXT,
    Qty                           REAL,
    CustomerCallOrderLineUniqueId TEXT,
    PRIMARY KEY (
        UniqueId ASC
    ),
    CONSTRAINT calllines FOREIGN KEY (
        CustomerCallOrderLineUniqueId
    )
    REFERENCES CustomerCallInvoiceLines (UniqueId) ON DELETE CASCADE
                                                 ON UPDATE CASCADE
);

-- ----------------------------
--  Alter table CustomerCallInvoice, CustomerCallOrder for thirdParty
-- ----------------------------
@UNCHECKED
alter table CustomerCallInvoice add column CheckDuration REAL;
@UNCHECKED
alter table CustomerCallInvoice add column CashDuration REAL;
@UNCHECKED
alter table CustomerCallInvoice add column PinCode TEXT;
@UNCHECKED
alter table CustomerCallInvoice add column PinCode2 TEXT;
@UNCHECKED
alter table CustomerCallOrder add column CheckDuration REAL;
@UNCHECKED
alter table CustomerCallOrder add column CashDuration REAL;
@UNCHECKED
alter table CustomerCallOrder add column PinCode TEXT;
@UNCHECKED
alter table CustomerCallOrder add column PinCode2 TEXT;

-- ----------------------------
--  Alter DistWarehouseProductQtyView related to product weight (productWeight added from Product table)
-- ----------------------------
DROP VIEW DistWarehouseProductQtyView;
CREATE VIEW DistWarehouseProductQtyView AS
    SELECT Product.UniqueId AS UniqueId,
           Product.ProductCode,
           Product.ProductName,
           Product.ProductTypeId,
           Product.productWeight,
           ProductUnitsView.UnitName AS UnitName,
           ProductUnitsView.ConvertFactor AS ConvertFactor,
           ProductUnitsView.ProductUnitId AS ProductUnitId,
           CAST ( (IFNULL(OnHandQty.OnHandQty, 0) * Product.productWeight) AS REAL) AS TotalWeight,
           CAST (IFNULL(OnHandQty.OnHandQty, 0) AS REAL) AS OnHandQty,
           CAST (IFNULL(TotalProductOrderQtyView.TotalQty, 0) AS REAL) AS TotalOrderQty,
           CAST (IFNULL(ReturnFromDistView.TotalQty, 0) AS REAL) AS TotalReturnedQty,
           CAST (IFNULL(WellReturnQtyView.WellReturnQty, 0) AS REAL) AS WellReturnQty,
           CAST (IFNULL(WasteReturnQtyView.WasteReturnQty, 0) AS REAL) AS WasteReturnQty,
           CAST (IFNULL(OnHandQty.OnHandQty, 0) - IFNULL(TotalProductOrderQtyView.TotalQty, 0) + IFNULL(WellReturnQtyView.WellReturnQty, 0) + IFNULL(WasteReturnQtyView.WasteReturnQty, 0) AS REAL) AS WarehouseProductQty
      FROM Product
           JOIN
           ProductUnitsView ON Product.UniqueId = ProductUnitsView.UniqueId
           LEFT JOIN
           OnHandQty ON Product.UniqueId = OnHandQty.ProductId
           LEFT JOIN
           WellReturnQtyView ON WellReturnQtyView.ProductUniqueId = Product.UniqueId
           LEFT JOIN
           WasteReturnQtyView ON WasteReturnQtyView.ProductUniqueId = Product.UniqueId
           LEFT JOIN
           ReturnFromDistView ON ReturnFromDistView.ProductId = Product.UniqueId
           LEFT JOIN
           TotalProductOrderQtyView ON TotalProductOrderQtyView.ProductId = Product.UniqueId
     GROUP BY Product.UniqueId;

-- ----------------------------
--  Alter table CustomerCallOrder related to issue NGT-2843
-- ----------------------------
@UNCHECKED
alter table CustomerCallOrder add column IsSent TEXT;

-- ----------------------------
--  Alter table CustomerCallOrderLines, CustomerCallReturnLines for thirdParty
-- ----------------------------
@UNCHECKED
alter table CustomerCallOrderLines add column EditReasonId TEXT;
@UNCHECKED
alter table CustomerCallReturnLines add column EditReasonId TEXT;

-- ----------------------------
--  Alter table CustomerCallOrderOrderView add EditReasonId for thirdParty
-- ----------------------------
DROP VIEW IF EXISTS "main"."CustomerCallOrderOrderView";
CREATE VIEW "CustomerCallOrderOrderView" AS
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
                      IFNULL(Product.productWeight, 0) AS productWeight,
                      CustomerCallOrderLines.EditReasonId AS EditReasonId
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
--  Alter table CustomerCallReturnView add EditReasonId for thirdParty
-- ----------------------------
DROP VIEW IF EXISTS "main"."CustomerCallReturnView";
CREATE VIEW "CustomerCallReturnView" AS
SELECT CustomerCallReturnLinesView.*,
           CustomerOldInvoiceHeader.SaleNo AS SaleNo,
           TotalRequestNetAmount
      FROM (
               SELECT CustomerCallReturnLinesView.UniqueId,
                      CustomerCallReturnLinesView.ProductId,
                      CustomerCallReturnLinesView.StockId,
                      CustomerCallReturnLinesView.ReferenceNo,
                      CustomerUniqueId AS CustomerUniqueId,
                      ReturnUniqueId AS ReturnUniqueId,
                      CustomerCallReturnLinesView.RequestUnitPrice,
                      CustomerCallReturnLinesView.TotalRequestAmount AS TotalRequestAmount,
                      CustomerCallReturnLinesView.IsFromRequest AS IsFromRequest,
                      CustomerCallReturnLinesView.IsCancelled AS IsCancelled,
                      OriginalTotalReturnQty AS OriginalTotalReturnQty,
                      Comment AS Comment,
                      CustomerCallReturnLinesView.DealerUniqueId AS DealerUniqueId,
                      ProductName AS ProductName,
                      ProductCode AS ProductCode,
                      sum(TotalReturnQty) AS TotalReturnQty,
                      group_concat(ConvertFactor, '|') AS ConvertFactor,
                      group_concat(ProductUnitId, '|') AS ProductUnitId,
                      group_concat(Qty, '|') AS Qty,
                      group_concat(CustomerCallReturnLinesView.UnitName, '|') AS UnitName,
                      group_concat(CustomerCallReturnLinesView.ReturnProductTypeId, ':') AS ReturnProductTypeId,
                      group_concat(CustomerCallReturnLinesView.ReturnReasonId, ':') AS ReturnReasonId,
                      CustomerCallReturnLinesView.InvoiceId,
                      IsPromoLine,
                      sum(CASE WHEN CustomerCallReturnLinesView.IsPromoLine THEN ifnull(CustomerCallReturnLinesView.TotalRequestNetAmount, 0) ELSE ifnull(CustomerCallReturnLinesView.TotalRequestAmount, 0) - ifnull(CustomerCallReturnLinesView.TotalRequestDis1Amount, 0) - ifnull(CustomerCallReturnLinesView.TotalRequestDis2Amount, 0) - ifnull(CustomerCallReturnLinesView.TotalRequestDis3Amount, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestDisOtherAmount, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestAdd1Amount, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestAdd2Amount, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestTax, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestCharge, 0) END) AS TotalRequestNetAmount,
                      sum(CustomerOldInvoiceDetail.TotalQty) AS InvoiceQty,
											CustomerCallReturnLinesView.EditReasonId
                 FROM CustomerCallReturnLinesView
                      LEFT JOIN
                      (
                          SELECT ProductId,
                                 SaleId,
                                 sum(TotalQty) AS TotalQty
                            FROM CustomerOldInvoiceDetail
                           GROUP BY CustomerOldInvoiceDetail.ProductId,
                                    CustomerOldInvoiceDetail.SaleId
                      )
                      AS CustomerOldInvoiceDetail ON CustomerOldInvoiceDetail.ProductId = CustomerCallReturnLinesView.ProductId AND
                                                     CustomerOldInvoiceDetail.SaleId = CustomerCallReturnLinesView.InvoiceId
                      JOIN
                      ReturnReason ON ReturnReason.UniqueId = CustomerCallReturnLinesView.ReturnReasonId
                GROUP BY CustomerCallReturnLinesView.ProductId,
                         CustomerCallReturnLinesView.ReturnUniqueId,
                         CustomerCallReturnLinesView.InvoiceId
           )
           AS CustomerCallReturnLinesView
           LEFT JOIN
           CustomerOldInvoiceHeader ON CustomerOldInvoiceHeader.UniqueId = InvoiceId;

-- ----------------------------
--  Alter table CustomerCallReturnAfterDiscountView add EditReasonId for thirdParty
--  Alter table CustomerCallReturnAfterDiscountView add ReturnReasonName for thirdParty
-- ----------------------------
DROP VIEW IF EXISTS "main"."CustomerCallReturnAfterDiscountView";
CREATE VIEW "CustomerCallReturnAfterDiscountView" AS
SELECT CustomerCallReturnLinesView.*,
           CustomerOldInvoiceDetail.TotalQty AS InvoiceQty,
           CustomerOldInvoiceHeader.SaleNo AS SaleNo,
           TotalRequestNetAmount
      FROM (
               SELECT CustomerCallReturnLinesView.UniqueId AS UniqueId,
                      ProductId,
                      StockId,
                      CustomerUniqueId AS CustomerUniqueId,
                      ReturnUniqueId AS ReturnUniqueId,
                      RequestUnitPrice,
                      TotalRequestAmount AS TotalRequestAmount,
                      IsFromRequest AS IsFromRequest,
                      OriginalTotalReturnQty AS OriginalTotalReturnQty,
                      Comment AS Comment,
                      DealerUniqueId AS DealerUniqueId,
                      ProductName AS ProductName,
                      ProductCode AS ProductCode,
                      CAST (sum(TotalReturnQty) AS REAL) AS TotalReturnQty,
                      group_concat(ConvertFactor, '|') AS ConvertFactor,
                      group_concat(ProductUnitId, '|') AS ProductUnitId,
                      group_concat(Qty, '|') AS Qty,
                      group_concat(UnitName, '|') AS UnitName,
                      group_concat(ReturnProductTypeId, ':') AS ReturnProductTypeId,
                      group_concat(ReturnReasonId, ':') AS ReturnReasonId,
											group_concat(ReturnReasonName, ':') AS ReturnReasonName,
                      InvoiceId,
                      IsPromoLine,
                      IsCancelled,
                      ReferenceNo,
                      sum(CASE WHEN CustomerCallReturnLinesView.IsPromoLine THEN ifnull(CustomerCallReturnLinesView.TotalRequestNetAmount, 0) ELSE ifnull(CustomerCallReturnLinesView.TotalRequestAmount, 0) - ifnull(CustomerCallReturnLinesView.TotalRequestDis1Amount, 0) - ifnull(CustomerCallReturnLinesView.TotalRequestDis2Amount, 0) - ifnull(CustomerCallReturnLinesView.TotalRequestDis3Amount, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestDisOtherAmount, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestAdd1Amount, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestAdd2Amount, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestAddOtherAmount, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestTax, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestCharge, 0) END) AS TotalRequestNetAmount,
                      CustomerCallReturnLinesView.EditReasonId
                 FROM CustomerCallReturnLinesView
                      JOIN
                      ReturnReason ON ReturnReason.UniqueId = CustomerCallReturnLinesView.ReturnReasonId
                GROUP BY ProductId,
                         ReturnUniqueId,
                         InvoiceId,
                         IsPromoLine
           )
           AS CustomerCallReturnLinesView
           LEFT JOIN
           CustomerOldInvoiceDetail ON CustomerOldInvoiceDetail.ProductId = CustomerCallReturnLinesView.ProductId AND
                                       CustomerOldInvoiceDetail.SaleId = CustomerCallReturnLinesView.InvoiceId AND
                                       PrizeType = IsPromoLine
           LEFT JOIN
           CustomerOldInvoiceHeader ON CustomerOldInvoiceHeader.UniqueId = CustomerOldInvoiceDetail.SaleId AND
                                       CustomerOldInvoiceDetail.SaleId = CustomerCallReturnLinesView.InvoiceId;

