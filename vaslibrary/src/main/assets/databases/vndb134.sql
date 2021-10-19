-- ----------------------------
--  Alter CustomerOldInvoiceDetail related to issue: NGT-3892
-- ----------------------------
alter table CustomerOldInvoiceDetail add column Dis1 REAL;
alter table CustomerOldInvoiceDetail add column Dis2 REAL;
alter table CustomerOldInvoiceDetail add column Dis3 REAL;
alter table CustomerOldInvoiceDetail add column OtherDiscount REAL;
alter table CustomerOldInvoiceDetail add column Add1 REAL;
alter table CustomerOldInvoiceDetail add column Add2 REAL;
alter table CustomerOldInvoiceDetail add column OtherAddition REAL;
alter table CustomerOldInvoiceDetail add column PreviousRetQty REAL;