-- ----------------------------
-- Table PinRequest_ for Customer
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