 alter table Customer add column IsZarShopCustomer TEXT;
alter table NewsData_ add column IsRead BIT;


--- CustomerGroupLastSalesReport
CREATE TABLE "CustomerGroupLastSalesReport" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"ProductBackOfficeCode" TEXT,
"CustomerGroup" TEXT,
"CustomerCode" TEXT,
"CustomerGroupTXT" TEXT,
"CustomerActivity" TEXT,
"CustomerActivityTXT" TEXT,
"LastSaleProductGroup" TEXT,
"LastSaleProductGroup_Reshteh" TEXT,
"LastSaleProductGroup_Formi" TEXT,
"LastSaleProductGroup_Jumbo" TEXT,
"LastSaleProductGroup_Ashianeh" TEXT,
"LastSaleProductGroup_Lazania" TEXT,
"LastSaleProductGroup_PodrKeik" TEXT,
"LastSaleProductGroup_ReshtehAsh" TEXT,
"LastSaleProductGroup_Ard" TEXT,
"LastSaleProductGroup_HaftGhaleh" TEXT,
"LastSaleProductGroup_vegan" TEXT,
"LastSaleProductGroup_protoeen" TEXT,
PRIMARY KEY ("UniqueId" ASC)
);