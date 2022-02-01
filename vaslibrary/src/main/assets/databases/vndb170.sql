-- ----------------------------
-- Table PictureSubjectZar for Customer
-- ----------------------------

CREATE TABLE "PictureSubjectZar" (
"UniqueId" TEXT NOT NULL COLLATE NOCASE,
"subSystemTypeUniqueId" TEXT COLLATE NOCASE,
"subjectTitle"  TEXT  ,
"centerUniqueIds"TEXT   ,
"customerCategoryUniqueIds"  TEXT  ,
"customerActivityUniqueIds"  TEXT ,
"demandTypeUniqueId" TEXT COLLATE NOCASE,
"subjectUniqueId" TEXT COLLATE NOCASE,
PRIMARY KEY ("UniqueId" ASC)
);
-- ----------------------------
-- Table structure for PictureCustomer
-- ----------------------------
DROP TABLE IF EXISTS "main"."PictureCustomer";
CREATE TABLE "PictureCustomer" (
"UniqueId"  TEXT NOT NULL,
"PictureSubjectId"  TEXT NOT NULL,
"CustomerId"  TEXT NOT NULL,
"DemandTypeUniqueId"  TEXT,
"DemandType"  INTEGER,
"FileId" TEXT,
"NoPictureReason"  TEXT DEFAULT NULL,
PRIMARY KEY ("UniqueId" ASC),
CONSTRAINT "fkey0" FOREIGN KEY ("CustomerId") REFERENCES "Customer" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE
);

alter table PictureCustomer add column Title TEXT;
-- ----------------------------
-- View structure for PictureCustomerView
-- ----------------------------
DROP VIEW IF EXISTS "main"."PictureCustomerView";
CREATE VIEW "PictureCustomerView" AS
SELECT
	PictureCustomer.UniqueId AS UniqueId,
	PictureCustomer.PictureSubjectId AS PictureSubjectId,
	PictureCustomer.CustomerId AS CustomerId,
	PictureCustomer.DemandTypeUniqueId AS DemandTypeUniqueId,
	PictureCustomer.DemandType AS DemandType,
	PictureCustomer.NoPictureReason AS NoPictureReason,
	PictureCustomer.Title AS Title,
	ifnull(
		PictureCustomerHistory.UniqueId,
		'0'
	) AS AlreadyTaken,
	group_concat(PictureFile.FileId, ':') AS FileIds,
	count(PictureFile.FileId) AS FileCount
FROM
	PictureCustomer
LEFT JOIN PictureFile ON PictureFile.PictureSubjectId = PictureCustomer.PictureSubjectId
AND PictureFile.CustomerId = PictureCustomer.CustomerId
LEFT JOIN PictureCustomerHistory ON PictureCustomerHistory.CustomerId = PictureCustomer.CustomerId
AND PictureCustomerHistory.PictureSubjectId = PictureCustomer.PictureSubjectId
GROUP BY
	PictureCustomer.UniqueId;
