DROP TABLE IF EXISTS "main"."CustomerCallOrderLinesInvoiceQtyDetail";
CREATE TABLE "CustomerCallOrderLinesInvoiceQtyDetail" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"OrderLineUniqueId"  TEXT,
"ProductUnitId"  TEXT,
"Vrkme" TEXT,
"UnitUniqueId" TEXT,
"Qty"  REAL,
PRIMARY KEY ("UniqueId" ASC),
CONSTRAINT "fkey1" FOREIGN KEY ("OrderLineUniqueId") REFERENCES "CustomerCallInvoiceLines" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT "fkey2" FOREIGN KEY ("ProductUnitId") REFERENCES "ProductUnit" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE
);

alter table CustomerCallReturn add column zterm TEXT;
alter table CustomerCallReturnRequest add column zterm TEXT;
