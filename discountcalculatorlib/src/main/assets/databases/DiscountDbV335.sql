PRAGMA foreign_keys = OFF;
CREATE INDEX IF NOT EXISTS main.index_DiscountId ON DiscountSDS4_19(Id);
CREATE INDEX IF NOT EXISTS main.index_DiscountProduct ON DiscountSDS4_19(goodsRef);
CREATE INDEX IF NOT EXISTS main.index_ProductId ON DiscountProduct(ProductId);
CREATE INDEX IF NOT EXISTS main.index_DisSaleHdrRef ON DiscountDisSaleSDS(HdrRef);
CREATE INDEX IF NOT EXISTS main.index_DisSaleDisRef ON DiscountDisSaleSDS(DisRef);
CREATE INDEX IF NOT EXISTS main.index_DiscountProductUnitRef ON DiscountProductUnit(BackOfficeId);
CREATE INDEX IF NOT EXISTS main.index_DiscountProductProductId ON DiscountProductUnit(ProductId);
