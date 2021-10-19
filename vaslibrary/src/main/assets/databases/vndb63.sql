-- ----------------------------
--  Alter view Image change IsLocal type to Integer. related to issue: DMC-38094
-- ----------------------------
CREATE TABLE "ImageTemp" (
	"UniqueId" TEXT NOT NULL COLLATE NOCASE,
	"TokenId" TEXT NOT NULL COLLATE NOCASE,
	"ImageFileName" TEXT NOT NULL,
	"IsDefault" INTEGER DEFAULT 1,
	"ImageType" TEXT,
	"IsLocal" INTEGER,
	PRIMARY KEY ("UniqueId" ASC)
);

INSERT INTO ImageTemp SELECT * FROM	Image;

DROP TABLE Image;

CREATE TABLE "Image" (
	"UniqueId" TEXT NOT NULL COLLATE NOCASE,
	"TokenId" TEXT NOT NULL COLLATE NOCASE,
	"ImageFileName" TEXT NOT NULL,
	"IsDefault" INTEGER DEFAULT 1,
	"ImageType" TEXT,
	"IsLocal" INTEGER,
	PRIMARY KEY ("UniqueId" ASC)
);

INSERT INTO Image SELECT * FROM	ImageTemp;

DROP TABLE ImageTemp;

