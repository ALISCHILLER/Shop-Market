-- ----------------------------
-- Questionnaire: Alter SupervisorCustomer Table
-- ----------------------------
alter table SupervisorCustomer add column EconomicCode TEXT COLLATE NOCASE;
alter table SupervisorCustomer add column CodeNaghsh TEXT COLLATE NOCASE;
alter table SupervisorCustomer add column NewCodeNaghsh TEXT COLLATE NOCASE;