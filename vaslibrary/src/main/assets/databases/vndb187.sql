<<<<<<< HEAD


---CREATE TABLE LocationConfirmTracking
alter table Product add column Cart  TEXT;

alter table Customer add column StrSaleStatus TEXT;


=======
---CREATE TABLE LocationConfirmTracking
CREATE TABLE "LocationConfirmTracking" (
"uniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"Lat"  TEXT ,
"Long"  TEXT  ,
"StrCreateDate" TEXT,
PRIMARY KEY ("uniqueId" ASC));
>>>>>>> simolate2
