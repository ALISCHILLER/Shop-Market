ALTER TABLE DiscountOrderPrize ADD COLUMN OrderDiscountRef INTEGER NULL;
ALTER TABLE DiscountEvcPrize ADD COLUMN OrderDiscountRef INTEGER NULL;

DROP INDEX IF EXISTS "main"."CustRefIndex";
DROP INDEX IF EXISTS "main"."OrderTypeIndex";
CREATE INDEX "main"."DiscountConditionOrderTypeIndex"
ON "DiscountConditionSDS4_19" ("DiscountRef" ASC, "OrderType" ASC);
CREATE INDEX "main"."DiscountConditionCustomerIndex"
ON "DiscountConditionSDS4_19" ("DiscountRef" ASC, "CustRef" ASC);
CREATE INDEX "main"."DiscountConditionDCRefIndex"
ON "DiscountConditionSDS4_19" ("DiscountRef" ASC, "DCRef" ASC);


