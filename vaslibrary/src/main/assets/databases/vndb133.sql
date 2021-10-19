-- ----------------------------
-- Table structure for TargetReviewReportView
-- ----------------------------
DROP TABLE IF EXISTS "TargetReviewReportView";
CREATE TABLE "TargetReviewReportView" (
  "UniqueId" text NOT NULL,
  "CalcDate" TEXT,
  "Condition" TEXT,
  "Type" TEXT,
  "Title" TEXT,
  "Target" real,
  "TargettoDate" real,
  "AchievementDate" real,
  "Achievement" real,
  "DailyPlan" real,
  "AchievementStimate" real,
  "CalculationPeriodId" integer,
  PRIMARY KEY ("UniqueId")
