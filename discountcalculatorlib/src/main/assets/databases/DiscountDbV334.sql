/*
Navicat SQLite Data Transfer

Source Server         : DiscountDbV3
Source Server Version : 31202
Source Host           : :0

Target Server Type    : SQLite
Target Server Version : 31202
File Encoding         : 65001

Date: 2018-03-03 15:15:17
*/

PRAGMA foreign_keys = OFF;

-- ----------------------------
-- Table structure for DiscountCustomerBoGroup
-- ----------------------------
DROP TABLE IF EXISTS "main"."DiscountCustomerBoGroup";
CREATE TABLE "DiscountCustomerBoGroup" (
"Id"  INTEGER NOT NULL,
"ParentRef"  INTEGER,
"NLeft"  INTEGER,
"NRight"  INTEGER,
"NLevel"  INTEGER,
PRIMARY KEY ("Id")
);
ALTER TABLE DiscountCustomer ADD COLUMN CustGroupRef INTEGER;
ALTER TABLE EVCTempCustomerSDS ADD COLUMN CustGroupRef INTEGER;