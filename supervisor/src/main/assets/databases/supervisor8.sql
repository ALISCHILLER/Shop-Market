-- ----------------------------
-- Table PinRequest_ for Customer
-- ----------------------------

CREATE TABLE "PinRequest_" (
"UniqueId" INTEGER PRIMARY KEY AUTOINCREMENT ,
"customerId"  TEXT ,
"pinType"  TEXT  COLLATE NOCASE ,
"dealerId" TEXT ,
"customerName"  TEXT ,
"dealerName" TEXT,
"date" DATE
);
