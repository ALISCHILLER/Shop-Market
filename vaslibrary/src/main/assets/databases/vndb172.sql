
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