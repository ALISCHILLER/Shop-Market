-- ----------------------------
-- ALTER TABLE Customer
-- ----------------------------
--ALTER TABLE Msl ADD COLUMN [DataOwnerCenterIds]  TEXT  NULL,
--ADD COLUMN [FromDate]  TEXT  NULL,
--ADD COLUMN [ToDate]  TEXT  NULL;




-- ----------------------------
-- Table structure for CustomerInventory
-- ----------------------------
CREATE TABLE "MslProductPattern" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"CustomerLevelId"  TEXT COLLATE NOCASE ,
"ProductId"  TEXT COLLATE NOCASE ,
"IsForce"  BIT,

PRIMARY KEY ("UniqueId" ASC)
);

-- ALTER TABLE Customer
-- ----------------------------
ALTER TABLE EmphaticProduct ADD COLUMN [IsEmphasis]  BIT  NULL;


