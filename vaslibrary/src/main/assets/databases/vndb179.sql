alter table Customer add column IsZarShopCustomer TEXT;
alter table NewsData_ add column IsRead BIT;


--- CustomerGroupLastSalesReport
CREATE TABLE "CustomerGroupLastSalesReport" (
 "UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
 "customerUniqeID" TEXT,
 "productBackOfficeCode" TEXT,
 "customerGroup" TEXT,
 "customerGroupTXT" TEXT,
 "customerActivity" TEXT,
 "customerActivityTXT" TEXT,
 "netWeight" DECIMAL,
 "netCount_CA" DECIMAL,
 "netCount_EA" DECIMAL,
 "productGroupName" TEXT,
 "productGroupCode" INTEGER,
 "weightProductGroup" DECIMAL,
 "customerCode" TEXT,
 "reshteh" DECIMAL,
 "formi" DECIMAL,
 "jumbo" DECIMAL,
 "ashianeh" DECIMAL,
 "lazania" DECIMAL,
 "podrKeik" DECIMAL,
 "reshtehAsh" DECIMAL,
 "ard" DECIMAL,
 "haftGhaleh" DECIMAL,
 "vegan" DECIMAL,
 "protoeen" DECIMAL,
 "lastSaleProductGroup" TEXT,
PRIMARY KEY ("UniqueId" ASC )
);