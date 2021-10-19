-- ----------------------------
--  Alter CustomerModel related to issue: NGT-3738
-- ----------------------------
alter table Customer add column IgnoreLocation INTEGER;
alter table Customer add column ParentCustomerId INTEGER;

-- ----------------------------
--  Alter CustomerModel related to issue: NGT-3215
-- ----------------------------
alter table Payment add column SayadNumber TEXT;