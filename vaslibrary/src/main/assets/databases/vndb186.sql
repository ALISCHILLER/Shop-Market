
alter table CustomerCallReturn add column zterm TEXT;

alter table CustomerCallReturnRequest add column zterm TEXT;

---CREATE TABLE LocationConfirmTracking
CREATE TABLE "LocationConfirmTracking" (
"uniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"Lat"  TEXT ,
"Long"  TEXT  ,
"StrCreateDate" TEXT,
PRIMARY KEY ("uniqueId" ASC));



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



