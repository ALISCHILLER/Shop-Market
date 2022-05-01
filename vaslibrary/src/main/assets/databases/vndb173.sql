
-- ----------------------------
-- ALTER TABLE Customer
-- ----------------------------
ALTER TABLE Customer ADD COLUMN [CodeNaghsh]  TEXT  NULL;



---- ----------------------------
---- Table PinRequest_ for CustomerNotAllowProduct
---- ----------------------------
CREATE TABLE "CustomerNotAllowProduct" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"ProductId" TEXT,
"CustomerId" TEXT,
PRIMARY KEY ("UniqueId" ASC)
);