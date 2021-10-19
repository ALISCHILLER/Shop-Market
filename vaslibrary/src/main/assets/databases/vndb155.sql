
-- ----------------------------
--  Add table DataForRegister , CustomerDataForRegister related to issue: NGT-4023
-- ----------------------------
CREATE TABLE DataForRegister (
    FieldName  TEXT,
    FieldKey   TEXT,
    FieldValue TEXT,
    UniqueId   TEXT PRIMARY KEY COLLATE NOCASE
);
CREATE TABLE CustomerDataForRegister (
    CustomerId TEXT COLLATE NOCASE,
    FieldName  TEXT,
    FieldKey   TEXT,
    FieldValue TEXT,
    UniqueId   TEXT PRIMARY KEY COLLATE NOCASE
);

-- ----------------------------
--  Alter WarehouseProductQtyView related to issue:NGT-4115
-- ----------------------------
DROP VIEW IF EXISTS "main"."WarehouseProductQtyView";
CREATE VIEW "WarehouseProductQtyView" AS
SELECT
	Product.UniqueId AS UniqueId,
	Product.ProductCode,
	Product.ProductName,
	Product.ProductTypeId,
	ProductUnitsView.UnitName AS UnitName,
	ProductUnitsView.ConvertFactor AS ConvertFactor,
	ProductUnitsView.ProductUnitId AS ProductUnitId,
	group_concat( ProductBatchOnHandQty.BatchNo, ':' ) AS BatchNo,
	CAST ( OnHandQty.OnHandQty AS REAL ) AS OnHandQty,
	CAST ( IFNULL( OnHandQty.RenewQty, 0 ) AS REAL ) AS RenewQty,
	CAST ( IFNULL( OnHandQty.ReservedQty, 0 ) AS REAL ) AS ReservedQty,
CASE

		WHEN CAST ( IFNULL( OnHandQty.OnHandQty, 0 ) - IFNULL( OnHandQty.ReservedQty, 0 ) AS REAL ) > 0 THEN
		CAST ( IFNULL( OnHandQty.OnHandQty, 0 ) - IFNULL( OnHandQty.ReservedQty, 0 ) AS REAL ) ELSE 0
	END AS RemainedAfterReservedQty,
	CAST ( IFNULL( OnHandQty.OnHandQty, 0 ) - IFNULL( TotalProductOrderQtyView.TotalQty, 0 ) AS REAL ) AS RemainedQty,
	CAST ( IFNULL( TotalProductOrderQtyView.TotalQty, 0 ) AS REAL ) AS TotalQty,
	CAST ( PR.SalePrice AS REAL ) AS SalePrice,
	CAST (
		ifnull( PR.SalePrice, 0 ) * ( IFNULL( OnHandQty.OnHandQty, 0 ) - IFNULL( TotalProductOrderQtyView.TotalQty, 0 ) ) AS REAL
	) AS RemainedPriceQty,
	ProductReturn.TotalReturnQty
FROM
	OnHandQty
	JOIN ProductUnitsView ON ProductUnitsView.UniqueId = OnHandQty.ProductId
	JOIN Product ON Product.UniqueId = OnHandQty.ProductId
	JOIN PriceHistory PR ON PR.GoodsRef = Product.BackOfficeId
	AND pr.BackOfficeId = (
	SELECT
		tP2.BackOfficeId
	FROM
		PriceHistory tP2
	WHERE
		tP2.GoodsRef = Product.BackOfficeId
		AND (
			( SELECT Value FROM SysConfig WHERE UniqueId = '94E76D64-62B7-430D-B5F9-420695C8DB66' ) BETWEEN IFNULL( tP2.StartDate, ( SELECT Value FROM SysConfig WHERE UniqueId = '94E76D64-62B7-430D-B5F9-420695C8DB66' ) )
			AND IFNULL( tP2.EndDate, ( SELECT Value FROM SysConfig WHERE UniqueId = '94E76D64-62B7-430D-B5F9-420695C8DB66' ) )
		)
		LIMIT 1
	)
	LEFT JOIN TotalProductOrderQtyView ON TotalProductOrderQtyView.ProductId = OnHandQty.ProductId
	LEFT JOIN ProductBatchOnHandQty ON ProductBatchOnHandQty.ProductId = OnHandQty.ProductId
	LEFT JOIN ( SELECT SUM( TotalReturnQty ) AS TotalReturnQty, ProductId FROM CustomerCallReturnView GROUP BY CustomerCallReturnView.ProductId ) AS ProductReturn ON ProductReturn.ProductId = OnHandQty.ProductId
