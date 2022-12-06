
---CREATE TABLE CommodityRationingViewModel
CREATE TABLE "CommodityRationingView" (
"uniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"quotasName"  TEXT ,
"fromDate"  TEXT  ,
"toDate"  TEXT,
"dataCenterOwnerIds"  TEXT,
"customerLevelIds" Text,
"customerActivityIds" Text,
"customerCategoryIds" Text,
"personnelIds" Text,
"quotasType" int,
PRIMARY KEY ("uniqueId" ASC));

--- CREATE TABLE Product_RationingViewModel
CREATE TABLE "Product_RationingView" (
"uniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"quotasUniqueId"  TEXT ,
"productUniuqeId"  TEXT  ,
"productUnitUniuqeId"  TEXT,
"productCode"  TEXT,
"productName" Text,
"quantity" int,
PRIMARY KEY ("uniqueId" ASC));
