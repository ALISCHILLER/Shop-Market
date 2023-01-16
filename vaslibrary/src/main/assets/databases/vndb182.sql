
-- ----------------------------
--  Alter EmphaticProduct
-- ----------------------------
alter table EmphaticProduct add column CustomerActivityUniqueIds TEXT;
alter table EmphaticProduct add column CustomerCategoryUniqueIds TEXT;
alter table EmphaticProduct add column CustomerLevelUniqueIds TEXT;



---CREATE TABLE CustomerXMounthSaleReport
DROP TABLE IF EXISTS "main"."CustomerXMounthSaleReport";
CREATE TABLE "CustomerXMounthSaleReport" (
"uniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"ProductName"  TEXT ,
"ProductCode"  TEXT  ,
"customerCode"  TEXT,
PRIMARY KEY ("uniqueId" ASC));


---CREATE TABLE CustomerGroupSimilarProductsalesReport
DROP TABLE IF EXISTS "main"."CustomerGroupSimilarProductsalesReport";
CREATE TABLE "CustomerGroupSimilarProductsalesReport" (
"uniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"ProductName"  TEXT ,
"ProductCode"  TEXT  ,
"customerCode"  TEXT,
PRIMARY KEY ("uniqueId" ASC));

---CREATE TABLE CustomerSumMoneyAndWeightReport
DROP TABLE IF EXISTS "main"."CustomerSumMoneyAndWeightReport";
CREATE TABLE "CustomerSumMoneyAndWeightReport" (
"uniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"Money_Sum"  TEXT ,
"Weight_Sum"  TEXT  ,
"CustomerCode"  TEXT,
PRIMARY KEY ("uniqueId" ASC));

---CREATE TABLE CheckCustomerCredit
CREATE TABLE "CheckCustomerCredit" (
"uniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"customerBackOfficeCode"  TEXT ,
"CustomerCreditLimit"  DECIMAL  ,
"customerUsedCredit"  DECIMAL  ,
"customerRemainCredit"  DECIMAL  ,
"customerCode"  TEXT,
PRIMARY KEY ("uniqueId" ASC));