GROUP BY
	Product.UniqueId;


-- ----------------------------
--  Add Item to table CustomerOldInvoiceDetail related to issue: NGT-4098
-- ----------------------------
alter table CustomerOldInvoiceDetail add column Item TEXT;

-- ----------------------------
--  Add ReferenceNo to view CustomerCallReturnView related to issue: NGT-4098
-- ----------------------------
DROP VIEW IF EXISTS CustomerCallReturnView;
CREATE VIEW CustomerCallReturnView AS
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
                      sum(CustomerOldInvoiceDetail.TotalQty) AS InvoiceQty
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
--  Add Item to view OldInvoiceDetailView related to issue: NGT-4098
-- ----------------------------
DROP VIEW IF EXISTS OldInvoiceDetailView;
CREATE VIEW OldInvoiceDetailView AS
    SELECT CustomerOldInvoiceHeader.CustomerId,
           CustomerOldInvoiceHeader.StockId,
           CustomerOldInvoiceDetail.SaleId AS SaleId,
           CustomerOldInvoiceHeader.SaleNo,
           CustomerOldInvoiceDetail.ProductId,
           CustomerOldInvoiceDetail.PrizeType,
           AddAmount,
           UnitQty,
           TotalQty,
           UnitPrice,
           CustomerOldInvoiceHeader.SalePDate,
           Product.ProductName,
           Product.ProductCode,
           Product.ProductGroupId,
           CustomerOldInvoiceDetail.TotalAmount,
           CustomerOldInvoiceDetail.Item AS Item,
           CustomerCallReturnView.TotalReturnQty AS TotalReturnQty,
           CustomerCallReturnView.TotalRequestAmount AS TotalRequestAmount
      FROM (
               SELECT SaleId,
                      ProductId,
                      Item,
                      SUM(CustomerOldInvoiceDetail.AddAmount) AS AddAmount,
                      SUM(CustomerOldInvoiceDetail.UnitQty) AS UnitQty,
                      SUM(CustomerOldInvoiceDetail.TotalQty) AS TotalQty,
                      CustomerOldInvoiceDetail.PrizeType AS PrizeType,
                      MAX(CustomerOldInvoiceDetail.UnitPrice * ABS(PrizeType - 1) ) AS UnitPrice,
                      SUM( (CustomerOldInvoiceDetail.UnitPrice * CustomerOldInvoiceDetail.TotalQty) * ABS(PrizeType - 1) ) AS TotalAmount
                 FROM CustomerOldInvoiceDetail
                GROUP BY CustomerOldInvoiceDetail.ProductId,
                         CustomerOldInvoiceDetail.SaleId
           )
           AS CustomerOldInvoiceDetail
           JOIN
           Product ON CustomerOldInvoiceDetail.ProductId = Product.UniqueId
           LEFT JOIN
           CustomerCallReturnView ON CustomerCallReturnView.ProductId = Product.UniqueId AND
                                     CustomerCallReturnView.InvoiceId = SaleId
           JOIN
           CustomerOldInvoiceHeader ON CustomerOldInvoiceHeader.UniqueId = CustomerOldInvoiceDetail.SaleId;


-- ----------------------------
--  Alter table CustomerCallReturnLines convert ReferenceNo to String. related to issue: NGT-4098
-- ----------------------------

