-- ----------------------------
--  Alter table OrderPrize related to issue: DMC-42051
-- ----------------------------
@UNCHECKED
ALTER TABLE OrderPrize ADD COLUMN CallOrderId TEXT;