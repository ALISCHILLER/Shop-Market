ALTER TABLE DiscountCustomer ADD COLUMN [CustChequeRetQty]  INTEGER  NULL;

ALTER TABLE EVCTempDiscountSDS4_19 ADD COLUMN [MinCustChequeRetQty]  INTEGER  NULL;
ALTER TABLE EVCTempDiscountSDS4_19 ADD COLUMN [MaxCustChequeRetQty]  INTEGER  NULL;
ALTER TABLE EVCTempCustomerSDS ADD COLUMN [CustChequeRetQty]  INTEGER  NULL;
ALTER TABLE EVCItemSDS ADD COLUMN [EvcItemOtherDiscount]  DECIMAL  NULL;
ALTER TABLE EVCItemSDS ADD COLUMN [EvcItemOtherAddition]  DECIMAL  NULL;

ALTER TABLE EVCHeaderSDS ADD COLUMN [OtherDiscount]  DECIMAL  NULL;
ALTER TABLE EVCHeaderSDS ADD COLUMN [OtherAddition]  DECIMAL  NULL;

ALTER TABLE EVCTempReturnItemSummarySDS ADD COLUMN [OtherDiscount]  DECIMAL  NULL;
ALTER TABLE EVCTempReturnItemSummarySDS ADD COLUMN [OtherAddition]  DECIMAL  NULL;

ALTER TABLE EVCTempReturnItemSummaryFinalSDS ADD COLUMN [RemainOtherAddition]  DECIMAL  NULL;
ALTER TABLE EVCTempReturnItemSummaryFinalSDS ADD COLUMN [RemainOtherDiscount]  DECIMAL  NULL;
ALTER TABLE EVCTempReturnItemSummaryFinalSDS ADD COLUMN [OtherAddition]  DECIMAL  NULL;
ALTER TABLE EVCTempReturnItemSummaryFinalSDS ADD COLUMN [OtherDiscount]  DECIMAL  NULL;

ALTER TABLE DiscountProductUnit ADD COLUMN [ForRetSale]  INTEGER  NULL;

-- ----------------------------
-- Table structure for EVCTempReturnItemSDS
-- ----------------------------
DROP TABLE IF EXISTS "main"."EVCTempReturnItemSDS";
CREATE TABLE "EVCTempReturnItemSDS" (
"_id"  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
"EVCId"  INTEGER,
"RowOrder"  INTEGER,
"GoodsRef"  INTEGER,
"UnitQty"  DECIMAL,
"CPriceRef"  INTEGER,
"UnitRef"  INTEGER,
"UnitCapasity"  DECIMAL,
"TotalQty"  DECIMAL,
"AmountNut"  DECIMAL,
"Discount"  DECIMAL,
"Amount"  DECIMAL,
"PrizeType"  DECIMAL,
"SupAmount"  DECIMAL,
"AddAmount"  DECIMAL,
"AddAmount1"  DECIMAL,
"AddAmount2"  DECIMAL,
"Charge"  DECIMAL,
"Tax"  DECIMAL,
"DisAmount1"  DECIMAL,
"DisAmount2"  DECIMAL,
"DisAmount3"  DECIMAL,
"FreeReasonId"  INTEGER,
"OtherDiscount"  DECIMAL,
"OtherAddition"  DECIMAL,
"CustPrice"  DECIMAL,
"UserPrice"  DECIMAL,
"PriceRef"  INTEGER
);

DROP TABLE IF EXISTS "main"."DiscountDisAcc";
CREATE TABLE "DiscountDisAcc" (
"Id"  INTEGER NOT NULL,
"IsDiscount"  INTEGER,
"IsDefault"  INTEGER,
PRIMARY KEY ("Id")
);

DROP TABLE IF EXISTS "main"."DiscountEvcPrize";
CREATE TABLE "DiscountEvcPrize" (
"Id"  INTEGER NOT NULL,
"EvcRef"  INTEGER,
"DiscountRef"  INTEGER,
"GoodsRef"  INTEGER,
"PrizeQty"  INTEGER,
"QtyUnit"  INTEGER,
PRIMARY KEY ("Id")
);

DROP TABLE IF EXISTS "main"."DiscountEvcPrizePackage";
CREATE TABLE "DiscountEvcPrizePackage" (
"Id"  INTEGER NOT NULL,
"EvcRef"  INTEGER,
"DiscountRef"  INTEGER,
"MainGoodsPackageItemRef"  INTEGER,
"ReplaceGoodsPackageItemRef"  INTEGER,
"PrizeQty"  INTEGER,
"PrizeCount"  INTEGER,
PRIMARY KEY ("Id")
);

DROP TABLE IF EXISTS "main"."DiscountOrderPrize";
CREATE TABLE "DiscountOrderPrize" (
"Id"  INTEGER NOT NULL,
"OrderRef"  INTEGER,
"DiscountRef"  INTEGER,
"GoodsRef"  INTEGER,
"Qty"  INTEGER,
"SaleQty"  INTEGER,
"IsAutomatic"  INTEGER,
PRIMARY KEY ("Id")
);

DROP TABLE IF EXISTS "main"."EVCTempOrderPrizeSDS";
CREATE TABLE "EVCTempOrderPrizeSDS" (
"Id"  INTEGER NOT NULL,
"OrderRef"  INTEGER,
"DiscountRef"  INTEGER,
"GoodsRef"  INTEGER,
"Qty"  INTEGER,
"IsDeleted"  INTEGER,
"SaleQty"  INTEGER,
"IsAutomatic"  INTEGER,
PRIMARY KEY ("Id")
);