PRAGMA foreign_keys = 0;
CREATE TABLE sqlitestudio_temp_table AS SELECT * FROM CustomerCallReturnLines;
DROP TABLE CustomerCallReturnLines;
CREATE TABLE CustomerCallReturnLines (
    UniqueId                   TEXT        NOT NULL
                                           COLLATE NOCASE,
    ReturnUniqueId             TEXT (2048) COLLATE NOCASE,
    ProductUniqueId            TEXT        COLLATE NOCASE,
    IsFreeItem                 INTEGER,
    RequestUnitPrice           REAL,
    TotalRequestAdd1Amount     REAL,
    TotalRequestAdd2Amount     REAL,
    TotalRequestAddOtherAmount REAL,
    TotalRequestTax            REAL,
    TotalRequestCharge         REAL,
    TotalRequestNetAmount      REAL,
    TotalRequestDis1Amount     REAL,
    TotalRequestDis2Amount     REAL,
    TotalRequestDis3Amount     REAL,
    TotalRequestDisOtherAmount REAL,
    SortId                     INTEGER,
    IndexInfo                  INTEGER,
    Weight                     REAL,
    ReturnProductTypeId        TEXT,
    ReferenceId                TEXT,
    ReferenceNo                TEXT,
    ReturnReasonId             TEXT,
    RequestBulkQty             REAL,
    RequestBulkUnitId          TEXT,
    IsPromoLine                INTEGER,
    StockId                    TEXT,
    ReferenceDate              INTEGER,
    PRIMARY KEY (
        UniqueId ASC
    ),
    CONSTRAINT fkey0 FOREIGN KEY (
        RequestBulkUnitId
    )
    REFERENCES ProductUnit (UniqueId) ON DELETE CASCADE
                                      ON UPDATE CASCADE,
    CONSTRAINT fkey3 FOREIGN KEY (
        ProductUniqueId
    )
    REFERENCES Product (UniqueId) ON DELETE CASCADE
                                  ON UPDATE CASCADE,
    CONSTRAINT fkey4 FOREIGN KEY (
        ReturnUniqueId
    )
    REFERENCES CustomerCallReturn (UniqueId) ON DELETE CASCADE
                                             ON UPDATE CASCADE
);
INSERT INTO CustomerCallReturnLines (
                                        UniqueId,
                                        ReturnUniqueId,
                                        ProductUniqueId,
                                        IsFreeItem,
                                        RequestUnitPrice,
                                        TotalRequestAdd1Amount,
                                        TotalRequestAdd2Amount,
                                        TotalRequestAddOtherAmount,
                                        TotalRequestTax,
                                        TotalRequestCharge,
                                        TotalRequestNetAmount,
                                        TotalRequestDis1Amount,
                                        TotalRequestDis2Amount,
                                        TotalRequestDis3Amount,
                                        TotalRequestDisOtherAmount,
                                        SortId,
                                        IndexInfo,
                                        Weight,
                                        ReturnProductTypeId,
                                        ReferenceId,
                                        ReferenceNo,
                                        ReturnReasonId,
                                        RequestBulkQty,
                                        RequestBulkUnitId,
                                        IsPromoLine,
                                        StockId,
                                        ReferenceDate
                                    )
                                    SELECT UniqueId,
                                           ReturnUniqueId,
                                           ProductUniqueId,
                                           IsFreeItem,
                                           RequestUnitPrice,
                                           TotalRequestAdd1Amount,
                                           TotalRequestAdd2Amount,
                                           TotalRequestAddOtherAmount,
                                           TotalRequestTax,
                                           TotalRequestCharge,
                                           TotalRequestNetAmount,
                                           TotalRequestDis1Amount,
                                           TotalRequestDis2Amount,
                                           TotalRequestDis3Amount,
                                           TotalRequestDisOtherAmount,
                                           SortId,
                                           IndexInfo,
                                           Weight,
                                           ReturnProductTypeId,
                                           ReferenceId,
                                           ReferenceNo,
                                           ReturnReasonId,
                                           RequestBulkQty,
                                           RequestBulkUnitId,
                                           IsPromoLine,
                                           StockId,
                                           ReferenceDate
                                      FROM sqlitestudio_temp_table;

DROP TABLE sqlitestudio_temp_table;
PRAGMA foreign_keys = 1;

-- ----------------------------
--  Alter table CustomerCallReturnLinesRequest convert ReferenceNo to String. related to issue: NGT-4098
-- ----------------------------

