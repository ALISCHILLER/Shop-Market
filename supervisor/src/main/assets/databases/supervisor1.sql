-- ----------------------------
-- Table structure for SysConfig
-- ----------------------------
CREATE TABLE IF NOT EXISTS "SysConfig" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"Scope"  TEXT DEFAULT 0,
"Name"  TEXT,
"Value"  TEXT,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for User
-- ----------------------------
CREATE TABLE IF NOT EXISTS "User" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"BackOfficeId"  TEXT,
"UserName"  TEXT,
"Status"  INTEGER,
"IsSalesman"  INTEGER,
"IsDistributer"  INTEGER,
"IsCollector"  INTEGER,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for Visitor
-- ----------------------------
CREATE TABLE IF NOT EXISTS "Visitor" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"Name"  TEXT,
"Status"  INTEGER,
PRIMARY KEY ("UniqueId")
);

-- ----------------------------
-- Table structure for Location
-- ----------------------------
DROP TABLE IF EXISTS "main"."Location";
CREATE TABLE "Location" (
"CustomerId" TEXT,
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"Longitude"  REAL,
"Latitude"  REAL,
"Accuracy"  REAL,
"Speed"  REAL,
"Date"  INTEGER,
"EventType"  TEXT,
"Event"  TEXT,
"IsSend"  INTEGER,
"LastRetryTime"  INTEGER,
"Tracking"  INTEGER,
"Provider" TEST,
"LicensePolicy" INTEGER,
"Address"  TEXT,
"ActivityType" INTEGER,
"IsImportant" INTEGER,
"TourId" TEXT,
"TourRef" INTEGER,
"CompanyPersonnelId" TEXT,
"CompanyPersonnelName" TEXT,
"TimeOffset" INTEGER,
PRIMARY KEY ("UniqueId" ASC)
);


-- ----------------------------
-- Table structure for RegionAreaPoint
-- ----------------------------
DROP TABLE IF EXISTS "main"."RegionAreaPoint";
CREATE TABLE "RegionAreaPoint" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"Priority"  INTEGER,
"Latitude"  REAL,
"Longitude"  REAL,
"VisitPathTypeUniqueId"  TEXT COLLATE NOCASE ,
PRIMARY KEY ("UniqueId")
);

-- ----------------------------
-- Table structure for Printer
-- ----------------------------
DROP TABLE IF EXISTS "main"."Printer";
CREATE TABLE "Printer" (
"UniqueId"	TEXT NOT NULL COLLATE NOCASE,
"PrinterName"	TEXT,
"IsDefault" INTEGER,
"IsFound" INTEGER,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Add Table structure for TrackingLogModel
-- ----------------------------
DROP TABLE IF EXISTS "main"."TrackingLog";
CREATE TABLE "TrackingLog" (
"uniqueId"  TEXT NOT NULL,
"ENTime"  TEXT,
"FATime"  TEXT,
"EventType"  TEXT,
"Description"  TEXT,
"Trace"  TEXT,
"Time"  INTEGER,
"Level" TEXT,
PRIMARY KEY ("uniqueId")
);

-- ----------------------------
-- Table structure for UpdateLog
-- ----------------------------
DROP TABLE IF EXISTS "main"."UpdateLog";
CREATE TABLE "UpdateLog" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"Name"  TEXT,
"Date"  INTEGER,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for Customer
-- ----------------------------
DROP TABLE IF EXISTS "main"."SupervisorCustomer";
CREATE TABLE "SupervisorCustomer" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"CustomerName"  TEXT,
"CustomerCode"  TEXT,
"Latitude" NUMBER,
"Longitude" NUMBER,
"Address"  TEXT,
"Phone"  TEXT,
"StoreName"  TEXT,
"Mobile"  TEXT,
"NationalCode"  TEXT(2048),
"CustomerLevel"  TEXT COLLATE NOCASE ,
"CustomerActivity"  TEXT COLLATE NOCASE ,
"CustomerCategory"  TEXT COLLATE NOCASE ,
"DealerId"  TEXT COLLATE NOCASE ,
"DealerName"  TEXT COLLATE NOCASE ,
"PathTitle"  TEXT COLLATE NOCASE ,
PRIMARY KEY ("UniqueId" ASC)
);