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


---CREATE TABLE CustomerGroupSimilarProductsalesReport
CREATE TABLE "CustomerGroupSimilarProductsalesReport" (
"uniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"ProductName"  TEXT ,
"ProductCode"  TEXT  ,
"customerUniqueId"  TEXT,
PRIMARY KEY ("uniqueId" ASC));

---CREATE TABLE CustomerSumMoneyAndWeightReport
CREATE TABLE "CustomerSumMoneyAndWeightReport" (
"uniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"Money_Sum"  TEXT ,
"Weight_Sum"  TEXT  ,
"CustomerId"  TEXT,
PRIMARY KEY ("uniqueId" ASC));