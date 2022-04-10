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
