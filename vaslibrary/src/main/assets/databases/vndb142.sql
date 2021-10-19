-- ----------------------------
--  Create Table CallOrderLinesTemp related to issue: DMC-62135
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerCallOrderLinesTemp";
CREATE TABLE "CustomerCallOrderLinesTemp" (
    "UniqueId" TEXT NOT NULL COLLATE NOCASE,
    "OrderUniqueId" TEXT NOT NULL,
    "ProductUniqueId" TEXT NOT NULL,
    "SortId" INTEGER,
    "IsRequestFreeItem" INTEGER,
    "RequestBulkQty" REAL,
    "RequestBulkQtyUnitUniqueId" TEXT,
    "RequestAdd1Amount" REAL,
    "RequestAdd2Amount" REAL,
    "RequestTaxAmount" REAL,
    "RequestChargeAmount" REAL,
    "RequestDis1Amount" REAL,
    "RequestDis2Amount" REAL,
    "RequestDis3Amount" REAL,
    "RequestOtherDiscountAmount" REAL,
    "InvoiceBulkQty" REAL,
    "InvoiceBulkQtyUnitUniqueId" TEXT,
     "InvoiceAmount" REAL,
        "InvoiceAdd1Amount" REAL,
        "InvoiceAdd2Amount" REAL,
        "InvoiceTaxAmount" REAL,
        "InvoiceChargeAmount" REAL,
        "InvoiceOtherDiscountAmount" REAL,
        "InvoiceDis1Amount" REAL,
        "InvoiceDis2Amount" REAL,
        "InvoiceDis3Amount" REAL,
        "EVCId" TEXT(2048),
        "FreeReasonId" TEXT,
        "DiscountRef" INTEGER,
        "DiscountId" TEXT COLLATE NOCASE,
        "IsPromoLine" INTEGER,
    	"PromotionPrice" REAL,
    	"PayDuration" INTEGER,
    	"RuleNo" INTEGER,
    	"RequestOtherAddAmount" REAL,
        PRIMARY KEY ("UniqueId" ASC),
        CONSTRAINT "fkey0" FOREIGN KEY ("OrderUniqueId") REFERENCES "CustomerCallOrder"("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE,
            CONSTRAINT "fkey1" FOREIGN KEY ("ProductUniqueId") REFERENCES "Product"("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE,
            CONSTRAINT "fkey2" FOREIGN KEY ("RequestBulkQtyUnitUniqueId") REFERENCES "ProductUnit"("UniqueId") ON DELETE SET NULL ON UPDATE SET NULL,
            CONSTRAINT "fkey3" FOREIGN KEY ("InvoiceBulkQtyUnitUniqueId") REFERENCES "ProductUnit"("UniqueId") ON DELETE SET NULL ON UPDATE SET NULL,
            CONSTRAINT "fkey4" FOREIGN KEY ("FreeReasonId") REFERENCES "FreeReason"("UniqueId") ON DELETE SET NULL ON UPDATE SET NULL
        );
-- ----------------------------
--  Create Table CallOrderLinesQtyTemp related to issue: DMC-62135
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerCallOrderLinesQtyTemp";
CREATE TABLE "CustomerCallOrderLinesQtyTemp" (
    "UniqueId" TEXT NOT NULL COLLATE NOCASE ,
    "OrderLineUniqueId" TEXT,
    "ProductUnitId" TEXT,
    "Qty" REAL,
    PRIMARY KEY ("UniqueId" ASC),
    CONSTRAINT "fkey1" FOREIGN KEY ("ProductUnitId") REFERENCES "ProductUnit" ("UniqueId"),
    CONSTRAINT "fkey2" FOREIGN KEY ("OrderLineUniqueId") REFERENCES "CustomerCallOrderLinesTemp" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE
);