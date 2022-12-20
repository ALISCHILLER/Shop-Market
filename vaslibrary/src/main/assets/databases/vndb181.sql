-- ----------------------------
--  Alter Table Customer Add Description Field
-- ----------------------------
Alter table Customer Add COLUMN DegreeStar INT;



---CREATE TABLE CustomerXMounthSaleReport
CREATE TABLE "CustomerXMounthSaleReport" (
"uniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"ProductName"  TEXT ,
"ProductCode"  TEXT  ,
"customerUniqueId"  TEXT,
PRIMARY KEY ("uniqueId" ASC));