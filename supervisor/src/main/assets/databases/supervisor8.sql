-- ----------------------------
-- Table PinRequest_ for PinRequest_
-- ----------------------------

CREATE TABLE "PinRequest_" (
"UniqueId" TEXT PRIMARY KEY  ,
"customerId"  TEXT ,
"pinType"  TEXT  COLLATE NOCASE ,
"pinName"  TEXT  COLLATE NOCASE ,
"dealerId" TEXT ,
"customerName"  TEXT ,
"dealerName" TEXT,
"date" TEXT,
"Status" TEXT,
"customer_call_order" TEXT
);
-- ----------------------------
-- Table PinRequest_ for CustomerPin
-- ----------------------------
CREATE TABLE "CustomerPin" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"CustomerUniqueId"TEXT,
"CustomerCode" TEXT,
"CustomerName"  TEXT,
"PinDate"  DATE,
"PinPDate"  TEXT,
"DealerName"  TEXT,
"Pin1"  TEXT,
"Pin2"  TEXT,
"Pin3"  TEXT,
"Pin4"  TEXT,
PRIMARY KEY ("UniqueId" ASC)
);
-- ----------------------------
-- Table PinRequest_ for NewsData_
-- ----------------------------
CREATE TABLE "NewsData_" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"title" TEXT,
"body" TEXT,
"subSystemType"  TEXT,
"centerUniqueIds"  TEXT,
"publishDate"  TEXT,
"publishPDate"  TEXT,
PRIMARY KEY ("UniqueId" ASC)
);