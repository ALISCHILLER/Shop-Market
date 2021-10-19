-- ----------------------------
-- Alter Table structure for CustomerCardex
-- ----------------------------
alter table CustomerCardex add column BankName text;
alter table CustomerCardex add column ChqDate text;

-- ----------------------------
-- Alter Table structure for CustomerCardexTemp
-- ----------------------------
alter table CustomerCardexTemp add column BankName text;
alter table CustomerCardexTemp add column ChqDate text;
