-- ----------------------------
-- Table structure for CustomerShipToParty
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerShipToParty";
CREATE TABLE "CustomerShipToParty" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"SoldToPartyUniqueId" TEXT,
"BackOfficeId"  TEXT,
"CustomerName"  TEXT,
"Address"  TEXT,
"Phone"  TEXT,
"StoreName"  TEXT,
"Mobile"  TEXT,
"Longitude"  REAL,
"Latitude"  REAL,
"PostCode"  TEXT,
"EconomicCode"  TEXT(2048),
"NationalCode"  TEXT(2048),
"IsActive"  INTEGER,
"IgnoreLocation" INTEGER,
PRIMARY KEY ("UniqueId" ASC)
);