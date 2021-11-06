-- ----------------------------
--  Alter Table Customer Add Description Field
-- ----------------------------
Alter table Customer Add COLUMN Description Text;

-- ----------------------------
--  Alter Table CustomerCallInvoice and CustomerCallOrder Add PinCode3 and PinCode4 Field
-- ----------------------------
ALTER TABLE CustomerCallInvoice ADD COLUMN [PinCode3]  TEXT  "";
ALTER TABLE CustomerCallInvoice ADD COLUMN [PinCode4]  TEXT  "";

ALTER TABLE CustomerCallOrder ADD COLUMN [PinCode3]  TEXT  "";
ALTER TABLE CustomerCallOrder ADD COLUMN [PinCode4]  TEXT  "";