PRAGMA foreign_keys = 0;
CREATE TABLE sqlitestudio_temp_table AS SELECT * FROM CustomerCallReturnLinesRequest;
DROP TABLE CustomerCallReturnLinesRequest;
CREATE TABLE CustomerCallReturnLinesRequest (
    UniqueId                   TEXT        NOT NULL
                                           COLLATE NOCASE,
    ReturnUniqueId             TEXT (2048) COLLATE NOCASE,
    ProductUniqueId            TEXT        COLLATE NOCASE,
    IsFreeItem                 INTEGER,
    RequestUnitPrice           REAL,
    TotalRequestAdd1Amount     REAL,
    TotalRequestAdd2Amount     REAL,
    TotalRequestAddOtherAmount REAL,
    TotalRequestTax            REAL,
    TotalRequestCharge         REAL,
    TotalRequestNetAmount      REAL,
    TotalRequestDis1Amount     REAL,
    TotalRequestDis2Amount     REAL,
    TotalRequestDis3Amount     REAL,
    TotalRequestDisOtherAmount REAL,
    SortId                     INTEGER,
    IndexInfo                  INTEGER,
    Weight                     REAL,
    ReturnProductTypeId        TEXT,
    ReferenceId                TEXT,
    ReferenceNo                TEXT,
    ReturnReasonId             TEXT,
    RequestBulkQty             REAL,
    RequestBulkUnitId          TEXT,
    IsPromoLine                INTEGER,
    StockId                    TEXT,
    ReferenceDate              INTEGER,
    PRIMARY KEY (
        UniqueId ASC
    ),
    CONSTRAINT fkey0 FOREIGN KEY (
        RequestBulkUnitId
    )
    REFERENCES ProductUnit (UniqueId) ON DELETE CASCADE
                                      ON UPDATE CASCADE,
    CONSTRAINT fkey3 FOREIGN KEY (
        ProductUniqueId
    )
    REFERENCES Product (UniqueId) ON DELETE CASCADE
                                  ON UPDATE CASCADE,
    CONSTRAINT fkey4 FOREIGN KEY (
        ReturnUniqueId
    )
    REFERENCES CustomerCallReturnRequest (UniqueId) ON DELETE CASCADE
                                                    ON UPDATE CASCADE
);

INSERT INTO CustomerCallReturnLinesRequest (
                                               UniqueId,
                                               ReturnUniqueId,
                                               ProductUniqueId,
                                               IsFreeItem,
                                               RequestUnitPrice,
                                               TotalRequestAdd1Amount,
                                               TotalRequestAdd2Amount,
                                               TotalRequestAddOtherAmount,
                                               TotalRequestTax,
                                               TotalRequestCharge,
                                               TotalRequestNetAmount,
                                               TotalRequestDis1Amount,
                                               TotalRequestDis2Amount,
                                               TotalRequestDis3Amount,
                                               TotalRequestDisOtherAmount,
                                               SortId,
                                               IndexInfo,
                                               Weight,
                                               ReturnProductTypeId,
                                               ReferenceId,
                                               ReferenceNo,
                                               ReturnReasonId,
                                               RequestBulkQty,
                                               RequestBulkUnitId,
                                               IsPromoLine,
                                               StockId,
                                               ReferenceDate
                                           )
                                           SELECT UniqueId,
                                                  ReturnUniqueId,
                                                  ProductUniqueId,
                                                  IsFreeItem,
                                                  RequestUnitPrice,
                                                  TotalRequestAdd1Amount,
                                                  TotalRequestAdd2Amount,
                                                  TotalRequestAddOtherAmount,
                                                  TotalRequestTax,
                                                  TotalRequestCharge,
                                                  TotalRequestNetAmount,
                                                  TotalRequestDis1Amount,
                                                  TotalRequestDis2Amount,
                                                  TotalRequestDis3Amount,
                                                  TotalRequestDisOtherAmount,
                                                  SortId,
                                                  IndexInfo,
                                                  Weight,
                                                  ReturnProductTypeId,
                                                  ReferenceId,
                                                  ReferenceNo,
                                                  ReturnReasonId,
                                                  RequestBulkQty,
                                                  RequestBulkUnitId,
                                                  IsPromoLine,
                                                  StockId,
                                                  ReferenceDate
                                             FROM sqlitestudio_temp_table;

DROP TABLE sqlitestudio_temp_table;
PRAGMA foreign_keys = 1;
