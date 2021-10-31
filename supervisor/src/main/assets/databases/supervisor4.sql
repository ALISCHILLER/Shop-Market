-- ----------------------------
-- Questionnaire: Alter SupervisorCustomer Table
-- ----------------------------
alter table SupervisorCustomer add column CenterId COLLATE NOCASE;
alter table SupervisorCustomer add column CityId TEXT COLLATE NOCASE;
alter table SupervisorCustomer add column CustomerActivityId TEXT COLLATE NOCASE;
alter table SupervisorCustomer add column CustomerCategoryId TEXT COLLATE NOCASE;
alter table SupervisorCustomer add column CustomerLevelId TEXT COLLATE NOCASE;
alter table SupervisorCustomer add column AreaId TEXT COLLATE NOCASE;
alter table SupervisorCustomer add column StateId TEXT COLLATE NOCASE;

-- ----------------------------
-- Table structure for QuestionnaireHeader
-- ----------------------------
DROP TABLE IF EXISTS "main"."QuestionnaireHeader";
CREATE TABLE "QuestionnaireHeader" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"Title"  TEXT,
"FromDate"  INTEGER,
"ToDate"  INTEGER,
"DemandTypeUniqueId"  TEXT COLLATE NOCASE ,
"CenterUniqueIds"  TEXT COLLATE NOCASE ,
"SaleZoneUniqueIds"  TEXT COLLATE NOCASE ,
"StateUniqueIds"  TEXT COLLATE NOCASE ,
"CityUniqueIds"  TEXT COLLATE NOCASE ,
"CustomerActivityUniqueIds"  TEXT COLLATE NOCASE ,
"CustomerCategoryUniqueIds"  TEXT COLLATE NOCASE ,
"CustomerLevelUniqueIds"  TEXT COLLATE NOCASE ,
"SaleOfficeUniqueIds"  TEXT COLLATE NOCASE ,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for QuestionnaireLine
-- ----------------------------
DROP TABLE IF EXISTS "main"."QuestionnaireLine";
CREATE TABLE "QuestionnaireLine" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"QuestionnaireUniqueId"  TEXT COLLATE NOCASE ,
"Title"  TEXT,
"QuestionnaireLineTypeUniqueId"  TEXT COLLATE NOCASE ,
"HasAttachment"  INTEGER,
"NumberOfOptions"  INTEGER,
"AttachmentTypeUniqueId"  TEXT COLLATE NOCASE ,
"QuestionGroupUniqueId"  TEXT COLLATE NOCASE ,
"RowIndex"  INTEGER,
"Validators"  TEXT,
PRIMARY KEY ("UniqueId" ASC),
CONSTRAINT "linetoquestinoiure" FOREIGN KEY ("QuestionnaireUniqueId") REFERENCES "QuestionnaireHeader" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for QuestionnaireLineOption
-- ----------------------------
DROP TABLE IF EXISTS "main"."QuestionnaireLineOption";
CREATE TABLE "QuestionnaireLineOption" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"QuestionnaireLineUniqueId"  TEXT COLLATE NOCASE ,
"Title"  TEXT,
"RowIndex"  INTEGER,
"QuestionGroupUniqueId"  TEXT COLLATE NOCASE ,
"AnswerAttachmentUniqueId" TEXT COLLATE NOCASE ,
PRIMARY KEY ("UniqueId" ASC),
CONSTRAINT "optionetoline" FOREIGN KEY ("QuestionnaireLineUniqueId") REFERENCES "QuestionnaireLine" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for QuestionnaireHistory
-- ----------------------------
DROP TABLE IF EXISTS "main"."QuestionnaireHistory";
CREATE TABLE "QuestionnaireHistory" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"QuestionnaireId"  TEXT COLLATE NOCASE ,
"CustomerId"  TEXT COLLATE NOCASE ,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for QuestionnaireCustomer
-- ----------------------------
DROP TABLE IF EXISTS "main"."QuestionnaireCustomer";
CREATE TABLE "QuestionnaireCustomer" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"QuestionnaireId"  TEXT COLLATE NOCASE ,
"CustomerId"  TEXT COLLATE NOCASE ,
"DemandTypeId"  TEXT COLLATE NOCASE ,
"DemandType"  INTEGER,
"NoAnswerReason"  TEXT,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- Table structure for QuestionnaireAnswer
-- ----------------------------
DROP TABLE IF EXISTS "main"."QuestionnaireAnswer";
CREATE TABLE "QuestionnaireAnswer" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"CustomerId"  TEXT NOT NULL COLLATE NOCASE ,
"QuestionnaireId"  TEXT COLLATE NOCASE ,
"QuestionnaireLineId"  TEXT COLLATE NOCASE ,
"Value"  TEXT,
"AttachmentId"  TEXT COLLATE NOCASE ,
PRIMARY KEY ("UniqueId" ASC)
);

-- ----------------------------
-- View structure for QuestionnaireCustomerView
-- ----------------------------
DROP VIEW IF EXISTS "main"."QuestionnaireCustomerView";
CREATE VIEW "QuestionnaireCustomerView" AS
SELECT
	QuestionnaireHeader.UniqueId AS QuestionnaireId,
	QuestionnaireHeader.Title,
	QuestionnaireCustomer.CustomerId,
	QuestionnaireCustomer.DemandTypeId,
	QuestionnaireCustomer.DemandType,
	QuestionnaireCustomer.UniqueId,
	QuestionnaireAnswer.AttachmentId,
	CASE
WHEN QuestionnaireAnswer.QuestionnaireId IS NULL THEN
	'0'
ELSE
	'1'
END AS HasAnswer,
 CASE
WHEN QuestionnaireHistory.UniqueId IS NULL THEN
	'0'
ELSE
	'1'
END AS AlreadyAnswered,
 QuestionnaireCustomer.NoAnswerReason
FROM
	QuestionnaireCustomer
INNER JOIN QuestionnaireHeader ON QuestionnaireHeader.UniqueId == QuestionnaireCustomer.QuestionnaireId
LEFT JOIN QuestionnaireAnswer ON QuestionnaireAnswer.CustomerId == QuestionnaireCustomer.CustomerId
AND QuestionnaireAnswer.QuestionnaireId == QuestionnaireCustomer.QuestionnaireId
LEFT JOIN QuestionnaireHistory ON QuestionnaireHistory.CustomerId == QuestionnaireCustomer.CustomerId
AND QuestionnaireHistory.QuestionnaireId == QuestionnaireCustomer.QuestionnaireId
GROUP BY
	QuestionnaireCustomer.UniqueId;

-- ----------------------------
-- Table structure for Image
-- ----------------------------
CREATE TABLE "Image" (
	"UniqueId" TEXT NOT NULL COLLATE NOCASE,
	"TokenId" TEXT NOT NULL COLLATE NOCASE,
	"ImageFileName" TEXT NOT NULL,
	"IsDefault" INTEGER DEFAULT 1,
	"ImageType" TEXT,
	"IsLocal" INTEGER,
	"LastUpdate" INTEGER,
	PRIMARY KEY ("UniqueId" ASC)
);


