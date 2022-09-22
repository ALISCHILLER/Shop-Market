alter table CustomerCallInvoice add column DocDate INTEGER;
alter table CustomerCallInvoice add column DocPDate Text;
alter table CustomerCallInvoice add column SalePDate Text;

alter table CustomerCallOrder add column DocDate INTEGER;
alter table CustomerCallOrder add column DocPDate TEXT;
alter table CustomerCallOrder add column SalePDate TEXT;

CREATE TABLE "DealerDivision" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"DivisionCenterKey"  TEXT NOT NULL COLLATE NOCASE ,
"DivisionBackOfficeCode"  TEXT NOT NULL COLLATE NOCASE ,
"DivisionSalesOrg"  TEXT NOT NULL COLLATE NOCASE ,
"DivisionDisChanel"  TEXT NOT NULL COLLATE NOCASE ,
"DivisionCode"  TEXT NOT NULL COLLATE NOCASE ,

PRIMARY KEY ("UniqueId" ASC)
);