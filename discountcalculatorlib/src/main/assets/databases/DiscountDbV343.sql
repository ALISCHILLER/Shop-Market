--Return Vnlite

-- ----------------------------
-- Table structure for EVCTempReturnItemVNLite
-- ----------------------------
DROP TABLE IF EXISTS "main"."EVCTempReturnItemVNLite";
CREATE TABLE [EVCTempReturnItemVNLite]([_id]  INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL ,
    [EVCId]  INTEGER  NULL ,[RowOrder]  INTEGER  NULL ,
    [GoodsRef]  INTEGER  NULL ,[UnitQty]  DECIMAL  NULL ,
    [CPriceRef]  INTEGER  NULL ,[UnitRef]  INTEGER  NULL ,[UnitCapasity]  DECIMAL  NULL ,
    [TotalQty]  DECIMAL  NULL ,[AmountNut]  DECIMAL  NULL ,[Discount]  DECIMAL  NULL ,[Amount]  DECIMAL  NULL ,
    [PrizeType]  DECIMAL  NULL ,[AddAmount]  DECIMAL  NULL ,[UserPrice]  DECIMAL  NULL ,[CustPrice]  DECIMAL  NULL ,
    [PriceId]  INTEGER  NULL ,[Charge]  DECIMAL  NULL ,[Tax]  DECIMAL  NULL ,[DetailId]  int  NULL ,
    [ReduceOfQty]  DECIMAL  NULL );

ALTER TABLE EVCItemVnLite ADD CPriceRef  INT NULL;

-- ----------------------------
-- Table structure for EVCTempReturnItemSummaryVNLite
-- ----------------------------
DROP TABLE IF EXISTS "main"."EVCTempReturnItemSummaryVNLite";
CREATE TABLE [EVCTempReturnItemSummaryVNLite](
    [_id]  INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL ,
    [EVCId]  INTEGER  NULL ,
    [DetailId] INT NULL,
    [RowOrder]  INTEGER  NULL ,
    [GoodsRef]  INTEGER  NULL ,
    [UnitQty]  DECIMAL  NULL ,
    [CPriceRef]  INTEGER  NULL ,
    [UnitRef]  INTEGER  NULL ,
    [UnitCapasity]  DECIMAL  NULL ,
    [TotalQty]  DECIMAL  NULL ,
    [AmountNut]  DECIMAL  NULL ,
    [Discount]  DECIMAL  NULL ,
    [Amount]  DECIMAL  NULL ,
    [PrizeType]  DECIMAL  NULL ,
    [SupAmount]  DECIMAL  NULL ,
    [AddAmount]  DECIMAL  NULL ,
    [UserPrice]  DECIMAL  NULL ,
    [CustPrice]  DECIMAL  NULL ,
    [PriceId]  INTEGER  NULL ,
    [Charge]  DECIMAL  NULL ,
    [Tax]  DECIMAL  NULL  );

-- ----------------------------
-- Table structure for EVCTempReturnItemSummaryFinalVNLite
-- ----------------------------
DROP TABLE IF EXISTS "main"."EVCTempReturnItemSummaryFinalVNLite";
CREATE TABLE [EVCTempReturnItemSummaryFinalVNLite](
    [_id]  INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL ,
    [EVCId]  INTEGER  NULL ,
    [RowOrder]  INTEGER  NULL ,
    [GoodsRef]  INTEGER  NULL ,
    [UnitQty]  DECIMAL  NULL ,
    [TotalQty]  DECIMAL  NULL ,
    [AmountNut]  DECIMAL  NULL ,
    [Discount]  DECIMAL  NULL ,
    [RemainDiscount] DECIMAL  NULL ,
    [RemainAddAmount] DECIMAL  NULL ,
    [Amount]  DECIMAL  NULL ,
    [PrizeType]  DECIMAL  NULL ,
    [AddAmount]  DECIMAL  NULL ,
    [CustPrice]  DECIMAL  NULL ,
    [UserPrice]  DECIMAL  NULL ,
    [Charge]  DECIMAL  NULL ,
    [Tax]  DECIMAL  NULL ,
    [PriceId]  INTEGER  NULL ,
    [DetailId] INT NULL,
    [CPriceRef]  INTEGER  NULL
 );

-- ----------------------------
-- Table structure for DisSaleVnLt
-- ----------------------------
DROP TABLE IF EXISTS "main"."DiscountDisSaleVnLt";
CREATE TABLE [DiscountDisSaleVnLt](
    [DiscountAmount]  DECIMAL  NULL ,
    [AddAmount]  DECIMAL  NULL ,
    [PrizeAmount]  DECIMAL  NULL ,
    [PrizeQty]  DECIMAL  NULL ,
    [SellDetailPromotionDetailId] INT NULL,
    [SellDetailId] INT NULL,
    [SellId] INT NULL,
    [PromotionId] INT NULL,
    [PromotionDetailId]INT NULL
);

-- ----------------------------
-- Table structure for TempPromotion
-- ----------------------------
DROP TABLE IF EXISTS "main"."TempPromotion";
CREATE TABLE [TempPromotion](
    [PromotionId] INT NULL
);
