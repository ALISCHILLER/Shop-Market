CREATE TABLE CustomerCallReturnTemp (
    UniqueId                          TEXT (100)  NOT NULL
                                                  COLLATE NOCASE,
    CustomerUniqueId                  TEXT (100),
    ReturnTypeUniqueId                TEXT,
    PersonnelUniqueId                 TEXT,
    LocalPaperNo                      TEXT (20),
    BackOfficeDistId                  TEXT (100),
    BackOfficeInvoiceId               TEXT,
    BackOfficeInvoiceNo               INTEGER,
    BackOfficeInvoiceDate             INTEGER,
    ReturnRequestBackOfficeId         INTEGER,
    ReturnRequestBackOfficeDate       INTEGER,
    ReturnRequestBackOfficeNo         TEXT (20),
    ReturnReasonUniqueId              TEXT,
    CallActionStatusUniqueId          TEXT (100),
    ReturnRequestRejectReasonUniqueId TEXT (100),
    TotalRequestAmount                REAL,
    TotalRequestTax                   REAL,
    TotalRequestCharge                REAL,
    TotalRequestDiscount              REAL,
    TotalReturnAmount                 REAL,
    TotalReturnOtherDiscount          REAL,
    TotalReturnDis1                   REAL,
    TotalReturnDis2                   REAL,
    TotalReturnDis3                   REAL,
    TotalReturnCharge                 REAL,
    TotalReturnTax                    REAL,
    TotalReturnAdd1                   REAL,
    TotalReturnAdd2                   REAL,
    Comment                           TEXT (2048),
    DCRefSDS                          INTEGER,
    SaleOfficeRefSDS                  INTEGER,
    StartTime                         INTEGER,
    EndTime                           INTEGER,
    DealerUniqueId                    TEXT,
    IsFromRequest                     INTEGER     DEFAULT 0,
    ReplacementRegistration           INTEGER,
    IsCancelled                       INTEGER,
    PRIMARY KEY (
        UniqueId ASC
    ),
    CONSTRAINT fkey1 FOREIGN KEY (
        CustomerUniqueId
    )
    REFERENCES Customer (UniqueId) ON DELETE CASCADE
                                   ON UPDATE CASCADE
);

CREATE TABLE CustomerCallReturnLinesTemp (
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
    ReferenceNo                INTEGER,
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
    REFERENCES CustomerCallReturnTemp (UniqueId) ON DELETE CASCADE
                                             ON UPDATE CASCADE
);


CREATE TABLE CustomerCallReturnLinesQtyDetailTemp (
    UniqueId           TEXT NOT NULL
                            COLLATE NOCASE,
    ReturnLineUniqueId TEXT COLLATE NOCASE,
    ProductUnitId      TEXT COLLATE NOCASE,
    Qty                REAL,
    PRIMARY KEY (
        UniqueId ASC
    ),
    CONSTRAINT fkey1 FOREIGN KEY (
        ProductUnitId
    )
    REFERENCES ProductUnit (UniqueId) ON DELETE CASCADE
                                      ON UPDATE CASCADE,
    CONSTRAINT fkey2 FOREIGN KEY (
        ReturnLineUniqueId
    )
    REFERENCES CustomerCallReturnLinesTemp (UniqueId) ON DELETE CASCADE
                                                  ON UPDATE CASCADE
);

